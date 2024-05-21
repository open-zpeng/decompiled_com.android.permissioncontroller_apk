package com.android.packageinstaller.permission.ui.handheld;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.PermissionGroup;
import com.android.packageinstaller.permission.utils.Utils;
/* loaded from: classes.dex */
public final class ManageStandardPermissionsFragment extends ManagePermissionsFragment {
    @Override // com.android.packageinstaller.permission.ui.handheld.ManagePermissionsFragment, com.android.packageinstaller.permission.model.PermissionGroups.PermissionsGroupsChangeCallback
    public /* bridge */ /* synthetic */ void onPermissionGroupsChanged() {
        super.onPermissionGroupsChanged();
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.ManagePermissionsFragment, androidx.preference.Preference.OnPreferenceClickListener
    public /* bridge */ /* synthetic */ boolean onPreferenceClick(Preference preference) {
        return super.onPreferenceClick(preference);
    }

    public static ManageStandardPermissionsFragment newInstance(long j) {
        ManageStandardPermissionsFragment manageStandardPermissionsFragment = new ManageStandardPermissionsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("com.android.packageinstaller.extra.SESSION_ID", j);
        manageStandardPermissionsFragment.setArguments(bundle);
        return manageStandardPermissionsFragment;
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.ManagePermissionsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.app_permission_manager);
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.ManagePermissionsFragment
    protected void updatePermissionsUi() {
        PreferenceScreen updatePermissionsUi = updatePermissionsUi(true);
        if (updatePermissionsUi == null) {
            return;
        }
        int i = 0;
        for (PermissionGroup permissionGroup : getPermissions().getGroups()) {
            if (!permissionGroup.getDeclaringPackage().equals("android")) {
                i++;
            }
        }
        Preference findPreference = updatePermissionsUi.findPreference("extra_prefs_key");
        if (i == 0) {
            if (findPreference != null) {
                updatePermissionsUi.removePreference(findPreference);
                return;
            }
            return;
        }
        if (findPreference == null) {
            findPreference = new Preference(getPreferenceManager().getContext());
            findPreference.setKey("extra_prefs_key");
            findPreference.setIcon(Utils.applyTint(getActivity(), (int) R.drawable.ic_more_items, 16843817));
            findPreference.setTitle(R.string.additional_permissions);
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$ManageStandardPermissionsFragment$ES6kiejOpYZyu0q79N_46T7rtgY
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return ManageStandardPermissionsFragment.this.lambda$updatePermissionsUi$0$ManageStandardPermissionsFragment(preference);
                }
            });
            updatePermissionsUi.addPreference(findPreference);
        }
        findPreference.setSummary(getResources().getQuantityString(R.plurals.additional_permissions_more, i, Integer.valueOf(i)));
    }

    public /* synthetic */ boolean lambda$updatePermissionsUi$0$ManageStandardPermissionsFragment(Preference preference) {
        ManageCustomPermissionsFragment newInstance = ManageCustomPermissionsFragment.newInstance(getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID"));
        newInstance.setTargetFragment(this, 0);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, newInstance);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
        return true;
    }
}
