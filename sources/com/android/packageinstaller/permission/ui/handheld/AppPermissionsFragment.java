package com.android.packageinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.car.ui.R;
import com.android.packageinstaller.PermissionControllerStatsLog;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.settingslib.HelpUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
/* loaded from: classes.dex */
public final class AppPermissionsFragment extends SettingsWithLargeHeader {
    private AppPermissions mAppPermissions;
    private Collator mCollator;
    private PreferenceScreen mExtraScreen;

    public static AppPermissionsFragment newInstance(String str, UserHandle userHandle, long j) {
        AppPermissionsFragment appPermissionsFragment = new AppPermissionsFragment();
        setPackageNameAndUserHandleAndSessionId(appPermissionsFragment, str, userHandle, j);
        return appPermissionsFragment;
    }

    private static <T extends Fragment> T setPackageNameAndUserHandleAndSessionId(T t, String str, UserHandle userHandle, long j) {
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        bundle.putLong("com.android.packageinstaller.extra.SESSION_ID", j);
        t.setArguments(bundle);
        return t;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLoading(true, false);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FragmentActivity activity = getActivity();
        PackageInfo packageInfo = getPackageInfo(activity, getArguments().getString("android.intent.extra.PACKAGE_NAME"), (UserHandle) getArguments().getParcelable("android.intent.extra.USER"));
        if (packageInfo == null) {
            Toast.makeText(activity, (int) R.string.app_not_found_dlg_title, 1).show();
            activity.finish();
            return;
        }
        addPreferencesFromResource(R.xml.allowed_denied);
        this.mAppPermissions = new AppPermissions(activity, packageInfo, true, new Runnable() { // from class: com.android.packageinstaller.permission.ui.handheld.AppPermissionsFragment.1
            @Override // java.lang.Runnable
            public void run() {
                AppPermissionsFragment.this.getActivity().finish();
            }
        });
        this.mCollator = Collator.getInstance(getContext().getResources().getConfiguration().getLocales().get(0));
        updatePreferences();
        logAppPermissionsFragmentView();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppPermissions.refresh();
        updatePreferences();
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2) {
            showAllPermissions(null);
            return true;
        } else if (itemId == 16908332) {
            getActivity().finish();
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        AppPermissions appPermissions = this.mAppPermissions;
        if (appPermissions != null) {
            bindUi(this, appPermissions.getPackageInfo());
        }
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.add(0, 2, 0, R.string.all_permissions);
        HelpUtils.prepareHelpMenuItem(getActivity(), menu, (int) R.string.help_app_permissions, AppPermissionsFragment.class.getName());
    }

    private void showAllPermissions(String str) {
        AllAppPermissionsFragment newInstance = AllAppPermissionsFragment.newInstance(getArguments().getString("android.intent.extra.PACKAGE_NAME"), str, (UserHandle) getArguments().getParcelable("android.intent.extra.USER"));
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, newInstance);
        beginTransaction.addToBackStack("AllPerms");
        beginTransaction.commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void bindUi(SettingsWithLargeHeader settingsWithLargeHeader, PackageInfo packageInfo) {
        FragmentActivity activity = settingsWithLargeHeader.getActivity();
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        settingsWithLargeHeader.setHeader(Utils.getBadgedIcon(activity, applicationInfo), Utils.getFullAppLabel(applicationInfo, activity), activity.getIntent().getBooleanExtra("hideInfoButton", false) ? null : new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", packageInfo.packageName, null)), UserHandle.getUserHandleForUid(applicationInfo.uid), false);
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_permissions);
        }
    }

    private void updatePreferences() {
        boolean z;
        int i;
        Context context = getPreferenceManager().getContext();
        if (context == null) {
            return;
        }
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("allowed");
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("denied");
        preferenceCategory.removeAll();
        preferenceCategory2.removeAll();
        findPreference("allowed_foreground").setVisible(false);
        PreferenceScreen preferenceScreen = this.mExtraScreen;
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        Preference preference = new Preference(context);
        preference.setIcon(R.drawable.ic_toc);
        preference.setTitle(R.string.additional_permissions);
        ArrayList arrayList = new ArrayList(this.mAppPermissions.getPermissionGroups());
        arrayList.sort(new Comparator() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionsFragment$879IxEdwHKeoVQXQy6hSimYCpWU
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AppPermissionsFragment.this.lambda$updatePreferences$0$AppPermissionsFragment((AppPermissionGroup) obj, (AppPermissionGroup) obj2);
            }
        });
        preferenceCategory.setOrderingAsAdded(true);
        preferenceCategory2.setOrderingAsAdded(true);
        long j = getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID", 0L);
        int i2 = 0;
        boolean z2 = false;
        while (i2 < arrayList.size()) {
            AppPermissionGroup appPermissionGroup = (AppPermissionGroup) arrayList.get(i2);
            if (Utils.shouldShowPermission(getContext(), appPermissionGroup)) {
                boolean equals = appPermissionGroup.getDeclaringPackage().equals("android");
                i = i2;
                PermissionControlPreference permissionControlPreference = new PermissionControlPreference(context, appPermissionGroup, AppPermissionsFragment.class.getName(), j);
                permissionControlPreference.setKey(appPermissionGroup.getName());
                permissionControlPreference.setIcon(Utils.applyTint(context, Utils.loadDrawable(context.getPackageManager(), appPermissionGroup.getIconPkg(), appPermissionGroup.getIconResId()), 16843817));
                permissionControlPreference.setTitle(appPermissionGroup.getFullLabel());
                permissionControlPreference.setGroupSummary(appPermissionGroup);
                if (equals) {
                    (appPermissionGroup.areRuntimePermissionsGranted() ? preferenceCategory : preferenceCategory2).addPreference(permissionControlPreference);
                } else {
                    if (this.mExtraScreen == null) {
                        this.mExtraScreen = getPreferenceManager().createPreferenceScreen(context);
                    }
                    this.mExtraScreen.addPreference(permissionControlPreference);
                    if (appPermissionGroup.areRuntimePermissionsGranted()) {
                        z2 = true;
                    }
                }
            } else {
                i = i2;
            }
            i2 = i + 1;
        }
        if (this.mExtraScreen != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AppPermissionsFragment$nYZYs2nI-TQL8pQMhlv1JXS9EAo
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference2) {
                    return AppPermissionsFragment.this.lambda$updatePreferences$1$AppPermissionsFragment(preference2);
                }
            });
            int preferenceCount = this.mExtraScreen.getPreferenceCount();
            preference.setSummary(getResources().getQuantityString(R.plurals.additional_permissions_more, preferenceCount, Integer.valueOf(preferenceCount)));
            (z2 ? preferenceCategory : preferenceCategory2).addPreference(preference);
        }
        if (preferenceCategory.getPreferenceCount() == 0) {
            Preference preference2 = new Preference(context);
            preference2.setTitle(getString(R.string.no_permissions_allowed));
            z = false;
            preference2.setSelectable(false);
            preferenceCategory.addPreference(preference2);
        } else {
            z = false;
        }
        if (preferenceCategory2.getPreferenceCount() == 0) {
            Preference preference3 = new Preference(context);
            preference3.setTitle(getString(R.string.no_permissions_denied));
            preference3.setSelectable(z);
            preferenceCategory2.addPreference(preference3);
        }
        setLoading(z, true);
    }

    public /* synthetic */ int lambda$updatePreferences$0$AppPermissionsFragment(AppPermissionGroup appPermissionGroup, AppPermissionGroup appPermissionGroup2) {
        return this.mCollator.compare(appPermissionGroup.getLabel(), appPermissionGroup2.getLabel());
    }

    public /* synthetic */ boolean lambda$updatePreferences$1$AppPermissionsFragment(Preference preference) {
        AdditionalPermissionsFragment additionalPermissionsFragment = new AdditionalPermissionsFragment();
        setPackageNameAndUserHandleAndSessionId(additionalPermissionsFragment, getArguments().getString("android.intent.extra.PACKAGE_NAME"), (UserHandle) getArguments().getParcelable("android.intent.extra.USER"), getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID", 0L));
        additionalPermissionsFragment.setTargetFragment(this, 0);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, additionalPermissionsFragment);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
        return true;
    }

    private void logAppPermissionsFragmentView() {
        int i;
        Context context = getPreferenceManager().getContext();
        if (context == null) {
            return;
        }
        String string = context.getString(R.string.permission_subtitle_only_in_foreground);
        long j = getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID", 0L);
        long nextLong = new Random().nextLong();
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("allowed");
        int preferenceCount = preferenceCategory.getPreferenceCount();
        int i2 = 0;
        while (i2 < preferenceCount) {
            Preference preference = preferenceCategory.getPreference(i2);
            if (preference.getSummary() == null) {
                i = i2;
            } else {
                i = i2;
                logAppPermissionsFragmentViewEntry(j, nextLong, preference.getKey(), string.contentEquals(preference.getSummary()) ? 2 : 1);
            }
            i2 = i + 1;
        }
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("denied");
        int preferenceCount2 = preferenceCategory2.getPreferenceCount();
        for (int i3 = 0; i3 < preferenceCount2; i3++) {
            Preference preference2 = preferenceCategory2.getPreference(i3);
            if (preference2.getSummary() != null) {
                logAppPermissionsFragmentViewEntry(j, nextLong, preference2.getKey(), 3);
            }
        }
    }

    private void logAppPermissionsFragmentViewEntry(long j, long j2, String str, int i) {
        PermissionControllerStatsLog.write(217, j, j2, str, this.mAppPermissions.getPackageInfo().applicationInfo.uid, this.mAppPermissions.getPackageInfo().packageName, i);
        Log.v("ManagePermsFragment", "AppPermissionFragment view logged with sessionId=" + j + " viewId=" + j2 + " permissionGroupName=" + str + " uid=" + this.mAppPermissions.getPackageInfo().applicationInfo.uid + " packageName=" + this.mAppPermissions.getPackageInfo().packageName + " category=" + i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PackageInfo getPackageInfo(Activity activity, String str, UserHandle userHandle) {
        try {
            return activity.createPackageContextAsUser(str, 0, userHandle).getPackageManager().getPackageInfo(str, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("ManagePermsFragment", "No package:" + activity.getCallingPackage(), e);
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class AdditionalPermissionsFragment extends SettingsWithLargeHeader {
        AppPermissionsFragment mOuterFragment;

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            this.mOuterFragment = (AppPermissionsFragment) getTargetFragment();
            super.onCreate(bundle);
            AppPermissionsFragment appPermissionsFragment = this.mOuterFragment;
            setHeader(appPermissionsFragment.mIcon, appPermissionsFragment.mLabel, null, null, false);
            setHasOptionsMenu(true);
            setPreferenceScreen(this.mOuterFragment.mExtraScreen);
        }

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            AppPermissionsFragment.bindUi(this, AppPermissionsFragment.getPackageInfo(getActivity(), getArguments().getString("android.intent.extra.PACKAGE_NAME"), (UserHandle) getArguments().getParcelable("android.intent.extra.USER")));
        }

        @Override // androidx.fragment.app.Fragment
        public boolean onOptionsItemSelected(MenuItem menuItem) {
            if (menuItem.getItemId() == 16908332) {
                getFragmentManager().popBackStack();
                return true;
            }
            return super.onOptionsItemSelected(menuItem);
        }
    }
}
