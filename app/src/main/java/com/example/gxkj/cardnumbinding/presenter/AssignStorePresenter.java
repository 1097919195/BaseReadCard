package com.example.gxkj.cardnumbinding.presenter;

import com.example.gxkj.cardnumbinding.bean.GoodsData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.StoreData;
import com.example.gxkj.cardnumbinding.contract.AssignStoreContract;
import com.example.gxkj.cardnumbinding.contract.GoodsBindingContract;
import com.jaydenxiao.common.baserx.RxSubscriber;

import java.util.List;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class AssignStorePresenter extends AssignStoreContract.Presenter{

    @Override
    public void getStoreDataRequest() {
        mRxManage.add(mModel.getStoreData().subscribeWith(new RxSubscriber<List<StoreData>>(mContext, false) {
            @Override
            protected void _onNext(List<StoreData> storeDataList) {
                mView.returnGetStoreData(storeDataList);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }

    @Override
    public void assignStoreRequest(String num, String id) {
        mRxManage.add(mModel.assignStore(num,id).subscribeWith(new RxSubscriber<HttpResponse>(mContext, false) {
            @Override
            protected void _onNext(HttpResponse httpResponse) {
                mView.returnAssignStore(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
