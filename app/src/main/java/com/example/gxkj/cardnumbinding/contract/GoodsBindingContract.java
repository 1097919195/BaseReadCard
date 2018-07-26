package com.example.gxkj.cardnumbinding.contract;

import com.example.gxkj.cardnumbinding.bean.GoodsData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.jaydenxiao.common.base.BaseModel;
import com.jaydenxiao.common.base.BasePresenter;
import com.jaydenxiao.common.base.BaseView;

import io.reactivex.Observable;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public interface GoodsBindingContract {
    interface Model extends BaseModel{
        Observable<GoodsData> getGoodsData(String code);

        Observable<HttpResponse> bindingCardWithGoods(String num, String id);
    }

    interface View extends BaseView{
        void returnGetGoodsData(GoodsData goodsData);

        void returnBindingCardWithGoods(HttpResponse httpResponse);
    }

    abstract class Presenter extends BasePresenter<View, Model> {

        public abstract void getGoodsDataRequest(String code);

        public abstract void bindingCardWithGoods(String num , String id);
    }


}
