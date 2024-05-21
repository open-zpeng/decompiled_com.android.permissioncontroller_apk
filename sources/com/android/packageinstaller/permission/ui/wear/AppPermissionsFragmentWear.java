package com.android.packageinstaller.permission.ui.wear;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.wear.ble.view.WearableDialogHelper;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.LocationUtils;
import com.android.packageinstaller.permission.utils.SafetyNetLogger;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.settingslib.RestrictedLockUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public final class AppPermissionsFragmentWear extends PreferenceFragmentCompat {
    private AppPermissions mAppPermissions;
    private boolean mHasConfirmedRevoke;
    private PackageManager mPackageManager;
    private ArraySet<AppPermissionGroup> mToggledGroups;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    public static AppPermissionsFragmentWear newInstance(String str) {
        AppPermissionsFragmentWear appPermissionsFragmentWear = new AppPermissionsFragmentWear();
        setPackageName(appPermissionsFragmentWear, str);
        return appPermissionsFragmentWear;
    }

    private static <T extends Fragment> T setPackageName(T t, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        t.setArguments(bundle);
        return t;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PermissionSwitchPreference extends SwitchPreference {
        private final Activity mActivity;

        public PermissionSwitchPreference(Activity activity) {
            super(activity);
            this.mActivity = activity;
        }

        @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
        protected void performClick(View view) {
            super.performClick(view);
            if (isEnabled()) {
                return;
            }
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mActivity, RestrictedLockUtils.getProfileOrDeviceOwner(this.mActivity, UserHandle.of(UserHandle.myUserId())));
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        PackageInfo packageInfo;
        super.onCreate(bundle);
        String string = getArguments().getString("android.intent.extra.PACKAGE_NAME");
        FragmentActivity activity = getActivity();
        this.mPackageManager = activity.getPackageManager();
        try {
            packageInfo = this.mPackageManager.getPackageInfo(string, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("AppPermFragWear", "No package:" + activity.getCallingPackage(), e);
            packageInfo = null;
        }
        if (packageInfo == null) {
            Toast.makeText(activity, (int) R.string.app_not_found_dlg_title, 1).show();
            activity.finish();
            return;
        }
        this.mAppPermissions = new AppPermissions(activity, packageInfo, true, new Runnable() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$AppPermissionsFragmentWear$JglwLeF5RwQG5FeExea5BcZ1HG0
            @Override // java.lang.Runnable
            public final void run() {
                AppPermissionsFragmentWear.this.lambda$onCreate$0$AppPermissionsFragmentWear();
            }
        });
        addPreferencesFromResource(R.xml.watch_permissions);
        initializePermissionGroupList();
    }

    public /* synthetic */ void lambda$onCreate$0$AppPermissionsFragmentWear() {
        getActivity().finish();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppPermissions.refresh();
        for (AppPermissionGroup appPermissionGroup : this.mAppPermissions.getPermissionGroups()) {
            if (Utils.areGroupPermissionsIndividuallyControlled(getContext(), appPermissionGroup.getName())) {
                for (PermissionInfo permissionInfo : getPermissionInfosFromGroup(appPermissionGroup)) {
                    setPreferenceCheckedIfPresent(permissionInfo.name, appPermissionGroup.areRuntimePermissionsGranted(new String[]{permissionInfo.name}));
                }
            } else {
                setPreferenceCheckedIfPresent(appPermissionGroup.getName(), appPermissionGroup.areRuntimePermissionsGranted());
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        logAndClearToggledGroups();
    }

    private void initializePermissionGroupList() {
        List<AppPermissionGroup> permissionGroups = this.mAppPermissions.getPermissionGroups();
        ArrayList<SwitchPreference> arrayList = new ArrayList();
        if (!permissionGroups.isEmpty()) {
            getPreferenceScreen().removePreference(findPreference("no_permissions"));
        }
        for (AppPermissionGroup appPermissionGroup : permissionGroups) {
            if (Utils.shouldShowPermission(getContext(), appPermissionGroup)) {
                boolean equals = appPermissionGroup.getDeclaringPackage().equals("android");
                if (Utils.areGroupPermissionsIndividuallyControlled(getContext(), appPermissionGroup.getName())) {
                    for (PermissionInfo permissionInfo : getPermissionInfosFromGroup(appPermissionGroup)) {
                        showOrAddToNonSystemPreferences(createSwitchPreferenceForPermission(appPermissionGroup, permissionInfo), arrayList, equals);
                    }
                } else {
                    showOrAddToNonSystemPreferences(createSwitchPreferenceForGroup(appPermissionGroup), arrayList, equals);
                }
            }
        }
        for (SwitchPreference switchPreference : arrayList) {
            getPreferenceScreen().addPreference(switchPreference);
        }
    }

    private void showOrAddToNonSystemPreferences(SwitchPreference switchPreference, List<SwitchPreference> list, boolean z) {
        if (z) {
            getPreferenceScreen().addPreference(switchPreference);
        } else {
            list.add(switchPreference);
        }
    }

    private SwitchPreference createSwitchPreferenceForPermission(final AppPermissionGroup appPermissionGroup, final PermissionInfo permissionInfo) {
        final PermissionSwitchPreference permissionSwitchPreference = new PermissionSwitchPreference(getActivity());
        permissionSwitchPreference.setKey(permissionInfo.name);
        permissionSwitchPreference.setTitle(permissionInfo.loadLabel(this.mPackageManager));
        permissionSwitchPreference.setChecked(appPermissionGroup.areRuntimePermissionsGranted(new String[]{permissionInfo.name}));
        permissionSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$AppPermissionsFragmentWear$Wgp6qoVmqeDW-APqwhl6yjfy31s
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return AppPermissionsFragmentWear.this.lambda$createSwitchPreferenceForPermission$2$AppPermissionsFragmentWear(appPermissionGroup, permissionInfo, permissionSwitchPreference, preference, obj);
            }
        });
        return permissionSwitchPreference;
    }

    public /* synthetic */ boolean lambda$createSwitchPreferenceForPermission$2$AppPermissionsFragmentWear(final AppPermissionGroup appPermissionGroup, final PermissionInfo permissionInfo, final SwitchPreference switchPreference, Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            appPermissionGroup.grantRuntimePermissions(false, new String[]{permissionInfo.name});
            if (Utils.areGroupPermissionsIndividuallyControlled(getContext(), appPermissionGroup.getName()) && appPermissionGroup.doesSupportRuntimePermissions()) {
                String[] strArr = null;
                int size = appPermissionGroup.getPermissions().size();
                for (int i = 0; i < size; i++) {
                    Permission permission = appPermissionGroup.getPermissions().get(i);
                    if (!permission.isGranted() && !permission.isUserFixed()) {
                        strArr = ArrayUtils.appendString(strArr, permission.getName());
                    }
                }
                if (strArr != null) {
                    appPermissionGroup.revokeRuntimePermissions(true, strArr);
                }
            }
        } else {
            final Permission permissionFromGroup = getPermissionFromGroup(appPermissionGroup, permissionInfo.name);
            if (permissionFromGroup == null) {
                return false;
            }
            boolean isGrantedByDefault = permissionFromGroup.isGrantedByDefault();
            if (isGrantedByDefault || (!appPermissionGroup.doesSupportRuntimePermissions() && !this.mHasConfirmedRevoke)) {
                showRevocationWarningDialog(new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$AppPermissionsFragmentWear$tuBcjjtb5nKICQgzqu1y0bSEM2s
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        AppPermissionsFragmentWear.this.lambda$createSwitchPreferenceForPermission$1$AppPermissionsFragmentWear(appPermissionGroup, permissionInfo, switchPreference, permissionFromGroup, dialogInterface, i2);
                    }
                }, isGrantedByDefault ? R.string.system_warning : R.string.old_sdk_deny_warning);
                return false;
            }
            revokePermissionInGroup(appPermissionGroup, permissionInfo.name);
        }
        return true;
    }

    public /* synthetic */ void lambda$createSwitchPreferenceForPermission$1$AppPermissionsFragmentWear(AppPermissionGroup appPermissionGroup, PermissionInfo permissionInfo, SwitchPreference switchPreference, Permission permission, DialogInterface dialogInterface, int i) {
        revokePermissionInGroup(appPermissionGroup, permissionInfo.name);
        switchPreference.setChecked(false);
        if (permission.isGrantedByDefault()) {
            return;
        }
        this.mHasConfirmedRevoke = true;
    }

    private void showRevocationWarningDialog(DialogInterface.OnClickListener onClickListener, int i) {
        WearableDialogHelper.DialogBuilder dialogBuilder = new WearableDialogHelper.DialogBuilder(getContext());
        dialogBuilder.setNegativeIcon(R.drawable.confirm_button);
        dialogBuilder.setPositiveIcon(R.drawable.cancel_button);
        dialogBuilder.setNegativeButton(R.string.grant_dialog_button_deny_anyway, onClickListener).setPositiveButton(R.string.cancel, (DialogInterface.OnClickListener) null).setMessage(i).show();
    }

    private static Permission getPermissionFromGroup(AppPermissionGroup appPermissionGroup, String str) {
        int size = appPermissionGroup.getPermissions().size();
        for (int i = 0; i < size; i++) {
            Permission permission = appPermissionGroup.getPermissions().get(i);
            if (permission.getName().equals(str)) {
                return permission;
            }
        }
        if ("user".equals(Build.TYPE)) {
            Log.e("AppPermFragWear", String.format("The impossible happens, permission %s is not in group %s.", str, appPermissionGroup.getName()));
            return null;
        }
        throw new IllegalArgumentException(String.format("Permission %s is not in group %s", str, appPermissionGroup.getName()));
    }

    private void revokePermissionInGroup(AppPermissionGroup appPermissionGroup, String str) {
        appPermissionGroup.revokeRuntimePermissions(true, new String[]{str});
        if (Utils.areGroupPermissionsIndividuallyControlled(getContext(), appPermissionGroup.getName()) && appPermissionGroup.doesSupportRuntimePermissions() && !appPermissionGroup.areRuntimePermissionsGranted()) {
            appPermissionGroup.revokeRuntimePermissions(false);
        }
    }

    private SwitchPreference createSwitchPreferenceForGroup(final AppPermissionGroup appPermissionGroup) {
        final PermissionSwitchPreference permissionSwitchPreference = new PermissionSwitchPreference(getActivity());
        permissionSwitchPreference.setKey(appPermissionGroup.getName());
        permissionSwitchPreference.setTitle(appPermissionGroup.getLabel());
        permissionSwitchPreference.setChecked(appPermissionGroup.areRuntimePermissionsGranted());
        if (appPermissionGroup.isSystemFixed() || appPermissionGroup.isPolicyFixed()) {
            permissionSwitchPreference.setEnabled(false);
        } else {
            permissionSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$AppPermissionsFragmentWear$gkywLVaowyfaWomBbSBDlgF_Hhs
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return AppPermissionsFragmentWear.this.lambda$createSwitchPreferenceForGroup$4$AppPermissionsFragmentWear(appPermissionGroup, permissionSwitchPreference, preference, obj);
                }
            });
        }
        return permissionSwitchPreference;
    }

    public /* synthetic */ boolean lambda$createSwitchPreferenceForGroup$4$AppPermissionsFragmentWear(final AppPermissionGroup appPermissionGroup, final SwitchPreference switchPreference, Preference preference, Object obj) {
        if (LocationUtils.isLocationGroupAndProvider(getContext(), appPermissionGroup.getName(), appPermissionGroup.getApp().packageName)) {
            LocationUtils.showLocationDialog(getContext(), this.mAppPermissions.getAppLabel());
            return false;
        }
        if (((Boolean) obj).booleanValue()) {
            setPermission(appPermissionGroup, switchPreference, true);
        } else {
            boolean hasGrantedByDefaultPermission = appPermissionGroup.hasGrantedByDefaultPermission();
            if (hasGrantedByDefaultPermission || (!appPermissionGroup.doesSupportRuntimePermissions() && !this.mHasConfirmedRevoke)) {
                showRevocationWarningDialog(new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$AppPermissionsFragmentWear$epwXnazyb47kdgXJOaLVc0nzJUI
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AppPermissionsFragmentWear.this.lambda$createSwitchPreferenceForGroup$3$AppPermissionsFragmentWear(appPermissionGroup, switchPreference, dialogInterface, i);
                    }
                }, hasGrantedByDefaultPermission ? R.string.system_warning : R.string.old_sdk_deny_warning);
                return false;
            }
            setPermission(appPermissionGroup, switchPreference, false);
        }
        return true;
    }

    public /* synthetic */ void lambda$createSwitchPreferenceForGroup$3$AppPermissionsFragmentWear(AppPermissionGroup appPermissionGroup, SwitchPreference switchPreference, DialogInterface dialogInterface, int i) {
        setPermission(appPermissionGroup, switchPreference, false);
        if (appPermissionGroup.hasGrantedByDefaultPermission()) {
            return;
        }
        this.mHasConfirmedRevoke = true;
    }

    private void setPermission(AppPermissionGroup appPermissionGroup, SwitchPreference switchPreference, boolean z) {
        if (z) {
            appPermissionGroup.grantRuntimePermissions(false);
        } else {
            appPermissionGroup.revokeRuntimePermissions(false);
        }
        addToggledGroup(appPermissionGroup);
        switchPreference.setChecked(z);
    }

    private void addToggledGroup(AppPermissionGroup appPermissionGroup) {
        if (this.mToggledGroups == null) {
            this.mToggledGroups = new ArraySet<>();
        }
        this.mToggledGroups.add(appPermissionGroup);
    }

    private void logAndClearToggledGroups() {
        ArraySet<AppPermissionGroup> arraySet = this.mToggledGroups;
        if (arraySet != null) {
            SafetyNetLogger.logPermissionsToggled(arraySet);
            this.mToggledGroups = null;
        }
    }

    private List<PermissionInfo> getPermissionInfosFromGroup(AppPermissionGroup appPermissionGroup) {
        ArrayList arrayList = new ArrayList(appPermissionGroup.getPermissions().size());
        Iterator<Permission> it = appPermissionGroup.getPermissions().iterator();
        while (it.hasNext()) {
            Permission next = it.next();
            try {
                arrayList.add(this.mPackageManager.getPermissionInfo(next.getName(), 0));
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("AppPermFragWear", "No permission:" + next.getName());
            }
        }
        return arrayList;
    }

    private void setPreferenceCheckedIfPresent(String str, boolean z) {
        Preference findPreference = findPreference(str);
        if (findPreference instanceof SwitchPreference) {
            ((SwitchPreference) findPreference).setChecked(z);
        }
    }
}
