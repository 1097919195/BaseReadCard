package com.example.gxkj.cardnumbinding.contract;


import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import io.reactivex.Observable;


/**
 * Created by Administrator on 2018/5/20 0020.
 */

public interface UploadCardNumContract {
    interface Model extends BaseModel{
        Observable<HttpResponse> getUploadCardNumData(String num);

        Observable<HttpResponse> getUploadCardNumDataWithStaff(String num);
    }

    interface View extends BaseView{
        void returnUploadCardNumData(HttpResponse httpResponse);

        void returnUploadCardNumDataWithStaff(HttpResponse httpResponse);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        public abstract void uploadCardNumRequest(String num);

        public abstract void uploadCardNumRequestWithStaff(String num);
    }

}
