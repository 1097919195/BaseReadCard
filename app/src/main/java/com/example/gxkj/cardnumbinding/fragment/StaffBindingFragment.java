package com.example.gxkj.cardnumbinding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gxkj.cardnumbinding.R;
import com.example.gxkj.cardnumbinding.activity.MainActivity;
import com.example.gxkj.cardnumbinding.app.AppConstant;
import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.StaffData;
import com.example.gxkj.cardnumbinding.camera.CaptureActivity;
import com.example.gxkj.cardnumbinding.contract.SampleBindingContract;
import com.example.gxkj.cardnumbinding.contract.StaffBindingContract;
import com.example.gxkj.cardnumbinding.model.SampleBindingModel;
import com.example.gxkj.cardnumbinding.model.StaffBindingModel;
import com.example.gxkj.cardnumbinding.presenter.SampleBindingPresenter;
import com.example.gxkj.cardnumbinding.presenter.StaffBindingPresenter;
import com.jaydenxiao.common.base.BaseFragment;
import com.jaydenxiao.common.baserx.RxBus2;
import com.jaydenxiao.common.commonutils.ImageLoaderUtils;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;
import com.jaydenxiao.common.security.Md5Security;

import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2018/5/25 0025.
 */

public class StaffBindingFragment extends BaseFragment<StaffBindingPresenter,StaffBindingModel> implements StaffBindingContract.View{
    public static final int REQUEST_CODE_WECHATUSER = 1201;
    private static final int REQUEST_CODE_CONTRACT = 1202;
    public static final String REDIRECT_URI = "redirect_uri";
    private static final int SCAN_HINT = 1001;
    private static final int CODE_HINT = 1002;

    @BindView(R.id.displayCard)
    TextView displayCard;
    @BindView(R.id.m_tvDeviceNode)
    TextView m_tvDeviceNode;
    @BindView(R.id.btnScan)
    Button btnScan;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.department)
    TextView department;
    @BindView(R.id.gender)
    TextView gender;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.position)
    TextView position;
    @BindView(R.id.email)
    TextView email;

    @BindView(R.id.commit)
    Button commit;
    @BindView(R.id.imgWithProduct)
    ImageView imgWithProduct;

    @Override
    protected int getLayoutResource() {
        return R.layout.frag_staff_binding;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);
    }

    @Override
    protected void initView() {
        initUSBStatus();
        initListener();
        initRxBus2();
    }

    private void initUSBStatus() {
        if (MainActivity.haveUsbHostApi) {
            m_tvDeviceNode.post(new Runnable() {
                @Override
                public void run() {
                    m_tvDeviceNode.setText("已连接");
                }
            });
        } else {
            m_tvDeviceNode.post(new Runnable() {
                @Override
                public void run() {
                    m_tvDeviceNode.setText("未连接");
                }
            });
        }
    }

    private void initRxBus2() {
        //设置获取到的产品图片
        mRxManager.on(AppConstant.RXBUS_STAFF_PHOTO, new Consumer<String>() {
            @Override
            public void accept(String imgStr) throws Exception {
                ImageLoaderUtils.displayBigPhoto(getActivity(),imgWithProduct,imgStr);
            }
        });

        //获取卡号
        mRxManager.on(AppConstant.SEND_CARD_NUMBER, new Consumer<String>() {
            @Override
            public void accept(String cardNum) throws Exception {
                displayCard.setText(cardNum);
            }
        });

        //清除卡号
        mRxManager.on(AppConstant.CLEAR_CARD_NUMBER, new Consumer<String>() {
            @Override
            public void accept(String cardNum) throws Exception {
                displayCard.setText(cardNum);
            }
        });
    }

    private void initListener() {
        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            startActivityForResult(intent,REQUEST_CODE_WECHATUSER);
        });

        commit.setOnClickListener(v -> {
            if (!AppConstant.CARD_NUMBER.equals("")) {
                if (!AppConstant.STAFF_ID.equals("")) {
                    mPresenter.bindingCardWithStaff(AppConstant.CARD_NUMBER ,AppConstant.STAFF_ID);
                }else {
                    ToastUtil.showShort("请先确认员工");
                }
            }else {
                ToastUtil.showShort("当前卡号为空");
            }

        });
    }

    //获取二维码扫描结果处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.loge(String.valueOf(requestCode)+"   "+String.valueOf(resultCode));
        if (requestCode == REQUEST_CODE_WECHATUSER) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                switch (resultCode) {
                    case SCAN_HINT:
                        if (result != null) {
                            LogUtils.loge("二维码解析====" + result);
                            if (result.contains("http")) {
                                String sUrl = URLEncoder.encode("http://weixin.qq.com/q/02A2Ru9QsudTk10000g07M");
                                Md5Security.getMD5("http://weixin.qq.com/q/02A2Ru9QsudTk10000g07M");
                                mPresenter.getStaffDataRequest("http://weixin.qq.com/q/02E4ZU8osudTk10000g07W");
                            }else {
                                mPresenter.getStaffDataRequest("http://weixin.qq.com/q/02E4ZU8osudTk10000g07W");
                            }
                        } else {
                            ToastUtil.showShort(getString(R.string.scan_qrcode_failed));
                        }
                        break;
                    case CODE_HINT:
                        break;
                    default:
                        break;
                }
            }
        }

    }


    //获取员工信息结果
    @Override
    public void returnGetStaffData(StaffData staffData) {
        name.setText(staffData.getName());
        num.setText(staffData.getNum());
        department.setText(staffData.getDepartment());
        if (staffData.getGender() == 1) {
            gender.setText("男");
        } else {
            gender.setText("女");
        }
        mobile.setText(staffData.getMobile());
        position.setText(staffData.getPosition());
        email.setText(staffData.getEmail());

        AppConstant.STAFF_ID = staffData.get_id();
//        String relative = staffData.getAvatar().getRelative_image_path();
        RxBus2.getInstance().post(AppConstant.RXBUS_STAFF_PHOTO,staffData.getAvatar());
    }

    //绑卡成功操作
    @Override
    public void returnBindingCardWithStaff(HttpResponse httpResponse) {
        AppConstant.CARD_NUMBER = "";
        RxBus2.getInstance().post(AppConstant.CLEAR_CARD_NUMBER,"请刷卡");
        ToastUtil.showShort("binding staff is OK");
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
