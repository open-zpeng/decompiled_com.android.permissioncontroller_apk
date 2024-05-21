package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.UserHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import androidx.preference.Preference;
import com.android.car.ui.R;
import java.util.Objects;
/* loaded from: classes.dex */
public class DialerRoleBehavior implements RoleBehavior {
    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isAvailableAsUser(Role role, UserHandle userHandle, Context context) {
        return ((TelephonyManager) context.getSystemService(TelephonyManager.class)).isVoiceCapable();
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public void prepareApplicationPreferenceAsUser(Role role, Preference preference, ApplicationInfo applicationInfo, UserHandle userHandle, Context context) {
        if (Objects.equals(applicationInfo.packageName, ((TelecomManager) context.getSystemService(TelecomManager.class)).getSystemDialerPackage())) {
            preference.setSummary(R.string.default_app_system_default);
        } else {
            preference.setSummary((CharSequence) null);
        }
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public CharSequence getConfirmationMessage(Role role, String str, Context context) {
        return EncryptionUnawareConfirmationMixin.getConfirmationMessage(role, str, context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public String getFallbackHolder(Role role, Context context) {
        return ExclusiveDefaultHolderMixin.getDefaultHolder(role, "config_defaultDialer", context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isVisibleAsUser(Role role, UserHandle userHandle, Context context) {
        return context.getResources().getBoolean(R.bool.config_showDialerRole);
    }
}
