package com.android.packageinstaller.role.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Process;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class PreferredActivity {
    private final RequiredActivity mActivity;
    private final List<IntentFilterData> mIntentFilterDatas;

    public PreferredActivity(RequiredActivity requiredActivity, List<IntentFilterData> list) {
        this.mActivity = requiredActivity;
        this.mIntentFilterDatas = list;
    }

    public void configure(String str, Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName qualifyingComponentForPackage = this.mActivity.getQualifyingComponentForPackage(str, context);
        if (qualifyingComponentForPackage == null) {
            return;
        }
        int size = this.mIntentFilterDatas.size();
        for (int i = 0; i < size; i++) {
            IntentFilterData intentFilterData = this.mIntentFilterDatas.get(i);
            IntentFilter createIntentFilter = intentFilterData.createIntentFilter();
            createIntentFilter.addCategory("android.intent.category.DEFAULT");
            packageManager.replacePreferredActivity(createIntentFilter, intentFilterData.getDataScheme() != null ? 2097152 : 1048576, this.mActivity.getQualifyingComponentsAsUser(Process.myUserHandle(), context), qualifyingComponentForPackage);
        }
    }

    public String toString() {
        return "PreferredActivity{mActivity=" + this.mActivity + ", mIntentFilterDatas=" + this.mIntentFilterDatas + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || PreferredActivity.class != obj.getClass()) {
            return false;
        }
        PreferredActivity preferredActivity = (PreferredActivity) obj;
        return Objects.equals(this.mActivity, preferredActivity.mActivity) && Objects.equals(this.mIntentFilterDatas, preferredActivity.mIntentFilterDatas);
    }

    public int hashCode() {
        return Objects.hash(this.mActivity, this.mIntentFilterDatas);
    }
}
