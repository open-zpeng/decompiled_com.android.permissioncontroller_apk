package com.android.packageinstaller.permission.ui.television;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.ui.ReviewPermissionsActivity;
import com.android.packageinstaller.permission.utils.LocationUtils;
import com.android.packageinstaller.permission.utils.SafetyNetLogger;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.Iterator;
/* loaded from: classes.dex */
public final class AppPermissionsFragment extends SettingsWithHeader implements Preference.OnPreferenceChangeListener {
    private AppPermissions mAppPermissions;
    private PreferenceScreen mExtraScreen;
    private boolean mHasConfirmedRevoke;
    private ArraySet<AppPermissionGroup> mToggledGroups;

    public static AppPermissionsFragment newInstance(String str) {
        AppPermissionsFragment appPermissionsFragment = new AppPermissionsFragment();
        setPackageName(appPermissionsFragment, str);
        return appPermissionsFragment;
    }

    private static <T extends Fragment> T setPackageName(T t, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        t.setArguments(bundle);
        return t;
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
        String string = getArguments().getString("android.intent.extra.PACKAGE_NAME");
        Activity activity = getActivity();
        PackageInfo packageInfo = getPackageInfo(activity, string);
        if (packageInfo == null) {
            Toast.makeText(activity, (int) R.string.app_not_found_dlg_title, 1).show();
            activity.finish();
            return;
        }
        this.mAppPermissions = new AppPermissions(activity, packageInfo, true, new Runnable() { // from class: com.android.packageinstaller.permission.ui.television.-$$Lambda$AppPermissionsFragment$NOhr4H4dxc9GxdGF6rJdJrZ5rCg
            @Override // java.lang.Runnable
            public final void run() {
                AppPermissionsFragment.this.lambda$onCreate$0$AppPermissionsFragment();
            }
        });
        if (this.mAppPermissions.isReviewRequired()) {
            Intent intent = new Intent(getActivity(), ReviewPermissionsActivity.class);
            intent.putExtra("android.intent.extra.PACKAGE_NAME", string);
            startActivity(intent);
            getActivity().finish();
            return;
        }
        loadPreferences();
    }

    public /* synthetic */ void lambda$onCreate$0$AppPermissionsFragment() {
        getActivity().finish();
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppPermissions.refresh();
        loadPreferences();
        setPreferencesCheckedState();
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 0) {
            getFragmentManager().beginTransaction().replace(16908290, AllAppPermissionsFragment.newInstance(getArguments().getString("android.intent.extra.PACKAGE_NAME"))).addToBackStack("AllPerms").commit();
            return true;
        } else if (itemId == 16908332) {
            getActivity().finish();
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        AppPermissions appPermissions = this.mAppPermissions;
        if (appPermissions != null) {
            bindUi(this, appPermissions.getPackageInfo());
        }
    }

    @Override // android.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.add(0, 0, 0, R.string.all_permissions);
    }

    private static void bindUi(SettingsWithHeader settingsWithHeader, PackageInfo packageInfo) {
        Activity activity = settingsWithHeader.getActivity();
        PackageManager packageManager = activity.getPackageManager();
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        settingsWithHeader.setHeader(applicationInfo.loadIcon(packageManager), applicationInfo.loadLabel(packageManager), activity.getIntent().getBooleanExtra("hideInfoButton", false) ? null : new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", packageInfo.packageName, null)), settingsWithHeader.getString(R.string.app_permissions_decor_title));
    }

    private void loadPreferences() {
        Context context = getPreferenceManager().getContext();
        if (context == null) {
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        preferenceScreen.addPreference(createHeaderLineTwoPreference(context));
        PreferenceScreen preferenceScreen2 = this.mExtraScreen;
        if (preferenceScreen2 != null) {
            preferenceScreen2.removeAll();
            this.mExtraScreen = null;
        }
        Preference preference = new Preference(context);
        preference.setIcon(R.drawable.ic_toc);
        preference.setTitle(R.string.additional_permissions);
        Iterator<AppPermissionGroup> it = this.mAppPermissions.getPermissionGroups().iterator();
        while (true) {
            boolean z = true;
            if (!it.hasNext()) {
                break;
            }
            AppPermissionGroup next = it.next();
            if (Utils.shouldShowPermission(getContext(), next)) {
                boolean equals = next.getDeclaringPackage().equals("android");
                SwitchPreference switchPreference = new SwitchPreference(context);
                switchPreference.setOnPreferenceChangeListener(this);
                switchPreference.setKey(next.getName());
                switchPreference.setIcon(Utils.applyTint(getContext(), Utils.loadDrawable(context.getPackageManager(), next.getIconPkg(), next.getIconResId()), 16843817));
                switchPreference.setTitle(next.getLabel());
                if (next.isSystemFixed()) {
                    switchPreference.setSummary(getString(R.string.permission_summary_enabled_system_fixed));
                } else if (next.isPolicyFixed()) {
                    switchPreference.setSummary(getString(R.string.permission_summary_enforced_by_policy));
                }
                switchPreference.setPersistent(false);
                if (next.isSystemFixed() || next.isPolicyFixed()) {
                    z = false;
                }
                switchPreference.setEnabled(z);
                switchPreference.setChecked(next.areRuntimePermissionsGranted());
                if (equals) {
                    preferenceScreen.addPreference(switchPreference);
                } else {
                    if (this.mExtraScreen == null) {
                        this.mExtraScreen = getPreferenceManager().createPreferenceScreen(context);
                        this.mExtraScreen.addPreference(createHeaderLineTwoPreference(context));
                    }
                    this.mExtraScreen.addPreference(switchPreference);
                }
            }
        }
        if (this.mExtraScreen != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.television.-$$Lambda$AppPermissionsFragment$7R7eCpTQNcJhCr6iqLeVMh82-ms
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference2) {
                    return AppPermissionsFragment.this.lambda$loadPreferences$1$AppPermissionsFragment(preference2);
                }
            });
            int preferenceCount = this.mExtraScreen.getPreferenceCount() - 1;
            preference.setSummary(getResources().getQuantityString(R.plurals.additional_permissions_more, preferenceCount, Integer.valueOf(preferenceCount)));
            preferenceScreen.addPreference(preference);
        }
        setLoading(false, true);
    }

    public /* synthetic */ boolean lambda$loadPreferences$1$AppPermissionsFragment(Preference preference) {
        AdditionalPermissionsFragment additionalPermissionsFragment = new AdditionalPermissionsFragment();
        setPackageName(additionalPermissionsFragment, getArguments().getString("android.intent.extra.PACKAGE_NAME"));
        additionalPermissionsFragment.setTargetFragment(this, 0);
        getFragmentManager().beginTransaction().replace(16908290, additionalPermissionsFragment).addToBackStack(null).commit();
        return true;
    }

    private Preference createHeaderLineTwoPreference(Context context) {
        Preference preference = new Preference(context) { // from class: com.android.packageinstaller.permission.ui.television.AppPermissionsFragment.1
            @Override // androidx.preference.Preference
            public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
                super.onBindViewHolder(preferenceViewHolder);
                preferenceViewHolder.itemView.setBackgroundColor(AppPermissionsFragment.this.getResources().getColor(R.color.lb_header_banner_color));
            }
        };
        preference.setKey("HeaderPreferenceKey");
        preference.setSelectable(false);
        preference.setTitle(this.mLabel);
        preference.setIcon(this.mIcon);
        return preference;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(final Preference preference, Object obj) {
        final AppPermissionGroup permissionGroup = this.mAppPermissions.getPermissionGroup(preference.getKey());
        if (permissionGroup == null) {
            return false;
        }
        addToggledGroup(permissionGroup);
        if (LocationUtils.isLocationGroupAndProvider(getContext(), permissionGroup.getName(), permissionGroup.getApp().packageName)) {
            LocationUtils.showLocationDialog(getContext(), this.mAppPermissions.getAppLabel());
            return false;
        } else if (obj == Boolean.TRUE) {
            permissionGroup.grantRuntimePermissions(false);
            return true;
        } else {
            final boolean hasGrantedByDefaultPermission = permissionGroup.hasGrantedByDefaultPermission();
            if (hasGrantedByDefaultPermission || (!permissionGroup.doesSupportRuntimePermissions() && !this.mHasConfirmedRevoke)) {
                new AlertDialog.Builder(getContext()).setMessage(hasGrantedByDefaultPermission ? R.string.system_warning : R.string.old_sdk_deny_warning).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.television.-$$Lambda$AppPermissionsFragment$BtSRokva2daggZeHv3S0gd2u53o
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AppPermissionsFragment.this.lambda$onPreferenceChange$2$AppPermissionsFragment(preference, permissionGroup, hasGrantedByDefaultPermission, dialogInterface, i);
                    }
                }).show();
                return false;
            }
            permissionGroup.revokeRuntimePermissions(false);
            return true;
        }
    }

    public /* synthetic */ void lambda$onPreferenceChange$2$AppPermissionsFragment(Preference preference, AppPermissionGroup appPermissionGroup, boolean z, DialogInterface dialogInterface, int i) {
        ((SwitchPreference) preference).setChecked(false);
        appPermissionGroup.revokeRuntimePermissions(false);
        if (z) {
            return;
        }
        this.mHasConfirmedRevoke = true;
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        logToggledGroups();
    }

    private void addToggledGroup(AppPermissionGroup appPermissionGroup) {
        if (this.mToggledGroups == null) {
            this.mToggledGroups = new ArraySet<>();
        }
        this.mToggledGroups.add(appPermissionGroup);
    }

    private void logToggledGroups() {
        ArraySet<AppPermissionGroup> arraySet = this.mToggledGroups;
        if (arraySet != null) {
            SafetyNetLogger.logPermissionsToggled(arraySet);
            this.mToggledGroups = null;
        }
    }

    private void setPreferencesCheckedState() {
        setPreferencesCheckedState(getPreferenceScreen());
        PreferenceScreen preferenceScreen = this.mExtraScreen;
        if (preferenceScreen != null) {
            setPreferencesCheckedState(preferenceScreen);
        }
    }

    private void setPreferencesCheckedState(PreferenceScreen preferenceScreen) {
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (preference instanceof SwitchPreference) {
                SwitchPreference switchPreference = (SwitchPreference) preference;
                AppPermissionGroup permissionGroup = this.mAppPermissions.getPermissionGroup(switchPreference.getKey());
                if (permissionGroup != null) {
                    switchPreference.setChecked(permissionGroup.areRuntimePermissionsGranted());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PackageInfo getPackageInfo(Activity activity, String str) {
        try {
            return activity.getPackageManager().getPackageInfo(str, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("ManagePermsFragment", "No package:" + activity.getCallingPackage(), e);
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class AdditionalPermissionsFragment extends SettingsWithHeader {
        AppPermissionsFragment mOuterFragment;

        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle bundle) {
            this.mOuterFragment = (AppPermissionsFragment) getTargetFragment();
            super.onCreate(bundle);
            setHasOptionsMenu(true);
        }

        @Override // com.android.packageinstaller.permission.ui.television.PermissionsFrameFragment, androidx.preference.PreferenceFragment
        public void onCreatePreferences(Bundle bundle, String str) {
            setPreferenceScreen(this.mOuterFragment.mExtraScreen);
        }

        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            bindUi(this, AppPermissionsFragment.getPackageInfo(getActivity(), getArguments().getString("android.intent.extra.PACKAGE_NAME")));
        }

        private static void bindUi(SettingsWithHeader settingsWithHeader, PackageInfo packageInfo) {
            Activity activity = settingsWithHeader.getActivity();
            PackageManager packageManager = activity.getPackageManager();
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            settingsWithHeader.setHeader(applicationInfo.loadIcon(packageManager), applicationInfo.loadLabel(packageManager), activity.getIntent().getBooleanExtra("hideInfoButton", false) ? null : new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", packageInfo.packageName, null)), settingsWithHeader.getString(R.string.additional_permissions_decor_title));
        }

        @Override // android.app.Fragment
        public boolean onOptionsItemSelected(MenuItem menuItem) {
            if (menuItem.getItemId() == 16908332) {
                getFragmentManager().popBackStack();
                return true;
            }
            return super.onOptionsItemSelected(menuItem);
        }
    }
}
