package com.jaydenxiao.common.baserx;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


//RxManager的封装
/**
 * 用于管理单个presenter的RxBus的事件和Rxjava相关代码的生命周期处理
 * Created by xsf
 * on 2016.08.14:50
 */
public class RxManager {
    //拿到rxBus
//    public RxBus mRxBus = RxBus.getInstance();
    public RxBus2 mRxBus2 = RxBus2.getInstance();
    //使用Map管理rxbus订阅
    private Map<String, Observable<?>> mObservables = new HashMap<>();
    /*管理Observables 和 Subscribers订阅,防止内存泄漏*/
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    /**
     * RxBus注入监听（订阅）
     * （可实现相同类型的事件或者消息的区分）
//     * @param eventName
//     * @param action1
     */
//    public <T>void on(String eventName, Action1<T> action1) {
//        Observable<T> mObservable = mRxBus.register(eventName);
//        mObservables.put(eventName, mObservable);
//        /*订阅管理*/
//        mCompositeSubscription.add(mObservable.observeOn(AndroidSchedulers.mainThread())
//                .subscribe(action1, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        //处理错误信息
//                        throwable.printStackTrace();
//                    }
//                }));
//    }
    public <T>void on(String eventName, Consumer<T> action1) {
        Observable<T> mObservable = mRxBus2.register(eventName);
        mObservables.put(eventName, mObservable);
        /*订阅管理*/
        mCompositeDisposable.add(mObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //处理错误信息
                        throwable.printStackTrace();
                    }
                }));
    }

    /**
     * 单纯的Observables 和 Subscribers管理
     * @param m
     */
    public void add(Disposable m) {
        /*订阅管理*/
        mCompositeDisposable.add(m);
    }
    /**
     * 单个presenter生命周期结束，取消订阅和所有rxbus观察
     */
    public void clear() {
        mCompositeDisposable.clear();// 取消所有订阅
        for (Map.Entry<String, Observable<?>> entry : mObservables.entrySet()) {
//            mRxBus.unregister(entry.getKey(), entry.getValue());// 移除rxbus观察
            mRxBus2.unSubscribe(entry.getValue());
        }
    }
    //发送rxbus
    public void post(Object tag, Object content) {
//        mRxBus.post(tag, content);
        mRxBus2.post(tag,content);
    }
}
