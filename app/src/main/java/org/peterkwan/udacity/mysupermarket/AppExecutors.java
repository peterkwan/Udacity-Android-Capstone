package org.peterkwan.udacity.mysupermarket;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AppExecutors {

    private final Executor diskIO;
    private final Executor mainThread;
    private static AppExecutors sInstance;

    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (AppExecutors.class) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
