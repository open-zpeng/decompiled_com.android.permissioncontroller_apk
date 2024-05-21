package com.android.packageinstaller.role.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.packageinstaller.role.model.UserDeniedManager;
import java.util.Objects;
/* loaded from: classes.dex */
public class ClearUserDeniedReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Objects.equals(action, "android.intent.action.PACKAGE_DATA_CLEARED") || Objects.equals(action, "android.intent.action.PACKAGE_FULLY_REMOVED")) {
            UserDeniedManager.getInstance(context).clearPackageDenied(intent.getData().getSchemeSpecificPart());
        }
    }
}
