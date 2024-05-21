package com.android.packageinstaller.permission.ui.television;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.PermissionApps;
import com.android.packageinstaller.permission.model.PermissionGroup;
import com.android.packageinstaller.permission.model.PermissionGroups;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.List;
/* loaded from: classes.dex */
public final class ManagePermissionsFragment extends SettingsWithHeader implements PermissionGroups.PermissionsGroupsChangeCallback, Preference.OnPreferenceClickListener {
    private PreferenceScreen mExtraScreen;
    private PermissionGroups mPermissions;

    public static ManagePermissionsFragment newInstance() {
        return new ManagePermissionsFragment();
    }

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLoading(true, false);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.mPermissions = new PermissionGroups(getContext(), getLoaderManager(), this, false, true);
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (this.mPermissions.getGroup(key) == null) {
            return false;
        }
        Intent putExtra = new Intent("android.intent.action.MANAGE_PERMISSION_APPS").putExtra("android.intent.extra.PERMISSION_NAME", key);
        try {
            getActivity().startActivity(putExtra);
            return true;
        } catch (ActivityNotFoundException unused) {
            Log.w("ManagePermissionsFragment", "No app to handle " + putExtra);
            return true;
        }
    }

    @Override // com.android.packageinstaller.permission.model.PermissionGroups.PermissionsGroupsChangeCallback
    public void onPermissionGroupsChanged() {
        updatePermissionsUi();
    }

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        bindPermissionUi(this, getView());
    }

    private static void bindPermissionUi(SettingsWithHeader settingsWithHeader, View view) {
        if (settingsWithHeader == null || view == null) {
            return;
        }
        settingsWithHeader.setHeader(null, null, null, settingsWithHeader.getString(R.string.manage_permissions_decor_title));
    }

    private void updatePermissionsUi() {
        PreferenceScreen preferenceScreen;
        Context context = getPreferenceManager().getContext();
        if (context == null) {
            return;
        }
        List<PermissionGroup> groups = this.mPermissions.getGroups();
        PreferenceScreen preferenceScreen2 = getPreferenceScreen();
        new PermissionApps.PmCache(getContext().getPackageManager());
        for (PermissionGroup permissionGroup : groups) {
            boolean equals = permissionGroup.getDeclaringPackage().equals("android");
            Preference findPreference = findPreference(permissionGroup.getName());
            if (findPreference == null && (preferenceScreen = this.mExtraScreen) != null) {
                findPreference = preferenceScreen.findPreference(permissionGroup.getName());
            }
            if (findPreference == null) {
                findPreference = new Preference(context);
                findPreference.setOnPreferenceClickListener(this);
                findPreference.setKey(permissionGroup.getName());
                findPreference.setIcon(Utils.applyTint(context, permissionGroup.getIcon(), 16843817));
                findPreference.setTitle(permissionGroup.getLabel());
                findPreference.setSummary(" ");
                findPreference.setPersistent(false);
                if (equals) {
                    preferenceScreen2.addPreference(findPreference);
                } else {
                    if (this.mExtraScreen == null) {
                        this.mExtraScreen = getPreferenceManager().createPreferenceScreen(context);
                    }
                    this.mExtraScreen.addPreference(findPreference);
                }
            }
            findPreference.setSummary(getString(R.string.app_permissions_group_summary, Integer.valueOf(permissionGroup.getGranted()), Integer.valueOf(permissionGroup.getTotal())));
        }
        PreferenceScreen preferenceScreen3 = this.mExtraScreen;
        if (preferenceScreen3 != null && preferenceScreen3.getPreferenceCount() > 0 && preferenceScreen2.findPreference("extra_prefs_key") == null) {
            Preference preference = new Preference(context);
            preference.setKey("extra_prefs_key");
            preference.setIcon(Utils.applyTint(context, (int) R.drawable.ic_more_items, 16843817));
            preference.setTitle(R.string.additional_permissions);
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.television.ManagePermissionsFragment.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference2) {
                    AdditionalPermissionsFragment additionalPermissionsFragment = new AdditionalPermissionsFragment();
                    additionalPermissionsFragment.setTargetFragment(ManagePermissionsFragment.this, 0);
                    FragmentTransaction beginTransaction = ManagePermissionsFragment.this.getFragmentManager().beginTransaction();
                    beginTransaction.replace(16908290, additionalPermissionsFragment);
                    beginTransaction.addToBackStack(null);
                    beginTransaction.commit();
                    return true;
                }
            });
            int preferenceCount = this.mExtraScreen.getPreferenceCount();
            preference.setSummary(getResources().getQuantityString(R.plurals.additional_permissions_more, preferenceCount, Integer.valueOf(preferenceCount)));
            preferenceScreen2.addPreference(preference);
        }
        if (preferenceScreen2.getPreferenceCount() != 0) {
            setLoading(false, true);
        }
    }

    /* loaded from: classes.dex */
    public static class AdditionalPermissionsFragment extends SettingsWithHeader {
        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle bundle) {
            setLoading(true, false);
            super.onCreate(bundle);
            getActivity().setTitle(R.string.additional_permissions);
            setHasOptionsMenu(true);
        }

        @Override // android.app.Fragment
        public void onDestroy() {
            getActivity().setTitle(R.string.app_permissions);
            super.onDestroy();
        }

        @Override // android.app.Fragment
        public boolean onOptionsItemSelected(MenuItem menuItem) {
            if (menuItem.getItemId() == 16908332) {
                getFragmentManager().popBackStack();
                return true;
            }
            return super.onOptionsItemSelected(menuItem);
        }

        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            bindPermissionUi(this, getView());
        }

        private static void bindPermissionUi(SettingsWithHeader settingsWithHeader, View view) {
            if (settingsWithHeader == null || view == null) {
                return;
            }
            settingsWithHeader.setHeader(null, null, null, settingsWithHeader.getString(R.string.additional_permissions_decor_title));
        }

        @Override // com.android.packageinstaller.permission.ui.television.PermissionsFrameFragment, androidx.preference.PreferenceFragment
        public void onCreatePreferences(Bundle bundle, String str) {
            setPreferenceScreen(((ManagePermissionsFragment) getTargetFragment()).mExtraScreen);
            setLoading(false, true);
        }
    }
}
