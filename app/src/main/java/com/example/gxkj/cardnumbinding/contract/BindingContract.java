package com.example.gxkj.cardnumbinding.contract;

import com.example.gxkj.cardnumbinding.bean.FinishedProductSampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public interface BindingContract {
    interface Model extends BaseModel{
        Observable<FinishedProductSampleData> getSampleData(String code);

        Observable<HttpResponse> bindingCardWithCode(String num , String id);
    }

    interface View extends BaseView{
        void returnGetSampleData(FinishedProductSampleData sampleData);

        void returnBindingCardWithCode(HttpResponse httpResponse);
    }

    abstract class Presenter extends BasePresenter<View, Model> {

        public abstract void getSampleDataRequest(String code);

        public abstract void bindingCardWithCode(String num , String id);
    }


}
