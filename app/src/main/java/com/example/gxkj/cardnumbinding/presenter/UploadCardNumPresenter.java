package com.example.gxkj.cardnumbinding.presenter;

import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.contract.UploadCardNumContract;
import com.jaydenxiao.common.baserx.RxSubscriber;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class UploadCardNumPresenter extends UploadCardNumContract.Presenter {
    @Override
    public void uploadCardNumRequest(String num) {
        mRxManage.add(mModel.getUploadCardNumData(num).subscribeWith(new RxSubscriber<HttpResponse>(mContext, false) {
            @Override
            protected void _onNext(HttpResponse httpResponse) {
                mView.returnUploadCardNumData(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
