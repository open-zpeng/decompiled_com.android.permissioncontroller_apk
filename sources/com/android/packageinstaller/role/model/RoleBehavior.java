package com.android.packageinstaller.role.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.UserHandle;
import androidx.preference.Preference;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public interface RoleBehavior {
    default CharSequence getConfirmationMessage(Role role, String str, Context context) {
        return null;
    }

    default String getFallbackHolder(Role role, Context context) {
        return null;
    }

    default Intent getManageIntentAsUser(Role role, UserHandle userHandle, Context context) {
        return null;
    }

    default List<String> getQualifyingPackagesAsUser(Role role, UserHandle userHandle, Context context) {
        return null;
    }

    default void grant(Role role, String str, Context context) {
    }

    default boolean isApplicationVisibleAsUser(Role role, ApplicationInfo applicationInfo, UserHandle userHandle, Context context) {
        return true;
    }

    default boolean isAvailableAsUser(Role role, UserHandle userHandle, Context context) {
        return true;
    }

    default Boolean isPackageQualified(Role role, String str, Context context) {
        return null;
    }

    default boolean isVisibleAsUser(Role role, UserHandle userHandle, Context context) {
        return true;
    }

    default void onHolderChangedAsUser(Role role, UserHandle userHandle, Context context) {
    }

    default void onHolderSelectedAsUser(Role role, String str, UserHandle userHandle, Context context) {
    }

    default void onRoleAdded(Role role, Context context) {
    }

    default void prepareApplicationPreferenceAsUser(Role role, Preference preference, ApplicationInfo applicationInfo, UserHandle userHandle, Context context) {
    }

    default void preparePreferenceAsUser(Role role, TwoTargetPreference twoTargetPreference, UserHandle userHandle, Context context) {
    }

    default void revoke(Role role, String str, Context context) {
    }

    default List<String> getDefaultHolders(Role role, Context context) {
        return Collections.emptyList();
    }
}
