package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import com.android.packageinstaller.permission.utils.CollectionUtils;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.List;
/* loaded from: classes.dex */
public class ExclusiveDefaultHolderMixin {
    private static final String LOG_TAG = "ExclusiveDefaultHolderMixin";

    public static List<String> getDefaultHolders(Role role, String str, Context context) {
        return CollectionUtils.singletonOrEmpty(getDefaultHolder(role, str, context));
    }

    public static String getDefaultHolder(Role role, String str, Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier(str, "string", "android");
        if (identifier == 0) {
            String str2 = LOG_TAG;
            Log.w(str2, "Cannot find resource for default holder: " + str);
            return null;
        }
        try {
            String string = resources.getString(identifier);
            if (TextUtils.isEmpty(string)) {
                return null;
            }
            ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(string, context);
            if (applicationInfo == null) {
                String str3 = LOG_TAG;
                Log.w(str3, "Cannot get ApplicationInfo for default holder, config: " + str + ", package: " + string);
                return null;
            } else if ((applicationInfo.flags & 1) == 0) {
                String str4 = LOG_TAG;
                Log.w(str4, "Default holder is not a system app, config: " + str + ", package: " + string);
                return null;
            } else if (role.isPackageQualified(string, context)) {
                return string;
            } else {
                String str5 = LOG_TAG;
                Log.w(str5, "Default holder does not qualify for the role, config: " + str + ", package: " + string);
                return null;
            }
        } catch (Resources.NotFoundException e) {
            String str6 = LOG_TAG;
            Log.w(str6, "Cannot get resource for default holder: " + str, e);
            return null;
        }
    }
}
