package com.android.packageinstaller.permission.data;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.lifecycle.LiveData;
import java.util.List;
/* loaded from: classes.dex */
class UsersLiveData extends LiveData<List<UserHandle>> {
    private static UsersLiveData sInstance;
    private final Application mApplication;
    private BroadcastReceiver mUserMonitor = new BroadcastReceiver() { // from class: com.android.packageinstaller.permission.data.UsersLiveData.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            UsersLiveData.this.update();
        }
    };

    public static UsersLiveData get(Application application) {
        if (sInstance == null) {
            sInstance = new UsersLiveData(application);
        }
        return sInstance;
    }

    private UsersLiveData(Application application) {
        this.mApplication = application;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        setValue(((UserManager) this.mApplication.getSystemService(UserManager.class)).getUserProfiles());
    }

    @Override // androidx.lifecycle.LiveData
    protected void onActive() {
        update();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        this.mApplication.registerReceiver(this.mUserMonitor, intentFilter);
    }

    @Override // androidx.lifecycle.LiveData
    protected void onInactive() {
        this.mApplication.unregisterReceiver(this.mUserMonitor);
    }
}
