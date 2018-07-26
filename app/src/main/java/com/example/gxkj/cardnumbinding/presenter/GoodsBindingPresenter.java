package com.example.gxkj.cardnumbinding.presenter;

import com.example.gxkj.cardnumbinding.bean.GoodsData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.contract.GoodsBindingContract;
import com.jaydenxiao.common.baserx.RxSubscriber;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class GoodsBindingPresenter extends GoodsBindingContract.Presenter{
    @Override
    public void getGoodsDataRequest(String code) {
        mRxManage.add(mModel.getGoodsData(code).subscribeWith(new RxSubscriber<GoodsData>(mContext, true) {
            @Override
            protected void _onNext(GoodsData goodsData) {
                mView.returnGetGoodsData(goodsData);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);

            }
        }));
    }

    @Override
    public void bindingCardWithGoods(String num, String id) {
        mRxManage.add(mModel.bindingCardWithGoods(num , id).subscribeWith(new RxSubscriber<HttpResponse>(mContext, true) {
            @Override
            protected void _onNext(HttpResponse httpResponse) {
                mView.returnBindingCardWithGoods(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
