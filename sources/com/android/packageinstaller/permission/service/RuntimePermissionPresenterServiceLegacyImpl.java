package com.android.packageinstaller.permission.service;

import android.content.Context;
import android.content.pm.permission.RuntimePermissionPresentationInfo;
import android.permissionpresenterservice.RuntimePermissionPresenterService;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class RuntimePermissionPresenterServiceLegacyImpl extends RuntimePermissionPresenterService {
    /* JADX WARN: Multi-variable type inference failed */
    public List<RuntimePermissionPresentationInfo> onGetAppPermissions(String str) {
        List<android.permission.RuntimePermissionPresentationInfo> onGetAppPermissions = PermissionControllerServiceImpl.onGetAppPermissions((Context) this, str);
        ArrayList arrayList = new ArrayList(onGetAppPermissions.size());
        int size = onGetAppPermissions.size();
        for (int i = 0; i < size; i++) {
            android.permission.RuntimePermissionPresentationInfo runtimePermissionPresentationInfo = onGetAppPermissions.get(i);
            arrayList.add(new RuntimePermissionPresentationInfo(runtimePermissionPresentationInfo.getLabel(), runtimePermissionPresentationInfo.isGranted(), runtimePermissionPresentationInfo.isStandard()));
        }
        return arrayList;
    }
}
