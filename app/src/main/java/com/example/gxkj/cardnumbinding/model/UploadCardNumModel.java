package com.example.gxkj.cardnumbinding.model;

import com.example.gxkj.cardnumbinding.api.Api;
import com.example.gxkj.cardnumbinding.api.ApiConstants;
import com.example.gxkj.cardnumbinding.api.HostType;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.contract.UploadCardNumContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class UploadCardNumModel implements UploadCardNumContract.Model {
    @Override
    public Observable<HttpResponse> getUploadCardNumData(String num) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .uploadCardNum(num)
                .compose(RxSchedulers.io_main());
    }
}
