package com.example.gxkj.cardnumbinding.model;

import com.example.gxkj.cardnumbinding.api.Api;
import com.example.gxkj.cardnumbinding.api.HostType;
import com.example.gxkj.cardnumbinding.bean.FinishedProductSampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.contract.SampleBindingContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class StaffBindingModel implements SampleBindingContract.Model {
    @Override
    public Observable<FinishedProductSampleData> getSampleData(String code) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getSampleData(code)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<HttpResponse> bindingCardWithCode(String num, String id) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .bindingCardWithCode(num ,id)
                .compose(RxSchedulers.io_main());
    }
}
