package com.example.gxkj.cardnumbinding.contract;

import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.StaffData;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public interface StaffBindingContract {
    interface Model extends BaseModel{
        Observable<StaffData> getStaffData(String code);

        Observable<HttpResponse> bindingCardWithStaff(String num, String id);
    }

    interface View extends BaseView{
        void returnGetStaffData(StaffData staffData);

        void returnBindingCardWithStaff(HttpResponse httpResponse);
    }

    abstract class Presenter extends BasePresenter<View, Model> {

        public abstract void getStaffDataRequest(String code);

        public abstract void bindingCardWithStaff(String num , String id);
    }


}
