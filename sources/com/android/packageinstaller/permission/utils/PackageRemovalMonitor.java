package com.android.packageinstaller.permission.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
/* loaded from: classes.dex */
public abstract class PackageRemovalMonitor extends BroadcastReceiver {
    private final Context mContext;
    private final String mPackageName;

    protected abstract void onPackageRemoved();

    public PackageRemovalMonitor(Context context, String str) {
        this.mContext = context;
        this.mPackageName = str;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.PACKAGE_REMOVED".equals(intent.getAction()) && this.mPackageName.equals(intent.getData().getSchemeSpecificPart())) {
            onPackageRemoved();
        }
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver(this, intentFilter);
    }

    public void unregister() {
        this.mContext.unregisterReceiver(this);
    }
}
