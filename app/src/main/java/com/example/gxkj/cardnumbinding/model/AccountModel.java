package com.example.gxkj.cardnumbinding.model;


import com.example.gxkj.cardnumbinding.api.Api;
import com.example.gxkj.cardnumbinding.api.HostType;
import com.example.gxkj.cardnumbinding.bean.LoginTokenData;
import com.example.gxkj.cardnumbinding.contract.AccountContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import io.reactivex.Observable;


/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class AccountModel implements AccountContract.Model{
    @Override
    public Observable<LoginTokenData> getTokenSignIn(String userName, String passWord) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getTokenWithSignIn(userName, passWord)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }
}
