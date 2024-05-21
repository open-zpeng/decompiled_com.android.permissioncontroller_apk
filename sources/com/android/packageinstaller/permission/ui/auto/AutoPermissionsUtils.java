package com.android.packageinstaller.permission.ui.auto;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import com.android.packageinstaller.permission.utils.Utils;
/* loaded from: classes.dex */
public final class AutoPermissionsUtils {
    public static PackageInfo getPackageInfo(Activity activity, String str, UserHandle userHandle) {
        try {
            return activity.createPackageContextAsUser(str, 0, userHandle).getPackageManager().getPackageInfo(str, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("AutoPermissionsUtils", "No package:" + activity.getCallingPackage(), e);
            return null;
        }
    }

    public static Preference createHeaderPreference(Context context, ApplicationInfo applicationInfo) {
        Drawable badgedIcon = Utils.getBadgedIcon(context, applicationInfo);
        Preference preference = new Preference(context);
        preference.setIcon(badgedIcon);
        preference.setKey(applicationInfo.packageName);
        preference.setTitle(Utils.getFullAppLabel(applicationInfo, context));
        return preference;
    }
}
