package com.example.gxkj.cardnumbinding.presenter;

import com.example.gxkj.cardnumbinding.contract.AccountContract;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class AccountPresenter extends AccountContract.Presenter{
    @Override
    public void getTokenSignInRequest(String userName, String passWord) {
        mRxManage.add(mModel.getTokenSignIn(userName,passWord)
                .subscribe(
                        httpResponse -> {mView.returnGetTokenSignIn(httpResponse);},
                        e ->{mView.showErrorTip(e.getMessage());}
                ));
    }
}
