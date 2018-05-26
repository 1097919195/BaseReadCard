package com.example.gxkj.cardnumbinding.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.gxkj.cardnumbinding.R;
import com.example.gxkj.cardnumbinding.app.AppApplication;
import com.example.gxkj.cardnumbinding.app.AppConstant;
import com.example.gxkj.cardnumbinding.fragment.SampleBindingFragment;
import com.example.gxkj.cardnumbinding.fragment.StaffBindingFragment;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.baserx.RxBus2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cc.lotuscard.ILotusCallBack;
import cc.lotuscard.LotusCardDriver;
import cc.lotuscard.LotusCardParam;

import static cc.lotuscard.LotusCardDriver.m_InEndpoint;
import static cc.lotuscard.LotusCardDriver.m_OutEndpoint;
import static cc.lotuscard.LotusCardDriver.m_UsbDeviceConnection;

public class MainActivity extends BaseActivity implements ILotusCallBack{

    private LotusCardDriver mLotusCardDriver;
    private UsbManager usbManager = null;
    private UsbDevice usbDevice = null;
    private UsbInterface usbInterface = null;
    private UsbDeviceConnection usbDeviceConnection = null;
    private final int m_nVID = 1306;//供应商ID
    private final int m_nPID = 20763;//产品识别码
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    public static boolean haveUsbHostApi = false;
    private String deviceNode;//USB设备名称
    private HashMap<String, UsbDevice> deviceList;

    private long deviceHandle = -1;
    private Handler mHandler = null;
    private CardOperateThread cardOperateThread;
    private LotusCardParam tLotusCardParam1 = new LotusCardParam();
    private UsbDeviceConnection conn = null;//这个类用于发送和接收数据和控制消息到USB设备

    /*********************************** UI *********************************/
    private Boolean flag = false;

    SampleBindingFragment sampleBindingFragment;
    StaffBindingFragment staffBindingFragment;
    @BindView(R.id.bindingWithStaff)
    Button bindingWithStaff;
    @BindView(R.id.bindingWithSample)
    Button bindingWithSample;


    public static void startActivity(Context mContext) {
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_main;
    }

    @Override
    public void initPresenter() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = true;
    }

    @Override
    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//底部导航栏覆盖activity
        mLotusCardDriver = new LotusCardDriver();
        mLotusCardDriver.m_lotusCallBack = this;
        // 设置USB读写回调 串口可以不用此操作
        haveUsbHostApi = setUsbCallBack();
        //测卡器设备检测
        cardDeviceChecked();
        initHandleCardDetails();
//        initListener();
//        initRxBus2WithSamplePhoto();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化frament
        initFragment(savedInstanceState);
    }

    private void initFragment(Bundle savedInstanceState) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int currentTabPosition = 0;
        if (savedInstanceState != null) {
            sampleBindingFragment = (SampleBindingFragment) getSupportFragmentManager().findFragmentByTag("sampleBindingFragment");
            staffBindingFragment = (StaffBindingFragment) getSupportFragmentManager().findFragmentByTag("staffBindingFragment");

            currentTabPosition = savedInstanceState.getInt(AppConstant.HOME_CURRENT_TAB_POSITION);
        } else {
            sampleBindingFragment = new SampleBindingFragment();
            staffBindingFragment = new StaffBindingFragment();

            transaction.add(R.id.fl_body, sampleBindingFragment, "sampleBindingFragment");
            transaction.add(R.id.fl_body, staffBindingFragment, "staffBindingFragment");
        }
        transaction.commit();
        SwitchTo(currentTabPosition);
    }

    @OnClick({R.id.bindingWithSample,R.id.bindingWithStaff})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.bindingWithSample:
                SwitchTo(0);
                break;
            case R.id.bindingWithStaff:
                SwitchTo(1);
                break;
            default:
                break;
        }

    }

    private void SwitchTo(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                transaction.hide(staffBindingFragment);
                transaction.show(sampleBindingFragment);
                transaction.commitAllowingStateLoss();
                bindingWithSample.setTextColor(getResources().getColor(R.color.red));
                bindingWithStaff.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                transaction.hide(sampleBindingFragment);
                transaction.show(staffBindingFragment);
                transaction.commitAllowingStateLoss();
                bindingWithSample.setTextColor(getResources().getColor(R.color.white));
                bindingWithStaff.setTextColor(getResources().getColor(R.color.red));
                break;
            default:
                break;
        }
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
                    AppConstant.CARD_NUMBER = msg.obj.toString();
//                    Bundle bundle = new Bundle();
//                    bundle.putString(AppConstant.CARD_NUMBER,msg.obj.toString());
//                    sampleBindingFragment.setArguments(bundle);
//                    staffBindingFragment.setArguments(bundle);
                    RxBus2.getInstance().post(AppConstant.SEND_CARD_NUMBER,msg.obj.toString() + "  (" + strDate + ")");
//                    displayCard.setText(msg.obj.toString() + "  (" + strDate + ")");
//                    mPresenter.getQualityDataRequest("59f171090246a35c424dcec5");
//                    flag = false;
                }
            }
        };
    }

    //刷卡器USB状态检测
    private void cardDeviceChecked() {
        if (haveUsbHostApi) {
//            m_tvDeviceNode.post(new Runnable() {
//                @Override
//                public void run() {
//                    m_tvDeviceNode.setText("已连接");
//                }
//            });
            initAuto();
        }else {
//            m_tvDeviceNode.post(new Runnable() {
//                @Override
//                public void run() {
//                    m_tvDeviceNode.setText("未连接");
//                }
//            });
        }
    }

    //设置USB读写回调
    private Boolean setUsbCallBack() {
        Boolean bResult = false;
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        // Get UsbManager from Android.
        usbManager = (UsbManager) AppApplication.getAppContext().getSystemService(USB_SERVICE);
        if (null == usbManager){
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
        if (null == usbDevice){
            return bResult;
        }
        usbInterface = usbDevice.getInterface(0);
        if (null == usbInterface){
            return bResult;
        }
        if (false == usbManager.hasPermission(usbDevice)) {//权限判断
            usbManager.requestPermission(usbDevice, pendingIntent);
        }

        if (usbManager.hasPermission(usbDevice)) {
            conn = usbManager.openDevice(usbDevice);//获取实例
        }

        if (null == conn){
            return bResult;
        }

        if (conn.claimInterface(usbInterface, true)) {
            usbDeviceConnection = conn;
        } else {
            conn.close();
        }
        if (null == usbDeviceConnection){
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
            int n=0;

            while (true) {//使得线程循环
                if (haveUsbHostApi && flag) {//是否暂停
                    Log.e("test===",String.valueOf(n++));
                    try {
                        nRequestType = LotusCardDriver.RT_NOT_HALT;//未进入休眠的卡
                        bResult = mLotusCardDriver.GetCardNo(deviceHandle, nRequestType, tLotusCardParam1);//获取卡号，true表示成功

                        //如果失败了则sleep跳出,再循环
                        if (!bResult) {
                            Thread.sleep(500);
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
            addLog("null == m_UsbDeviceConnection");
            return false;
        }
        if (null == m_OutEndpoint) {
            addLog("null == m_OutEndpoint");
            return false;
        }
        if (null == m_InEndpoint) {
            addLog("null == m_InEndpoint");
            return false;
        }
        if (nBufferLength < 65) {
            addLog("nBufferLength < 65");
            return false;
        }
        if (true == bRead) {
            arrBuffer[0] = 0;
            while (true) {
                nResult = m_UsbDeviceConnection.bulkTransfer(m_InEndpoint,
                        arrBuffer, 64, 3000);
                if (nResult <= 0) {
                    addLog("nResult <= 0 is " + nResult);
                    break;
                }
                if (arrBuffer[0] != 0) {
                    //此处调整一下
                    System.arraycopy(arrBuffer, 0, arrBuffer, 1, nResult);
                    arrBuffer[0] = (byte)nResult;
                    break;
                }
                nWaitCount++;
                if (nWaitCount > 1000) {
                    addLog("nWaitCount > 1000");
                    break;
                }
            }
            if (nResult == 64) {
                bResult = true;
            } else {
                addLog("nResult != 64 is" +nResult);
                bResult = false;
            }
        } else {
            nResult = m_UsbDeviceConnection.bulkTransfer(m_OutEndpoint,
                    arrBuffer, 64, 3000);
            if (nResult == 64) {
                bResult = true;
            } else {
                addLog("m_OutEndpoint bulkTransfer Write error");
                bResult = false;
            }
        }
        return bResult;
    }
    public void addLog(String strLog) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        flag = false;
    }
}
