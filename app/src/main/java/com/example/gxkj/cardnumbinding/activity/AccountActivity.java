package com.example.gxkj.cardnumbinding.activity;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.gxkj.cardnumbinding.R;
import com.example.gxkj.cardnumbinding.app.AppApplication;
import com.example.gxkj.cardnumbinding.app.AppConstant;
import com.example.gxkj.cardnumbinding.bean.HttpResponse;
import com.example.gxkj.cardnumbinding.contract.AccountContract;
import com.example.gxkj.cardnumbinding.model.AccountModel;
import com.example.gxkj.cardnumbinding.presenter.AccountPresenter;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.SPUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class AccountActivity extends BaseActivity<AccountPresenter,AccountModel> implements AccountContract.View{
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.passWord)
    TextView passWord;
    @BindView(R.id.login)
    Button login;

    @Override
    public int getLayoutId() {
        return R.layout.act_signin;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);
    }

    @Override
    public void initView() {
        initListener();
    }

    private void initListener() {
        login.setOnClickListener(v -> {
            mPresenter.getTokenSignInRequest("duc","000000");
        });
    }

    @Override
    public void returnGetTokenSignIn(HttpResponse httpResponse) {
        if (httpResponse.getSuccess()) {
            try {
                JSONObject jsonObject = new JSONObject(httpResponse.getData().toString());
                LogUtils.loge(httpResponse.getData().toString());
                LogUtils.loge(jsonObject.getString("jwt"));
                SPUtils.setSharedStringData(AppApplication.getAppContext(),AppConstant.TOKEN,jsonObject.getString("jwt"));
                MainActivity.startActivity(mContext);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            ToastUtil.showShort("用户名或者密码错误！");
        }
    }

    @Override
    public void showLoading(String title) {

    }

    @Override
    public void stopLoading() {

    }

    @Override
    public void showErrorTip(String msg) {
        ToastUtil.showShort(msg);
    }

}
