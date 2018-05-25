package com.example.gxkj.cardnumbinding.presenter;

import com.example.gxkj.cardnumbinding.bean.FinishedProductSampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.contract.SampleBindingContract;
import com.jaydenxiao.common.baserx.RxSubscriber;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class SampleBindingPresenter extends SampleBindingContract.Presenter{
    @Override
    public void getSampleDataRequest(String code) {
        mRxManage.add(mModel.getSampleData(code).subscribeWith(new RxSubscriber<FinishedProductSampleData>(mContext, true) {
            @Override
            protected void _onNext(FinishedProductSampleData sampleData) {
                mView.returnGetSampleData(sampleData);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);

            }
        }));
    }

    @Override
    public void bindingCardWithCode(String num, String id) {
        mRxManage.add(mModel.bindingCardWithCode(num , id).subscribeWith(new RxSubscriber<HttpResponse>(mContext, true) {
            @Override
            protected void _onNext(HttpResponse httpResponse) {
                mView.returnBindingCardWithCode(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
