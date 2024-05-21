package com.android.packageinstaller.role.model;

import android.app.role.RoleManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.CollectionUtils;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
import com.android.packageinstaller.role.utils.UserUtils;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class HomeRoleBehavior implements RoleBehavior {
    private static final String LOG_TAG = "HomeRoleBehavior";

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isAvailableAsUser(Role role, UserHandle userHandle, Context context) {
        return !UserUtils.isWorkProfile(userHandle, context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public String getFallbackHolder(Role role, Context context) {
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(role.getRequiredComponents().get(0).getIntentFilterData().createIntent(), 851968);
        int size = queryIntentActivities.size();
        int i = Integer.MIN_VALUE;
        String str = null;
        for (int i2 = 0; i2 < size; i2++) {
            ResolveInfo resolveInfo = queryIntentActivities.get(i2);
            if (!isSettingsApplication(resolveInfo.activityInfo.applicationInfo, context)) {
                int i3 = resolveInfo.priority;
                if (i3 > i) {
                    str = resolveInfo.activityInfo.packageName;
                    i = resolveInfo.priority;
                } else if (i3 == i) {
                    str = null;
                }
            }
        }
        return str;
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isVisibleAsUser(Role role, UserHandle userHandle, Context context) {
        return VisibilityMixin.isVisible("config_showDefaultHome", context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public void preparePreferenceAsUser(Role role, TwoTargetPreference twoTargetPreference, UserHandle userHandle, final Context context) {
        final Intent addFlags;
        ActivityInfo resolveActivityInfo;
        String str = (String) CollectionUtils.firstOrNull(((RoleManager) context.getSystemService(RoleManager.class)).getRoleHoldersAsUser(role.getName(), userHandle));
        twoTargetPreference.setOnSecondTargetClickListener((str == null || (resolveActivityInfo = (addFlags = new Intent("android.intent.action.APPLICATION_PREFERENCES").setPackage(str).addFlags(268468224)).resolveActivityInfo(UserUtils.getUserContext(context, userHandle).getPackageManager(), 0)) == null || !resolveActivityInfo.exported) ? null : new TwoTargetPreference.OnSecondTargetClickListener() { // from class: com.android.packageinstaller.role.model.-$$Lambda$HomeRoleBehavior$EZ7hqHSxExe1nMl6nuPwfq7eDrk
            @Override // com.android.packageinstaller.role.ui.TwoTargetPreference.OnSecondTargetClickListener
            public final void onSecondTargetClick(TwoTargetPreference twoTargetPreference2) {
                HomeRoleBehavior.lambda$preparePreferenceAsUser$0(context, addFlags, twoTargetPreference2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$preparePreferenceAsUser$0(Context context, Intent intent, TwoTargetPreference twoTargetPreference) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, "Cannot start activity for home app preferences", e);
        }
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public boolean isApplicationVisibleAsUser(Role role, ApplicationInfo applicationInfo, UserHandle userHandle, Context context) {
        return !isSettingsApplication(applicationInfo, context);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public void prepareApplicationPreferenceAsUser(Role role, Preference preference, ApplicationInfo applicationInfo, UserHandle userHandle, Context context) {
        boolean isMissingWorkProfileSupport = isMissingWorkProfileSupport(applicationInfo, context);
        preference.setEnabled(!isMissingWorkProfileSupport);
        preference.setSummary(isMissingWorkProfileSupport ? context.getString(R.string.home_missing_work_profile_support) : null);
    }

    private boolean isMissingWorkProfileSupport(ApplicationInfo applicationInfo, Context context) {
        if (UserUtils.getWorkProfile(context) != null) {
            return !(applicationInfo.targetSdkVersion >= 21);
        }
        return false;
    }

    private boolean isSettingsApplication(ApplicationInfo applicationInfo, Context context) {
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(new Intent("android.settings.SETTINGS"), 851968);
        if (resolveActivity == null || resolveActivity.activityInfo == null) {
            return false;
        }
        return Objects.equals(applicationInfo.packageName, resolveActivity.activityInfo.packageName);
    }

    @Override // com.android.packageinstaller.role.model.RoleBehavior
    public void onHolderSelectedAsUser(Role role, String str, UserHandle userHandle, Context context) {
        context.startActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").setFlags(268435456));
    }
}
