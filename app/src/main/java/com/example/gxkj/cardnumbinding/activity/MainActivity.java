package com.example.gxkj.cardnumbinding.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gxkj.cardnumbinding.R;
import com.example.gxkj.cardnumbinding.app.AppApplication;
import com.example.gxkj.cardnumbinding.app.AppConstant;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.MtmData;
import com.example.gxkj.cardnumbinding.contract.UploadCardNumContract;
import com.example.gxkj.cardnumbinding.model.UploadCardNumModel;
import com.example.gxkj.cardnumbinding.presenter.UploadCardNumPresenter;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.SPUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cc.lotuscard.ILotusCallBack;
import cc.lotuscard.LotusCardDriver;
import cc.lotuscard.LotusCardParam;

import static cc.lotuscard.LotusCardDriver.m_InEndpoint;
import static cc.lotuscard.LotusCardDriver.m_OutEndpoint;
import static cc.lotuscard.LotusCardDriver.m_UsbDeviceConnection;

public class MainActivity extends BaseActivity<UploadCardNumPresenter, UploadCardNumModel> implements ILotusCallBack, UploadCardNumContract.View {

    private LotusCardDriver mLotusCardDriver;
    private UsbManager usbManager = null;
    private UsbDevice usbDevice = null;
    private UsbInterface usbInterface = null;
    private UsbDeviceConnection usbDeviceConnection = null;
    private final int m_nVID = 1306;//供应商ID
    private final int m_nPID = 20763;//产品识别码
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private Boolean haveUsbHostApi = false;
    private String deviceNode;//USB设备名称
    private HashMap<String, UsbDevice> deviceList;

    private long deviceHandle = -1;
    private Handler mHandler = null;
    private CardOperateThread cardOperateThread;
    private LotusCardParam tLotusCardParam1 = new LotusCardParam();
    private UsbDeviceConnection conn = null;//这个类用于发送和接收数据和控制消息到USB设备

    /*********************************** UI *********************************/

    public static final int REQUEST_CODE_WECHATUSER = 1201;
    private static final int REQUEST_CODE_CONTRACT = 1202;
    public static final String REDIRECT_URI = "redirect_uri";
    private static final int SCAN_HINT = 1001;
    private static final int CODE_HINT = 1002;
    @BindView(R.id.displayCard)
    TextView displayCard;
    @BindView(R.id.m_tvDeviceNode)
    TextView m_tvDeviceNode;
    @BindView(R.id.card_type)
    Spinner card_type;
    @BindView(R.id.mtm_spinner)
    Spinner mtm_spinner;
    private Boolean flag = false;

    int cardType = 1;
    String mtmID = "";


    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this, mModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = true;
    }

    @Override
    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        mLotusCardDriver = new LotusCardDriver();
        mLotusCardDriver.m_lotusCallBack = this;
        // 设置USB读写回调 串口可以不用此操作
        haveUsbHostApi = SetUsbCallBack();
        //测卡器设备检测
        cardDeviceChecked();
        initHandleCardDetails();
        initSipnner();
        initListener();
    }


    private void initSipnner() {
        List<String> spinnerList = new ArrayList<String>();
        spinnerList.add("员工卡号上传模式中");
        spinnerList.add("样衣卡号上传模式中");
        spinnerList.add("物品卡号上传模式中");
        spinnerList.add("对应卡号分配模式中");
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.item_sipnner_type, R.id.tv_sipnner, spinnerList);
        myAdapter.setDropDownViewResource(R.layout.item_sipnner_type);//保持布局样式一致
        card_type.setAdapter(myAdapter);

        card_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mtm_spinner.setVisibility(View.GONE);
                cardType = i+1;
                LogUtils.loge("cardType===" + cardType);
//                if (searchType == 0) {
//                    et_search.setInputType(InputType.TYPE_CLASS_NUMBER);
//                    et_search.setText("");
//                } else {
//                    et_search.setInputType(InputType.TYPE_CLASS_TEXT);
//                    et_search.setText("");
//                }

                if (cardType == 4) {
                    ToastUtil.showShort("请记得在右边选择需要分配的用户");
                    mPresenter.getMtmDataRequest();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void initListener() {

    }

    private void initHandleCardDetails() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (flag) {
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                    String strDate = formatter.format(curDate);
                    displayCard.setText(msg.obj.toString() + "  (" + strDate + ")");

                    if (cardType != 4) {
                        mPresenter.uploadCardNumRequest(cardType,msg.obj.toString());
                    }else {
                        if (mtmID != "") {
                            mPresenter.assignCardNumRequest(mtmID,msg.obj.toString());
                        }else {
                            ToastUtil.showShort("没有对应的用户可以分配");
                        }

                    }


//                    flag = false;
                }
            }
        };
    }

    //刷卡器USB状态检测
    private void cardDeviceChecked() {
        if (haveUsbHostApi) {
            m_tvDeviceNode.post(new Runnable() {
                @Override
                public void run() {
                    m_tvDeviceNode.setText("已连接");
                }
            });
            initAuto();
        } else {
            m_tvDeviceNode.post(new Runnable() {
                @Override
                public void run() {
                    m_tvDeviceNode.setText("未连接");
                }
            });
        }
    }

    //设置USB读写回调
    private Boolean SetUsbCallBack() {
        Boolean bResult = false;
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        // Get UsbManager from Android.
        usbManager = (UsbManager) AppApplication.getAppContext().getSystemService(USB_SERVICE);
        if (null == usbManager) {
            return bResult;
        }

        //获取设备及设备名字
        deviceList = usbManager.getDeviceList();
        if (!deviceList.isEmpty()) {
            for (UsbDevice device : deviceList.values()) {
                if ((m_nVID == device.getVendorId())
                        && (m_nPID == device.getProductId())) {
                    usbDevice = device;
                    deviceNode = usbDevice.getDeviceName();
                    break;
                }
            }
        }
        if (null == usbDevice) {
            return bResult;
        }
        usbInterface = usbDevice.getInterface(0);
        if (null == usbInterface) {
            return bResult;
        }
        if (false == usbManager.hasPermission(usbDevice)) {//权限判断
            usbManager.requestPermission(usbDevice, pendingIntent);
        }

        if (usbManager.hasPermission(usbDevice)) {
            conn = usbManager.openDevice(usbDevice);//获取实例
        }

        if (null == conn) {
            return bResult;
        }

        if (conn.claimInterface(usbInterface, true)) {
            usbDeviceConnection = conn;
        } else {
            conn.close();
        }
        if (null == usbDeviceConnection) {
            return bResult;
        }
        // 把上面获取的对性设置到接口中用于回调操作
        LotusCardDriver.m_UsbDeviceConnection = usbDeviceConnection;
        if (usbInterface.getEndpoint(1) != null) {
            LotusCardDriver.m_OutEndpoint = usbInterface.getEndpoint(1);
        }
        if (usbInterface.getEndpoint(0) != null) {
            LotusCardDriver.m_InEndpoint = usbInterface.getEndpoint(0);
        }
        bResult = true;
        return bResult;
    }

    //自动检测USB设备初始化
    public void initAuto() {
        if (-1 == deviceHandle) {
            deviceHandle = mLotusCardDriver.OpenDevice("", 0, 0, 0, 0,// 使用内部默认超时设置
                    true);
        }
        if (deviceHandle != -1) {
            cardOperateThread = new CardOperateThread();
            new Thread(cardOperateThread).start();
        }
    }

    //子线程检测卡号
    public class CardOperateThread implements Runnable {
        @Override
        public void run() {
            boolean bResult;
            int nRequestType;
            long lCardNo;
            int n = 0;

            while (true) {//使得线程循环
                if (haveUsbHostApi && flag) {//是否暂停
                    Log.e("test===", String.valueOf(n++));
                    try {
                        nRequestType = LotusCardDriver.RT_NOT_HALT;//未进入休眠的卡
                        bResult = mLotusCardDriver.GetCardNo(deviceHandle, nRequestType, tLotusCardParam1);//获取卡号，true表示成功

                        //如果失败了则sleep跳出,再循环
                        if (!bResult) {
                            Thread.sleep(200);
                            continue;
                        }

                        Message msg = new Message();
                        lCardNo = bytes2long(tLotusCardParam1.arrCardNo);
                        msg.obj = lCardNo;
                        mHandler.sendMessage(msg);

                        mLotusCardDriver.Beep(deviceHandle, 10);//响铃
                        mLotusCardDriver.Halt(deviceHandle);//响铃关闭
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    //获取到的卡号是4个字节的需转换
    public long bytes2long(byte[] byteNum) {
        long num = 0;
        for (int ix = 3; ix >= 0; --ix) {
            num <<= 8;
            if (byteNum[ix] < 0) {
                num |= (256 + (byteNum[ix]) & 0xff);
            } else {
                num |= (byteNum[ix] & 0xff);
            }
        }
        return num;
    }

    @Override
    public boolean callBackExtendIdDeviceProcess(Object objUser, byte[] arrBuffer) {
        return false;
    }

    @Override
    public boolean callBackReadWriteProcess(long nDeviceHandle, boolean bRead, byte[] arrBuffer) {
        int nResult = 0;
        boolean bResult = false;
        int nBufferLength = arrBuffer.length;
        int nWaitCount = 0;
        if (null == m_UsbDeviceConnection) {
            AddLog("null == m_UsbDeviceConnection");
            return false;
        }
        if (null == m_OutEndpoint) {
            AddLog("null == m_OutEndpoint");
            return false;
        }
        if (null == m_InEndpoint) {
            AddLog("null == m_InEndpoint");
            return false;
        }
        if (nBufferLength < 65) {
            AddLog("nBufferLength < 65");
            return false;
        }
        if (true == bRead) {
            arrBuffer[0] = 0;
            while (true) {
                nResult = m_UsbDeviceConnection.bulkTransfer(m_InEndpoint,
                        arrBuffer, 64, 3000);
                if (nResult <= 0) {
                    AddLog("nResult <= 0 is " + nResult);
                    break;
                }
                if (arrBuffer[0] != 0) {
                    //此处调整一下
                    System.arraycopy(arrBuffer, 0, arrBuffer, 1, nResult);
                    arrBuffer[0] = (byte) nResult;
                    break;
                }
                nWaitCount++;
                if (nWaitCount > 1000) {
                    AddLog("nWaitCount > 1000");
                    break;
                }
            }
            if (nResult == 64) {
                bResult = true;
            } else {
                AddLog("nResult != 64 is" + nResult);
                bResult = false;
            }
        } else {
            nResult = m_UsbDeviceConnection.bulkTransfer(m_OutEndpoint,
                    arrBuffer, 64, 3000);
            if (nResult == 64) {
                bResult = true;
            } else {
                AddLog("m_OutEndpoint bulkTransfer Write error");
                bResult = false;
            }
        }
        return bResult;
    }

    public void AddLog(String strLog) {
    }

    //卡号上传返回
    @Override
    public void returnUploadCardNumData(HttpResponse httpResponse) {
        if (cardType==1) {
            ToastUtil.showShort("员工卡  OK");
        }
        if (cardType==2) {
            ToastUtil.showShort("样衣卡  OK");
        }
        if (cardType==3) {
            ToastUtil.showShort("物品卡  OK");
        }

    }

    //卡号分配
    @Override
    public void returnAssignCardNumData(HttpResponse httpResponse) {
        ToastUtil.showShort("assign  OK");
    }

    //mtm用户列表获取
    @Override
    public void returnMtmDData(List<MtmData> mtmDataList) {
        if (mtmDataList != null && mtmDataList.size() > 0) {
            mtm_spinner.setVisibility(View.VISIBLE);
            List<String> spinnerList = new ArrayList<String>();
            for (MtmData mtmData : mtmDataList) {
                spinnerList.add(mtmData.getName());
            }
            ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, R.layout.item_sipnner_type, R.id.tv_sipnner, spinnerList);
            myAdapter.setDropDownViewResource(R.layout.item_sipnner_type);//保持布局样式一致
            mtm_spinner.setAdapter(myAdapter);
            mtm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mtmID = mtmDataList.get(i).getID();
                    LogUtils.loge("用户对应的MtmID   " + mtmID);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            ToastUtil.showShort("暂时没有分店");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        flag = false;
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {
        ToastUtil.showShort(msg);
    }
}
