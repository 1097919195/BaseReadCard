package com.example.gxkj.cardnumbinding.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper;
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener;
import com.example.gxkj.cardnumbinding.R;
import com.example.gxkj.cardnumbinding.activity.MainActivity;
import com.example.gxkj.cardnumbinding.app.AppConstant;
import com.example.gxkj.cardnumbinding.bean.GoodsData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.StoreData;
import com.example.gxkj.cardnumbinding.camera.CaptureActivity;
import com.example.gxkj.cardnumbinding.contract.AssignStoreContract;
import com.example.gxkj.cardnumbinding.contract.GoodsBindingContract;
import com.example.gxkj.cardnumbinding.model.AssignStoreModel;
import com.example.gxkj.cardnumbinding.model.GoodsBindingModel;
import com.example.gxkj.cardnumbinding.presenter.AssignStorePresenter;
import com.example.gxkj.cardnumbinding.presenter.GoodsBindingPresenter;
import com.jaydenxiao.common.base.BaseFragment;
import com.jaydenxiao.common.baserx.RxBus2;
import com.jaydenxiao.common.commonutils.ImageLoaderUtils;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2018/5/25 0025.
 */

public class AssignStoreFragment extends BaseFragment<AssignStorePresenter, AssignStoreModel> implements AssignStoreContract.View {
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
    @BindView(R.id.commit)
    Button commit;
    @BindView(R.id.unbind)
    Button unbind;
    @BindView(R.id.imgWithProduct)
    ImageView imgWithProduct;
    @BindView(R.id.recyclerStore)
    RecyclerView recyclerStore;
    @BindView(R.id.storeRefresh)
    TextView storeRefresh;
    @BindView(R.id.shop_name)
    TextView shop_name;
    @BindView(R.id.backShop)
    Button backShop;

    List<StoreData> storeDatas = new ArrayList<>();
    CommonRecycleViewAdapter<StoreData> storeAdapter;
    int positionAgo = 0;


    @Override
    protected int getLayoutResource() {
        return R.layout.frag_assign_store;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this, mModel);
    }

    @Override
    protected void initView() {
        initUSBStatus();
        initListener();
        initRxBus2();
        shop_name.setVisibility(View.GONE);
        mPresenter.getStoreDataRequest();//获取门店列表
    }

    private void initAdapter() {
        storeAdapter = new CommonRecycleViewAdapter<StoreData>(getActivity(), R.layout.item_store_name, storeDatas) {
            @Override
            public void convert(ViewHolderHelper helper, StoreData storeData) {
                TextView storeName = helper.getView(R.id.storeName);
                storeName.setText(storeData.getName());
                if (storeData.isSelected()) {
                    storeName.setBackgroundColor(getResources().getColor(R.color.item_selector));
                } else {
                    storeName.setBackgroundColor(getResources().getColor(R.color.item_unSelector));
                }
            }
        };

        recyclerStore.setAdapter(storeAdapter);
        recyclerStore.setLayoutManager(new LinearLayoutManager(getActivity()));

        storeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                if (positionAgo < storeDatas.size()) {//防止刷新后造成数组越界
                    storeDatas.get(positionAgo).setSelected(false);
                }
                storeDatas.get(position).setSelected(true);
                AppConstant.STORE_ID = storeDatas.get(position).get_id();
                ToastUtil.showShort("您当前选择的门店是 " + storeDatas.get(position).getName());
                storeAdapter.notifyDataSetChanged();
                positionAgo = position;

                //设置图片
                if (storeDatas.get(position).getImages() != null && storeDatas.get(position).getImages().size() > 0) {
                    RxBus2.getInstance().post(AppConstant.RXBUS_STORE_PHOTO, AppConstant.IMAGE_DOMAIN_NAME + storeDatas.get(position).getImages().get(0).getRelative_path());
                } else {
                    imgWithProduct.setImageResource(R.mipmap.gxkj_logo);
                }
                shop_name.setVisibility(View.VISIBLE);
                shop_name.setText(storeDatas.get(position).getName());
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
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
        mRxManager.on(AppConstant.RXBUS_STORE_PHOTO, new Consumer<String>() {
            @Override
            public void accept(String imgStr) throws Exception {
                ImageLoaderUtils.displayBigPhoto(getActivity(), imgWithProduct, imgStr);
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

    /**
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    private void initListener() {
        //入库
        backShop.setOnClickListener(v -> {
            if (!AppConstant.CARD_NUMBER.equals("")) {
                if (!AppConstant.STORE_ID.equals("")) {
//                    new MaterialDialog.Builder(getActivity())
//                            .title("撤销入库的数量")
//                            //前2个一个是hint一个是预输入的文字
//                            .input("请输入需要入库的数量，默认是1件", "1", new MaterialDialog.InputCallback() {
//                                @Override
//                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                                    if (input.toString().length() > 0) {
//                                        if (isInteger(input.toString())) {
//                                            mPresenter.backStoreRequest(AppConstant.CARD_NUMBER, AppConstant.STORE_ID, Integer.parseInt(input.toString()));
//                                        } else {
//                                            ToastUtil.showShort("输入的值必须为整数");
//                                        }
//                                    } else {
//                                        mPresenter.backStoreRequest(AppConstant.CARD_NUMBER, AppConstant.STORE_ID, 1);
//                                    }
//                                }
//                            })
//                            .negativeText("取消")
//                            .positiveColor(getResources().getColor(R.color.main_blue))
//                            .show();

                    mPresenter.backStoreRequest(AppConstant.CARD_NUMBER, AppConstant.STORE_ID, 1);

                } else {
                    ToastUtil.showShort("请先选择门店");
                }
            } else {
                ToastUtil.showShort("当前卡号为空");
            }
        });

        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE_WECHATUSER);
//            mPresenter.getSampleDataRequest("http://weixin.qq.com/q/02gJC4lIIAdW210000g07x");
        });

        //分配
        commit.setOnClickListener(v -> {
            if (!AppConstant.CARD_NUMBER.equals("")) {
                if (!AppConstant.STORE_ID.equals("")) {
//                    new MaterialDialog.Builder(getActivity())
//                            .title("分配的库存数量")
//                            //前2个一个是hint一个是预输入的文字
//                            .input("请输入需要分配的库存数量，默认是1件", "1", new MaterialDialog.InputCallback() {
//                                @Override
//                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
//                                    if (input.toString().length() > 0) {
//                                        if (isInteger(input.toString())) {
//                                            mPresenter.assignStoreRequest(AppConstant.CARD_NUMBER, AppConstant.STORE_ID, Integer.parseInt(input.toString()));
//                                        } else {
//                                            ToastUtil.showShort("输入的值必须为整数");
//                                        }
//                                    } else {
//                                        mPresenter.assignStoreRequest(AppConstant.CARD_NUMBER, AppConstant.STORE_ID, 1);
//                                    }
//                                }
//                            })
//                            .negativeText("取消")
//                            .positiveColor(getResources().getColor(R.color.main_blue))
//                            .show();

                    mPresenter.assignStoreRequest(AppConstant.CARD_NUMBER, AppConstant.STORE_ID, 1);

                } else {
                    ToastUtil.showShort("请先选择门店");
                }
            } else {
                ToastUtil.showShort("当前卡号为空");
            }

        });

        storeRefresh.setOnClickListener(v -> mPresenter.getStoreDataRequest());

        //解绑卡号
        unbind.setOnClickListener(v -> {
            if (!AppConstant.CARD_NUMBER.equals("")) {
                new MaterialDialog.Builder(getActivity())
                        .title("您确定要解绑该卡吗")
                        .onPositive((d, i) -> {
                            mPresenter.unbindCardRequest(AppConstant.CARD_NUMBER);
                        })
                        .negativeText("取消")
                        .positiveText("确定")
                        .negativeColor(getResources().getColor(R.color.red))
                        .show();

            } else {
                ToastUtil.showShort("当前卡号为空");
            }

        });
    }

    //获取二维码扫描结果处理
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.loge(String.valueOf(requestCode) + "   " + String.valueOf(resultCode));
        if (requestCode == REQUEST_CODE_WECHATUSER) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                String result = bundle.getString("result");
                switch (resultCode) {
                    case SCAN_HINT:
                        if (result != null) {
                            LogUtils.loge("二维码解析====" + result);
                            if (result.contains("http")) {
                            } else {
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
    public void returnGetStoreData(List<StoreData> storeDataList) {
        storeDatas = storeDataList;
        initAdapter();
    }

    @Override
    public void returnAssignStore(HttpResponse httpResponse) {
        AppConstant.CARD_NUMBER = "";
        RxBus2.getInstance().post(AppConstant.CLEAR_CARD_NUMBER, "请刷卡");
        ToastUtil.showShort("分配成功");
    }

    @Override
    public void returnBackStore(HttpResponse httpResponse) {
        AppConstant.CARD_NUMBER = "";
        RxBus2.getInstance().post(AppConstant.CLEAR_CARD_NUMBER, "请刷卡");
        ToastUtil.showShort("入库成功");
    }

    @Override
    public void returnUnbindCard(HttpResponse httpResponse) {
        AppConstant.CARD_NUMBER = "";
        RxBus2.getInstance().post(AppConstant.CLEAR_CARD_NUMBER, "请刷卡");
        ToastUtil.showShort("解绑成功");
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
