package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
/* loaded from: classes.dex */
public class VisibilityMixin {
    private static final String LOG_TAG = "VisibilityMixin";

    public static boolean isVisible(String str, Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier(str, "bool", "android");
        if (identifier == 0) {
            String str2 = LOG_TAG;
            Log.w(str2, "Cannot find resource for visibility: " + str);
            return true;
        }
        try {
            return resources.getBoolean(identifier);
        } catch (Resources.NotFoundException e) {
            String str3 = LOG_TAG;
            Log.w(str3, "Cannot get resource for visibility: " + str, e);
            return true;
        }
    }
}
