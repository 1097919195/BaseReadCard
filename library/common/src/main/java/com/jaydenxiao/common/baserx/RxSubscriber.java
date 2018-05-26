package com.jaydenxiao.common.baserx;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.R;
import com.jaydenxiao.common.basebean.HttpResponseError;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.NetWorkUtils;
import com.jaydenxiao.common.commonwidget.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;


/**
 * des:订阅封装,对异常进行封装
 * Created by xsf
 * on 2016.09.10:16
 */
/********************使用例子********************/
/*_apiService.login(mobile, verifyCode)
        .//省略
        .subscribe(new RxSubscriber<User user>(mContext,false) {
@Override
public void _onNext(User user) {
        // 处理user
        }

@Override
public void _onError(String msg) {
        ToastUtil.showShort(mActivity, msg);
        });*/
public abstract class RxSubscriber<T> extends DisposableObserver<T> {

    private Context mContext;
    private String msg;
    private boolean showDialog=true;

    /**
     * 是否显示浮动dialog
     */
    public void showDialog() {
        this.showDialog= true;
    }
    public void hideDialog() {
        this.showDialog= true;
    }

    public RxSubscriber(Context context, String msg,boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog=showDialog;
    }
    public RxSubscriber(Context context) {
        this(context, BaseApplication.getAppContext().getString(R.string.loading),true);
    }
    public RxSubscriber(Context context,boolean showDialog) {
        this(context, BaseApplication.getAppContext().getString(R.string.loading),showDialog);
    }

    @Override
    public void onComplete() {
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (showDialog) {
            try {
                LoadingDialog.showDialogForLoading((Activity) mContext,msg,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onNext(T t) {
        _onNext(t);
    }
    @Override
    public void onError(Throwable e) {
//        if (e instanceof HttpException) {
//            HttpException exception = (HttpException) e;
//            int code = exception.response().code();
//            LogUtils.loge("onErrorCode==" + code);
//            try {
//                String body = exception.response().errorBody().string();
//                LogUtils.loge("body==" + body);
//
//                JSONObject jsonObject = new JSONObject(body);
//                JSONArray jsonArray = jsonObject.getJSONArray("errors");
//                LogUtils.loge("jsonArray==" +jsonArray);
//                for (int i=0; i<jsonArray.length();i++) {
//                    JSONObject ob  = jsonArray.getJSONObject(i);
//                    String msg= ob.getString("message");
//                    LogUtils.loge("msg==" +msg);
//                }
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            } catch (JSONException e1) {
//                e1.printStackTrace();
//            }
//        }
        if (showDialog)
            LoadingDialog.cancelDialogForLoading();
        e.printStackTrace();
        //网络
        if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
            _onError(BaseApplication.getAppContext().getString(R.string.no_net));
        }
        //服务器
        else if (e instanceof ServerException) {
            _onError(e.getMessage());
        }
        else if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            int code = exception.response().code();
            LogUtils.loge("onErrorCode==" + code);
            if (code < 500) {
                if (code ==422) {
                    try {
                        String body = exception.response().errorBody().string();
                        LogUtils.loge("onErrorBody==" + body);

                        Gson gson = new Gson();
                        HttpResponseError responseError = gson.fromJson(body, HttpResponseError.class);
                        LogUtils.loge(responseError.getErrors().get(0).getMessage());
                        _onError(responseError.getErrors().get(0).getMessage());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }else {
                    try {
                        String body = exception.response().errorBody().string();
                        LogUtils.loge("onErrorBody==" + body);

                        Gson gson = new Gson();
                        HttpResponseError responseError = gson.fromJson(body, HttpResponseError.class);
                        LogUtils.loge(responseError.getMsg());
                        _onError(responseError.getMsg());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }else {
                _onError("服务器异常");
            }
        } else if (e instanceof InternalError) {
            LogUtils.loge("onError_internalError");
        }
        //其它
        else {
            _onError(BaseApplication.getAppContext().getString(R.string.net_error));
        }
    }

    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);

}
