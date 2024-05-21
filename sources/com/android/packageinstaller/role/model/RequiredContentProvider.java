package com.android.packageinstaller.role.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import com.android.packageinstaller.role.utils.UserUtils;
import java.util.List;
/* loaded from: classes.dex */
public class RequiredContentProvider extends RequiredComponent {
    public RequiredContentProvider(IntentFilterData intentFilterData, String str, List<RequiredMetaData> list) {
        super(intentFilterData, str, list);
    }

    @Override // com.android.packageinstaller.role.model.RequiredComponent
    protected List<ResolveInfo> queryIntentComponentsAsUser(Intent intent, int i, UserHandle userHandle, Context context) {
        return UserUtils.getUserContext(context, userHandle).getPackageManager().queryIntentContentProviders(intent, i);
    }

    @Override // com.android.packageinstaller.role.model.RequiredComponent
    protected ComponentName getComponentComponentName(ResolveInfo resolveInfo) {
        return new ComponentName(resolveInfo.providerInfo.packageName, resolveInfo.providerInfo.name);
    }

    @Override // com.android.packageinstaller.role.model.RequiredComponent
    protected String getComponentPermission(ResolveInfo resolveInfo) {
        throw new UnsupportedOperationException();
    }

    @Override // com.android.packageinstaller.role.model.RequiredComponent
    protected Bundle getComponentMetaData(ResolveInfo resolveInfo) {
        return resolveInfo.providerInfo.metaData;
    }
}
