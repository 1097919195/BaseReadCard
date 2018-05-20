package com.jaydenxiao.common.baserx;


import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * RxJava调度管理
 * Created by xsf
 * on 2016.08.14:50
 */
public class RxSchedulers {
    public static <T> ObservableTransformer<T, T> io_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());//它指定的操作将在 Android 主线程运行;
            }
        };
    }

    public static <T> MaybeTransformer<T, T> io_main_maybe() {
        return new MaybeTransformer<T, T>() {
            @Override
            public MaybeSource<T> apply(Maybe<T> maybe) {
                return maybe.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());//它指定的操作将在 Android 主线程运行;
            }

        };
    }
}
