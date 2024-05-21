package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;
import java.util.Collections;
import java.util.Set;
/* loaded from: classes.dex */
public class UserDeniedManager {
    private static UserDeniedManager sInstance;
    private final SharedPreferences mPreferences;

    public static UserDeniedManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UserDeniedManager(context);
        }
        return sInstance;
    }

    private UserDeniedManager(Context context) {
        this.mPreferences = context.getApplicationContext().getSharedPreferences("request_role_user_denied", 0);
    }

    public boolean isDeniedOnce(String str, String str2) {
        return isDenied(str, str2, false);
    }

    public void setDeniedOnce(String str, String str2) {
        setDenied(str, str2, false, true);
    }

    public boolean isDeniedAlways(String str, String str2) {
        return isDenied(str, str2, true);
    }

    public void setDeniedAlways(String str, String str2) {
        setDenied(str, str2, true, true);
    }

    public void clearDenied(String str, String str2) {
        setDenied(str, str2, false, false);
        setDenied(str, str2, true, false);
    }

    public void clearPackageDenied(String str) {
        this.mPreferences.edit().remove(getKey(str, false)).remove(getKey(str, true)).apply();
    }

    private static String getKey(String str, boolean z) {
        StringBuilder sb = new StringBuilder();
        sb.append(z ? "denied_always:" : "denied_once:");
        sb.append(str);
        return sb.toString();
    }

    private boolean isDenied(String str, String str2, boolean z) {
        return this.mPreferences.getStringSet(getKey(str2, z), Collections.emptySet()).contains(str);
    }

    private void setDenied(String str, String str2, boolean z, boolean z2) {
        String key = getKey(str2, z);
        Set<String> stringSet = this.mPreferences.getStringSet(key, Collections.emptySet());
        if (stringSet.contains(str) == z2) {
            return;
        }
        ArraySet arraySet = new ArraySet(stringSet);
        if (z2) {
            arraySet.add(str);
        } else {
            arraySet.remove(str);
        }
        if (str.isEmpty()) {
            this.mPreferences.edit().remove(key).apply();
        } else {
            this.mPreferences.edit().putStringSet(key, arraySet).apply();
        }
    }
}
