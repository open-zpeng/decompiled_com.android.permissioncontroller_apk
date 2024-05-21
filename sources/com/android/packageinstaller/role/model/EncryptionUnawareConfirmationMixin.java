package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import com.android.car.ui.R;
import com.android.packageinstaller.role.utils.PackageUtils;
/* loaded from: classes.dex */
public class EncryptionUnawareConfirmationMixin {
    private static final String LOG_TAG = "EncryptionUnawareConfirmationMixin";

    public static CharSequence getConfirmationMessage(Role role, String str, Context context) {
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(str, context);
        if (applicationInfo == null) {
            String str2 = LOG_TAG;
            Log.w(str2, "Cannot get ApplicationInfo for application, package name: " + str);
            return null;
        } else if (applicationInfo.isEncryptionAware()) {
            return null;
        } else {
            return context.getString(R.string.encryption_unaware_confirmation_message);
        }
    }
}
