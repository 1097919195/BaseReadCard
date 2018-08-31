package com.example.gxkj.cardnumbinding.presenter;

import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.contract.SampleBindingContract;
import com.jaydenxiao.common.baserx.RxSubscriber;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class SampleBindingPresenter extends SampleBindingContract.Presenter{
    @Override
    public void getSampleDataRequest(String code) {
        mRxManage.add(mModel.getSampleData(code).subscribeWith(new RxSubscriber<SampleData>(mContext, true) {
            @Override
            protected void _onNext(SampleData sampleData) {
                mView.returnGetSampleData(sampleData);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);

            }
        }));
    }

    @Override
    public void getSampleDataWithBarCodeRequest(String barCode) {
        mRxManage.add(mModel.getSampleDataWithBarCode(barCode).subscribeWith(new RxSubscriber<SampleData>(mContext, true) {
            @Override
            protected void _onNext(SampleData sampleData) {
                mView.returnGetSampleDataWithBarCode(sampleData);
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
