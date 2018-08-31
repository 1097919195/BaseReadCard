package com.example.gxkj.cardnumbinding.model;

import com.example.gxkj.cardnumbinding.api.Api;
import com.example.gxkj.cardnumbinding.api.HostType;
import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.contract.SampleBindingContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class SampleBindingModel implements SampleBindingContract.Model {
    @Override
    public Observable<SampleData> getSampleData(String code) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getSampleData(code)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<SampleData> getSampleDataWithBarCode(String barCode) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getSampleDataWithBarCode(barCode)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<HttpResponse> bindingCardWithCode(String num, String id) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .bindingCardWithSample(num ,id)
                .compose(RxSchedulers.io_main());
    }
}
