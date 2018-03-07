package com.shine.cardreaderswitch;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * author:
 * 时间:2018/3/1
 * qq:1220289215
 * 类描述：
 */

public class AppExecutors {
    private final Executor mNetworkExecutor;
    private final Executor mMainHandler;

    public AppExecutors() {
        mNetworkExecutor = Executors.newFixedThreadPool(3);
        mMainHandler = new MainExecutor();
    }

    public Executor networkExecutor() {
        return mNetworkExecutor;
    }

    public Executor mainHandler() {
        return mMainHandler;
    }

    private static class MainExecutor implements Executor {
        private final Handler mHandler=new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mHandler.post(command);
        }
    }
}

