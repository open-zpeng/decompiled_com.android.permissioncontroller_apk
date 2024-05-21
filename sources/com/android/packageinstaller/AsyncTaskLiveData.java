package com.android.packageinstaller;

import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
/* loaded from: classes.dex */
public abstract class AsyncTaskLiveData<T> extends LiveData<T> {
    protected abstract T loadValueInBackground();

    public /* synthetic */ void lambda$loadValue$0$AsyncTaskLiveData() {
        postValue(loadValueInBackground());
    }

    public void loadValue() {
        AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.-$$Lambda$AsyncTaskLiveData$p6I7fd52mjco5-ESsTcgU1Sa7Yk
            @Override // java.lang.Runnable
            public final void run() {
                AsyncTaskLiveData.this.lambda$loadValue$0$AsyncTaskLiveData();
            }
        });
    }
}
