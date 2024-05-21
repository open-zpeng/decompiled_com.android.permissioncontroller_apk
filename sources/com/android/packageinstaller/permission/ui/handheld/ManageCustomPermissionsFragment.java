package com.android.packageinstaller.permission.ui.handheld;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.preference.Preference;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class ManageCustomPermissionsFragment extends ManagePermissionsFragment {
    @Override // com.android.packageinstaller.permission.ui.handheld.ManagePermissionsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.ManagePermissionsFragment, com.android.packageinstaller.permission.model.PermissionGroups.PermissionsGroupsChangeCallback
    public /* bridge */ /* synthetic */ void onPermissionGroupsChanged() {
        super.onPermissionGroupsChanged();
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.ManagePermissionsFragment, androidx.preference.Preference.OnPreferenceClickListener
    public /* bridge */ /* synthetic */ boolean onPreferenceClick(Preference preference) {
        return super.onPreferenceClick(preference);
    }

    public static ManageCustomPermissionsFragment newInstance(long j) {
        ManageCustomPermissionsFragment manageCustomPermissionsFragment = new ManageCustomPermissionsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("com.android.packageinstaller.extra.SESSION_ID", j);
        manageCustomPermissionsFragment.setArguments(bundle);
        return manageCustomPermissionsFragment;
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.additional_permissions);
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.ManagePermissionsFragment
    protected void updatePermissionsUi() {
        updatePermissionsUi(false);
    }
}
