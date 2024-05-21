package com.android.packageinstaller.permission.ui.auto;

import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.PermissionGroup;
import com.android.packageinstaller.permission.utils.Utils;
/* loaded from: classes.dex */
public class AutoManageStandardPermissionsFragment extends AutoManagePermissionsFragment {
    @Override // com.android.packageinstaller.permission.ui.auto.AutoManagePermissionsFragment
    protected int getScreenHeaderRes() {
        return R.string.app_permission_manager;
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

    public static AutoManageStandardPermissionsFragment newInstance() {
        return new AutoManageStandardPermissionsFragment();
    }

    @Override // com.android.packageinstaller.permission.ui.auto.AutoManagePermissionsFragment
    protected void updatePermissionsUi() {
        updatePermissionsUi(true);
        int i = 0;
        for (PermissionGroup permissionGroup : getPermissions().getGroups()) {
            if (!permissionGroup.getDeclaringPackage().equals("android")) {
                i++;
            }
        }
        Preference findPreference = getPreferenceScreen().findPreference("extra_prefs_key");
        if (i == 0) {
            if (findPreference != null) {
                getPreferenceScreen().removePreference(findPreference);
                return;
            }
            return;
        }
        if (findPreference == null) {
            findPreference = new Preference(getPreferenceManager().getContext());
            findPreference.setKey("extra_prefs_key");
            findPreference.setIcon(Utils.applyTint(getActivity(), (int) R.drawable.ic_more_items, 16843817));
            findPreference.setTitle(R.string.additional_permissions);
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoManageStandardPermissionsFragment$yk3PsmoFdPG-vLDn6gUpEX4kh68
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return AutoManageStandardPermissionsFragment.this.lambda$updatePermissionsUi$0$AutoManageStandardPermissionsFragment(preference);
                }
            });
            getPreferenceScreen().addPreference(findPreference);
        }
        findPreference.setSummary(getResources().getQuantityString(R.plurals.additional_permissions_more, i, Integer.valueOf(i)));
    }

    public /* synthetic */ boolean lambda$updatePermissionsUi$0$AutoManageStandardPermissionsFragment(Preference preference) {
        AutoManageCustomPermissionsFragment autoManageCustomPermissionsFragment = new AutoManageCustomPermissionsFragment();
        autoManageCustomPermissionsFragment.setTargetFragment(this, 0);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, autoManageCustomPermissionsFragment);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
        return true;
    }
}
