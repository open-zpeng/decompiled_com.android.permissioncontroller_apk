package com.android.packageinstaller.role.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class RequiredComponent {
    private static final String LOG_TAG = "RequiredComponent";
    private final IntentFilterData mIntentFilterData;
    private final List<RequiredMetaData> mMetaData;
    private final String mPermission;

    protected abstract ComponentName getComponentComponentName(ResolveInfo resolveInfo);

    protected abstract Bundle getComponentMetaData(ResolveInfo resolveInfo);

    protected abstract String getComponentPermission(ResolveInfo resolveInfo);

    protected abstract List<ResolveInfo> queryIntentComponentsAsUser(Intent intent, int i, UserHandle userHandle, Context context);

    public RequiredComponent(IntentFilterData intentFilterData, String str, List<RequiredMetaData> list) {
        this.mIntentFilterData = intentFilterData;
        this.mPermission = str;
        this.mMetaData = list;
    }

    public IntentFilterData getIntentFilterData() {
        return this.mIntentFilterData;
    }

    public ComponentName getQualifyingComponentForPackage(String str, Context context) {
        List<ComponentName> qualifyingComponentsInternal = getQualifyingComponentsInternal(str, Process.myUserHandle(), context);
        if (qualifyingComponentsInternal.isEmpty()) {
            return null;
        }
        return qualifyingComponentsInternal.get(0);
    }

    public List<ComponentName> getQualifyingComponentsAsUser(UserHandle userHandle, Context context) {
        return getQualifyingComponentsInternal(null, userHandle, context);
    }

    private List<ComponentName> getQualifyingComponentsInternal(String str, UserHandle userHandle, Context context) {
        boolean z;
        Intent createIntent = this.mIntentFilterData.createIntent();
        if (str != null) {
            createIntent.setPackage(str);
        }
        boolean z2 = !this.mMetaData.isEmpty();
        List<ResolveInfo> queryIntentComponentsAsUser = queryIntentComponentsAsUser(createIntent, z2 ? 786560 : 786432, userHandle, context);
        ArraySet arraySet = new ArraySet();
        ArrayList arrayList = new ArrayList();
        int size = queryIntentComponentsAsUser.size();
        for (int i = 0; i < size; i++) {
            ResolveInfo resolveInfo = queryIntentComponentsAsUser.get(i);
            if (this.mPermission == null || Objects.equals(getComponentPermission(resolveInfo), this.mPermission)) {
                if (z2) {
                    Bundle componentMetaData = getComponentMetaData(resolveInfo);
                    if (componentMetaData == null) {
                        Log.w(LOG_TAG, "Component meta data is null");
                    } else {
                        int size2 = this.mMetaData.size();
                        int i2 = 0;
                        while (true) {
                            if (i2 >= size2) {
                                z = true;
                                break;
                            } else if (!this.mMetaData.get(i2).isQualified(componentMetaData)) {
                                z = false;
                                break;
                            } else {
                                i2++;
                            }
                        }
                        if (!z) {
                        }
                    }
                }
                ComponentName componentComponentName = getComponentComponentName(resolveInfo);
                String packageName = componentComponentName.getPackageName();
                if (!arraySet.contains(packageName)) {
                    arraySet.add(packageName);
                    arrayList.add(componentComponentName);
                }
            }
        }
        return arrayList;
    }

    public String toString() {
        return "RequiredComponent{mIntentFilterData=" + this.mIntentFilterData + ", mPermission='" + this.mPermission + "', mMetaData=" + this.mMetaData + '}';
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RequiredComponent requiredComponent = (RequiredComponent) obj;
        return Objects.equals(this.mIntentFilterData, requiredComponent.mIntentFilterData) && Objects.equals(this.mPermission, requiredComponent.mPermission) && Objects.equals(this.mMetaData, requiredComponent.mMetaData);
    }

    public int hashCode() {
        return Objects.hash(this.mIntentFilterData, this.mPermission, this.mMetaData);
    }
}
