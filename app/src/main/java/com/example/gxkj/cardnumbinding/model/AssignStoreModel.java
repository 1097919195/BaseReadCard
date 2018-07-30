package com.example.gxkj.cardnumbinding.model;

import com.example.gxkj.cardnumbinding.api.Api;
import com.example.gxkj.cardnumbinding.api.HostType;
import com.example.gxkj.cardnumbinding.bean.GoodsData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.StaffData;
import com.example.gxkj.cardnumbinding.bean.StoreData;
import com.example.gxkj.cardnumbinding.contract.AssignStoreContract;
import com.example.gxkj.cardnumbinding.contract.GoodsBindingContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class AssignStoreModel implements AssignStoreContract.Model {

    @Override
    public Observable<List<StoreData>> getStoreData() {
        return Api.getDefault(HostType.QUALITY_DATA)
                .getStoreData()
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<HttpResponse> assignStore(String num, String id) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .assignStore(num, id)
                .compose(RxSchedulers.io_main());
    }
}
