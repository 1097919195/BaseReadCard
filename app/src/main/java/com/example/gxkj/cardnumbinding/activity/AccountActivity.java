package com.example.gxkj.cardnumbinding.activity;

import android.content.Context;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
    @BindView(R.id.input_username)
    EditText userName;
    @BindView(R.id.input_password)
    EditText passWord;
    @BindView(R.id.btn_login)
    Button login;
    @BindView(R.id.btn_setting)
    Button setting;
    @BindView(R.id.input_eye)
    ImageView inputEye;
    @BindView(R.id.cb_remain_username)
    CheckBox remainUsername;
    @BindView(R.id.cb_remain_password)
    CheckBox remainPassword;

    private String username = "";
    private String password = "";
    private boolean diaplayPassword = false;

    public static void startAction(Context mContext) {
        Intent intent = new Intent(mContext, AccountActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_login;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);
    }

    @Override
    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//底部导航栏覆盖activity
        initUserInfo();
        initListener();
    }

    private void initUserInfo() {
        username = SPUtils.getSharedStringData(AppApplication.getAppContext(),AppConstant.USERINFO_NAME);
        password = SPUtils.getSharedStringData(AppApplication.getAppContext(),AppConstant.USERINFO_PASS);
        if (!"".equals(username)) {
            userName.setText(username);
            remainUsername.setChecked(true);
        }
        if (!"".equals(password)) {
            passWord.setText(password);
            remainPassword.setChecked(true);
        }
    }

    private void initListener() {
        login.setOnClickListener(v -> {
//            mPresenter.getTokenSignInRequest("duc","000000");
            username = userName.getText().toString();
            password = passWord.getText().toString();
            if (remainUsername.isChecked()) {
                SPUtils.setSharedStringData(AppApplication.getAppContext(),AppConstant.USERINFO_NAME,username);
            } else {
                SPUtils.setSharedStringData(AppApplication.getAppContext(),AppConstant.USERINFO_NAME,"");
            }

            if (remainPassword.isChecked()) {
                SPUtils.setSharedStringData(AppApplication.getAppContext(),AppConstant.USERINFO_PASS,password);
            } else {
                SPUtils.setSharedStringData(AppApplication.getAppContext(),AppConstant.USERINFO_PASS,"");
            }
            mPresenter.getTokenSignInRequest(username,password);
        });

        inputEye.setOnClickListener(v -> {
            if (diaplayPassword) {
                passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                diaplayPassword = false;
            } else {
                passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
                diaplayPassword = true;
            }
        });

        setting.setOnClickListener(v -> {
            ToastUtil.showShort("暂无");
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
