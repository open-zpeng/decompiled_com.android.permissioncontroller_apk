package com.android.packageinstaller.role.model;

import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.TelephonyManager;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.CollectionUtils;
import com.android.packageinstaller.role.utils.UserUtils;
/* loaded from: classes.dex */
public class SmsRoleBehavior implements RoleBehavior {
    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isAvailableAsUser(Role role, UserHandle userHandle, Context context) {
        if (UserUtils.isWorkProfile(userHandle, context) || ((UserManager) context.getSystemService(UserManager.class)).isRestrictedProfile(userHandle)) {
            return false;
        }
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).isSmsCapable() || getDefaultHolder(role, context) != null;
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public String getFallbackHolder(Role role, Context context) {
        String defaultHolder = getDefaultHolder(role, context);
        return defaultHolder != null ? defaultHolder : (String) CollectionUtils.firstOrNull(role.getQualifyingPackagesAsUser(Process.myUserHandle(), context));
    }

    private static String getDefaultHolder(Role role, Context context) {
        return ExclusiveDefaultHolderMixin.getDefaultHolder(role, "config_defaultSms", context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public CharSequence getConfirmationMessage(Role role, String str, Context context) {
        return EncryptionUnawareConfirmationMixin.getConfirmationMessage(role, str, context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isVisibleAsUser(Role role, UserHandle userHandle, Context context) {
        return context.getResources().getBoolean(R.bool.config_showSmsRole);
    }
}
