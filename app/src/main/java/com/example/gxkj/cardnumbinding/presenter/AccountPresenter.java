package com.example.gxkj.cardnumbinding.presenter;

import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.contract.AccountContract;
import com.jaydenxiao.common.baserx.RxSubscriber;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class AccountPresenter extends AccountContract.Presenter{
    @Override
    public void getTokenSignInRequest(String userName, String passWord) {
//        mRxManage.add(mModel.getTokenSignIn(userName,passWord)
//                .subscribe(
//                        httpResponse -> {mView.returnGetTokenSignIn(httpResponse);},
//                        e ->{mView.showErrorTip(e.getMessage());}
//                ));

        mRxManage.add(mModel.getTokenSignIn(userName,passWord).subscribeWith(new RxSubscriber<HttpResponse>(mContext, true) {
            @Override
            protected void _onNext(HttpResponse httpResponse) {
                mView.returnGetTokenSignIn(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
