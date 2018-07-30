package com.example.gxkj.cardnumbinding.contract;

import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.StaffData;
import com.example.gxkj.cardnumbinding.bean.StoreData;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public interface AssignStoreContract {
    interface Model extends BaseModel{
        Observable<List<StoreData>> getStoreData();

        Observable<HttpResponse> assignStore(String num, String id);
    }

    interface View extends BaseView{
        void returnGetStoreData(List<StoreData> storeDataList);

        void returnAssignStore(HttpResponse httpResponse);
    }

    abstract class Presenter extends BasePresenter<View, Model> {

        public abstract void getStoreDataRequest();

        public abstract void assignStoreRequest(String num , String id);
    }


}
