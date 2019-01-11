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
    @BindView(R.id.remark)
    TextView remark;

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
        mRxManager.on(AppConstant.RXBUS_GOODS_PHOTO, new Consumer<String>() {
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
                    ToastUtil.showShort("请先确认物品");
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
                                mPresenter.getGoodsDataRequest(result);
                            }else {
                                mPresenter.getGoodsDataRequest(result);
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
        num.setText(goodsData.getNum());
        remark.setText(goodsData.getRemark());

        AppConstant.GOODS_ID = goodsData.get_id();
        //设置图片
        if (goodsData.getImages() != null && goodsData.getImages().size() > 0) {
            GoodsData.ImagesBean imagesBean = goodsData.getImages().get(0);
            RxBus2.getInstance().post(AppConstant.RXBUS_GOODS_PHOTO,AppConstant.IMAGE_DOMAIN_NAME + imagesBean.getRelative_path());
        } else {
            imgWithProduct.setImageResource(R.mipmap.gxkj_logo);
        }

    }

    @Override
    public void returnBindingCardWithGoods(HttpResponse httpResponse) {
        AppConstant.CARD_NUMBER = "";
        RxBus2.getInstance().post(AppConstant.CLEAR_CARD_NUMBER,"请刷卡");
        ToastUtil.showShort("物品绑定成功");
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
