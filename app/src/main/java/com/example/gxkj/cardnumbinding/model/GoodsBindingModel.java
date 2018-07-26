package com.example.gxkj.cardnumbinding.model;

import com.example.gxkj.cardnumbinding.api.Api;
import com.example.gxkj.cardnumbinding.api.HostType;
import com.example.gxkj.cardnumbinding.bean.GoodsData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.contract.GoodsBindingContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class GoodsBindingModel implements GoodsBindingContract.Model {
    @Override
    public Observable<GoodsData> getGoodsData(String code) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getGoodsData(code)
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<HttpResponse> bindingCardWithGoods(String num, String id) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .bindingCardWithGoods(num ,id)
                .compose(RxSchedulers.io_main());
    }
}
