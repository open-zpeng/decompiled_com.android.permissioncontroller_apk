package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Process;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.List;
/* loaded from: classes.dex */
public class EmergencyRoleBehavior implements RoleBehavior {
    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isAvailableAsUser(Role role, UserHandle userHandle, Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        return telephonyManager.isEmergencyAssistanceEnabled() && telephonyManager.isVoiceCapable();
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public String getFallbackHolder(Role role, Context context) {
        List<String> qualifyingPackagesAsUser = role.getQualifyingPackagesAsUser(Process.myUserHandle(), context);
        int size = qualifyingPackagesAsUser.size();
        PackageInfo packageInfo = null;
        for (int i = 0; i < size; i++) {
            PackageInfo packageInfo2 = PackageUtils.getPackageInfo(qualifyingPackagesAsUser.get(i), 0, context);
            if (packageInfo2 != null && (packageInfo == null || packageInfo2.firstInstallTime < packageInfo.firstInstallTime)) {
                packageInfo = packageInfo2;
            }
        }
        if (packageInfo != null) {
            return packageInfo.packageName;
        }
        return null;
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isVisibleAsUser(Role role, UserHandle userHandle, Context context) {
        return VisibilityMixin.isVisible("config_showDefaultEmergency", context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public CharSequence getConfirmationMessage(Role role, String str, Context context) {
        return EncryptionUnawareConfirmationMixin.getConfirmationMessage(role, str, context);
    }
}
