package com.example.gxkj.cardnumbinding.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.gxkj.cardnumbinding.R;
import com.example.gxkj.cardnumbinding.app.AppApplication;
import com.example.gxkj.cardnumbinding.app.AppConstant;
import com.example.gxkj.cardnumbinding.bean.LoginTokenData;
import com.example.gxkj.cardnumbinding.contract.AccountContract;
import com.example.gxkj.cardnumbinding.model.AccountModel;
import com.example.gxkj.cardnumbinding.presenter.AccountPresenter;
import com.jaydenxiao.common.base.BaseActivity;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.PermissionUtils;
import com.jaydenxiao.common.commonutils.SPUtils;
import com.jaydenxiao.common.commonutils.ToastUtil;

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
    @BindView(R.id.input_eye)
    ImageView inputEye;
    @BindView(R.id.cb_remain_username)
    CheckBox remainUsername;
    @BindView(R.id.cb_remain_password)
    CheckBox remainPassword;

    private String username = "";
    private String password = "";
    private boolean diaplayPassword = false;

    SharedPreferences sp = AppApplication.getAppContext().getSharedPreferences("share", MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    boolean isFirstRun = sp.getBoolean("isFirstRun", true);
    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA ,Manifest.permission.RECORD_AUDIO};
    public static int permissionCode = 1;


    public static void startAction(Activity activity) {
        Intent intent = new Intent(activity, AccountActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public int getLayoutId() {
        return R.layout.act_login_another;
    }

    @Override
    public void initPresenter() {
        mPresenter.setVM(this,mModel);
    }

    @Override
    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//底部导航栏覆盖activity
        initPermission();
        initUserInfo();
        initListener();
    }

    private void initPermission() {
//        RxPermissions rxPermissions = new RxPermissions(AccountActivity.this);
//        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(
//                aBoolean -> {
////                        if (aBoolean==true) {
////                            //请求成功处理的事件
////                        } else {
////                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountActivity.this);
////                            alertDialog.setMessage("请手动开启权限！");
////                            alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    finish();
////                                }
////                            });
////                            alertDialog.create().show();
////                        }
//                });

        if (isFirstRun){
            //请求多个权限
            PermissionUtils.checkAndRequestMorePermissions(this,permissions,permissionCode);

            Log.e("debug", "第一次运行");
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }
    }

    private void initUserInfo() {
        username = SPUtils.getSharedStringData(AppApplication.getAppContext(), AppConstant.USERINFO_NAME);
        password = SPUtils.getSharedStringData(AppApplication.getAppContext(), AppConstant.USERINFO_PASS);
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
    }

    @Override
    public void returnGetTokenSignIn(LoginTokenData tokenData) {
//        if (httpResponse.getSuccess()) {
//            try {
//                JSONObject jsonObject = new JSONObject(httpResponse.getData().toString());
//                LogUtils.loge(httpResponse.getData().toString());
//                LogUtils.loge(jsonObject.getString("jwt"));
//                SPUtils.setSharedStringData(AppApplication.getAppContext(),AppConstant.TOKEN,jsonObject.getString("jwt"));
//                LotusCardDemoActivity.startAction(AccountActivity.this);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }else {
//            ToastUtil.showShort("用户名或者密码错误！");
//        }
        SPUtils.setSharedStringData(AppApplication.getAppContext(), AppConstant.LOGIN_TOKEN, tokenData.getToken_type() + tokenData.getAccess_token());
        LogUtils.loge(tokenData.getAccess_token());
        ToastUtil.showShort("登录成功！");
        finish();
        MainActivity.startAction(AccountActivity.this);
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
