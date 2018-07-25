package com.example.gxkj.cardnumbinding.contract;


import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.MtmData;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by Administrator on 2018/5/20 0020.
 */

public interface UploadCardNumContract {
    interface Model extends BaseModel {
        Observable<HttpResponse> getUploadCardNumData(int type, String num);

        Observable<HttpResponse> getAssignCardNumData(String mtmID, String num);

        Observable<List<MtmData>> getMtmData();
    }

    interface View extends BaseView {
        void returnUploadCardNumData(HttpResponse httpResponse);

        void returnAssignCardNumData(HttpResponse httpResponse);

        void returnMtmDData(List<MtmData> mtmDataList);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void uploadCardNumRequest(int type, String num);

        public abstract void assignCardNumRequest(String mtmID, String num);

        public abstract void getMtmDataRequest();
    }

}
