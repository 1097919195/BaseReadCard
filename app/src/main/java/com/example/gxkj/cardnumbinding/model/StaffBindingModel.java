package com.example.gxkj.cardnumbinding.model;

import com.example.gxkj.cardnumbinding.api.Api;
import com.example.gxkj.cardnumbinding.api.HostType;
import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.StaffData;
import com.example.gxkj.cardnumbinding.contract.StaffBindingContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class StaffBindingModel implements StaffBindingContract.Model {
    @Override
    public Observable<StaffData> getStaffData(String code) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getStaffData(code)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<HttpResponse> bindingCardWithStaff(String num, String id) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .bindingCardWithStaff(num ,id)
                .compose(RxSchedulers.io_main());
    }
}
