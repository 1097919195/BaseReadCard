package com.example.gxkj.cardnumbinding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gxkj.cardnumbinding.R;
import com.example.gxkj.cardnumbinding.activity.MainActivity;
import com.example.gxkj.cardnumbinding.app.AppConstant;
import com.example.gxkj.cardnumbinding.bean.GoodsData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.camera.CaptureActivity;
import com.example.gxkj.cardnumbinding.contract.GoodsBindingContract;
import com.example.gxkj.cardnumbinding.model.GoodsBindingModel;
import com.example.gxkj.cardnumbinding.presenter.GoodsBindingPresenter;
import com.jaydenxiao.common.base.BaseFragment;
import com.jaydenxiao.common.baserx.RxBus2;
import com.jaydenxiao.common.commonutils.ImageLoaderUtils;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2018/5/25 0025.
 */

public class GoodsBindingFragment extends BaseFragment<GoodsBindingPresenter,GoodsBindingModel> implements GoodsBindingContract.View{
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
    @BindView(R.id.spec)
    TextView spec;
    @BindView(R.id.ban_xing)
    TextView ban_xing;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.size)
    TextView size;
    @BindView(R.id.color)
    TextView color;
    @BindView(R.id.fabric)
    TextView fabric;
    @BindView(R.id.style)
    TextView style;
    @BindView(R.id.profile)
    TextView profile;
    @BindView(R.id.retailPrice)
    TextView retailPrice;
    @BindView(R.id.commit)
    Button commit;
    @BindView(R.id.imgWithProduct)
    ImageView imgWithProduct;

    @Override
    protected int getLayoutResource() {
        return R.layout.frag_goods_binding;
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
        mRxManager.on(AppConstant.RXBUS_SAMPLE_PHOTO, new Consumer<String>() {
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
//            mPresenter.getSampleDataRequest("http://weixin.qq.com/q/02gJC4lIIAdW210000g07x");
        });

        commit.setOnClickListener(v -> {
            if (!AppConstant.CARD_NUMBER.equals("")) {
                if (!AppConstant.GOODS_ID.equals("")) {
                    mPresenter.bindingCardWithGoods(AppConstant.CARD_NUMBER ,AppConstant.GOODS_ID);
                }else {
                    ToastUtil.showShort("请先确认产品");
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
                                mPresenter.getGoodsDataRequest("http://weixin.qq.com/q/02FThs8XsudTk10000M07q");
                            }else {
                                mPresenter.getGoodsDataRequest("http://weixin.qq.com/q/02FThs8XsudTk10000M07q");
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


    @Override
    public void returnGetGoodsData(GoodsData goodsData) {
        name.setText(goodsData.getName());
//        num.setText(sampleData.getNum());
//        spec.setText(sampleData.getSpec());
//        ban_xing.setText(sampleData.getBan_xing());
//        type.setText(String.valueOf(sampleData.getType()));
//        size.setText(sampleData.getSize());
//        color.setText(sampleData.getColor());
//        fabric.setText(sampleData.getFabric());
//        style.setText(sampleData.getStyle());
//        profile.setText(sampleData.getProfile());
//        retailPrice.setText(String.valueOf(sampleData.getRetailPrice()));
//
        AppConstant.GOODS_ID = goodsData.get_id();
//        LogUtils.loge(AppConstant.IMAGE_DOMAIN_NAME+sampleData.getImage());
//        RxBus2.getInstance().post(AppConstant.RXBUS_SAMPLE_PHOTO,AppConstant.IMAGE_DOMAIN_NAME+sampleData.getImage());
    }

    @Override
    public void returnBindingCardWithGoods(HttpResponse httpResponse) {
        AppConstant.CARD_NUMBER = "";
        RxBus2.getInstance().post(AppConstant.CLEAR_CARD_NUMBER,"请刷卡");
        ToastUtil.showShort("binding goods is OK");
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
