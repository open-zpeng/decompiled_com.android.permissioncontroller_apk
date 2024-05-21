package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArraySet;
import com.android.car.ui.R;
import com.android.packageinstaller.role.utils.UserUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class BrowserRoleBehavior implements RoleBehavior {
    private static final Intent BROWSER_INTENT = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.fromParts("http", "", null));

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public List<String> getDefaultHolders(Role role, Context context) {
        return ExclusiveDefaultHolderMixin.getDefaultHolders(role, "config_defaultBrowser", context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public String getFallbackHolder(Role role, Context context) {
        List<String> qualifyingPackagesAsUser = role.getQualifyingPackagesAsUser(Process.myUserHandle(), context);
        if (qualifyingPackagesAsUser.size() == 1) {
            return qualifyingPackagesAsUser.get(0);
        }
        return null;
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public List<String> getQualifyingPackagesAsUser(Role role, UserHandle userHandle, Context context) {
        return getQualifyingPackagesAsUserInternal(null, userHandle, context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public Boolean isPackageQualified(Role role, String str, Context context) {
        return Boolean.valueOf(!getQualifyingPackagesAsUserInternal(str, Process.myUserHandle(), context).isEmpty());
    }

    private List<String> getQualifyingPackagesAsUserInternal(String str, UserHandle userHandle, Context context) {
        PackageManager packageManager = UserUtils.getUserContext(context, userHandle).getPackageManager();
        Intent intent = BROWSER_INTENT;
        if (str != null) {
            intent = new Intent(intent).setPackage(str);
        }
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 917504);
        ArraySet arraySet = new ArraySet();
        int size = queryIntentActivities.size();
        for (int i = 0; i < size; i++) {
            ResolveInfo resolveInfo = queryIntentActivities.get(i);
            if (resolveInfo.handleAllWebDataURI) {
                arraySet.add(resolveInfo.activityInfo.packageName);
            }
        }
        return new ArrayList(arraySet);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isVisibleAsUser(Role role, UserHandle userHandle, Context context) {
        return context.getResources().getBoolean(R.bool.config_showBrowserRole);
    }
}
