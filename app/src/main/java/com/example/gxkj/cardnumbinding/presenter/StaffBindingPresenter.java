package com.example.gxkj.cardnumbinding.presenter;

import com.example.gxkj.cardnumbinding.bean.SampleData;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.bean.StaffData;
import com.example.gxkj.cardnumbinding.contract.SampleBindingContract;
import com.example.gxkj.cardnumbinding.contract.StaffBindingContract;
import com.jaydenxiao.common.baserx.RxSubscriber;

/**
 * Created by Administrator on 2018/5/20 0020.
 */

public class StaffBindingPresenter extends StaffBindingContract.Presenter{
    @Override
    public void getStaffDataRequest(String code) {
        mRxManage.add(mModel.getStaffData(code).subscribeWith(new RxSubscriber<StaffData>(mContext, true) {
            @Override
            protected void _onNext(StaffData staffData) {
                mView.returnGetStaffData(staffData);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);

            }
        }));
    }

    @Override
    public void bindingCardWithStaff(String num, String id) {
        mRxManage.add(mModel.bindingCardWithStaff(num , id).subscribeWith(new RxSubscriber<HttpResponse>(mContext, true) {
            @Override
            protected void _onNext(HttpResponse httpResponse) {
                mView.returnBindingCardWithStaff(httpResponse);
            }

            @Override
            protected void _onError(String message) {
                mView.showErrorTip(message);
            }
        }));
    }
}
