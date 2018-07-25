package com.example.gxkj.cardnumbinding.model;

import com.example.gxkj.cardnumbinding.api.Api;
import com.example.gxkj.cardnumbinding.api.ApiConstants;
import com.example.gxkj.cardnumbinding.api.HostType;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.MtmData;
import com.example.gxkj.cardnumbinding.contract.UploadCardNumContract;
import com.jaydenxiao.common.baserx.RxSchedulers;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class UploadCardNumModel implements UploadCardNumContract.Model {
    @Override
    public Observable<HttpResponse> getUploadCardNumData(int type, String num) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .uploadCardNum(type,num)
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<HttpResponse> getAssignCardNumData(String mtmID, String cardID) {
        return Api.getDefault(HostType.QUALITY_DATA)
                .assignCardNum(mtmID,cardID)
                .compose(RxSchedulers.io_main());
    }

    @Override
    public Observable<List<MtmData>> getMtmData() {
        return Api.getDefault(HostType.QUALITY_DATA)
                .mtmInfo()
                .map(new Api.HttpResponseFunc<>())
                .compose(RxSchedulers.io_main());
    }

}
