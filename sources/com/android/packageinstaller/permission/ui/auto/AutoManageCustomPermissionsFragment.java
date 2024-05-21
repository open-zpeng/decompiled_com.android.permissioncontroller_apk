package com.android.packageinstaller.permission.ui.auto;

import android.os.Bundle;
import androidx.preference.Preference;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class AutoManageCustomPermissionsFragment extends AutoManagePermissionsFragment {
    @Override // com.android.packageinstaller.permission.ui.auto.AutoManagePermissionsFragment
    protected int getScreenHeaderRes() {
        return R.string.additional_permissions;
    }

    @Override // com.android.packageinstaller.permission.ui.auto.AutoManagePermissionsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.packageinstaller.permission.ui.auto.AutoManagePermissionsFragment, androidx.preference.PreferenceFragmentCompat
    public /* bridge */ /* synthetic */ void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
    }

    @Override // com.android.packageinstaller.permission.ui.auto.AutoManagePermissionsFragment, com.android.packageinstaller.permission.model.PermissionGroups.PermissionsGroupsChangeCallback
    public /* bridge */ /* synthetic */ void onPermissionGroupsChanged() {
        super.onPermissionGroupsChanged();
    }

    @Override // com.android.packageinstaller.permission.ui.auto.AutoManagePermissionsFragment, androidx.preference.Preference.OnPreferenceClickListener
    public /* bridge */ /* synthetic */ boolean onPreferenceClick(Preference preference) {
        return super.onPreferenceClick(preference);
    }

    @Override // com.android.packageinstaller.permission.ui.auto.AutoManagePermissionsFragment
    protected void updatePermissionsUi() {
        updatePermissionsUi(false);
    }
}
