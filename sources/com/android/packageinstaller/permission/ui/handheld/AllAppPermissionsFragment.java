package com.android.packageinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.ui.handheld.AllAppPermissionsFragment;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class AllAppPermissionsFragment extends SettingsWithLargeHeader {
    private List<AppPermissionGroup> mGroups;

    public static AllAppPermissionsFragment newInstance(String str, UserHandle userHandle) {
        return newInstance(str, null, userHandle);
    }

    public static AllAppPermissionsFragment newInstance(String str, String str2, UserHandle userHandle) {
        AllAppPermissionsFragment allAppPermissionsFragment = new AllAppPermissionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        bundle.putString("android.intent.extra.PERMISSION_GROUP_NAME", str2);
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        allAppPermissionsFragment.setArguments(bundle);
        return allAppPermissionsFragment;
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            if (getArguments().getString("android.intent.extra.PERMISSION_GROUP_NAME") == null) {
                actionBar.setTitle(R.string.all_permissions);
            } else {
                actionBar.setTitle(R.string.app_permissions);
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);
        updateUi();
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX WARN: Removed duplicated region for block: B:70:0x0189 A[LOOP:1: B:68:0x0183->B:70:0x0189, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateUi() {
        /*
            Method dump skipped, instructions count: 406
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.packageinstaller.permission.ui.handheld.AllAppPermissionsFragment.updateUi():void");
    }

    private PermissionGroupInfo getGroup(String str, PackageManager packageManager) {
        try {
            return packageManager.getPermissionGroupInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    private PreferenceGroup findOrCreate(PackageItemInfo packageItemInfo, PackageManager packageManager, ArrayList<Preference> arrayList) {
        PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference(packageItemInfo.name);
        if (preferenceGroup == null) {
            PreferenceCategory preferenceCategory = new PreferenceCategory(getPreferenceManager().getContext());
            preferenceCategory.setKey(packageItemInfo.name);
            preferenceCategory.setTitle(packageItemInfo.loadLabel(packageManager));
            arrayList.add(preferenceCategory);
            getPreferenceScreen().addPreference(preferenceCategory);
            return preferenceCategory;
        }
        return preferenceGroup;
    }

    private Preference getPreference(PackageInfo packageInfo, PermissionInfo permissionInfo, PackageItemInfo packageItemInfo, PackageManager packageManager) {
        Preference preference;
        Drawable drawable;
        Context context = getPreferenceManager().getContext();
        final boolean isPermissionIndividuallyControlled = Utils.isPermissionIndividuallyControlled(getContext(), permissionInfo.name);
        if (isPermissionIndividuallyControlled) {
            preference = new MyMultiTargetSwitchPreference(context, permissionInfo.name, getPermissionForegroundGroup(packageInfo, permissionInfo.name));
        } else {
            preference = new Preference(context);
        }
        if (permissionInfo.icon != 0) {
            drawable = permissionInfo.loadUnbadgedIcon(packageManager);
        } else if (packageItemInfo != null && packageItemInfo.icon != 0) {
            drawable = packageItemInfo.loadUnbadgedIcon(packageManager);
        } else {
            drawable = context.getDrawable(R.drawable.ic_perm_device_info);
        }
        preference.setIcon(Utils.applyTint(context, drawable, 16843817));
        preference.setTitle(permissionInfo.loadSafeLabel(packageManager, 20000.0f, 1));
        preference.setSingleLineTitle(false);
        final CharSequence loadDescription = permissionInfo.loadDescription(packageManager);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AllAppPermissionsFragment$3IlLxMsa33pe3DLnjV25APxXWoI
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                return AllAppPermissionsFragment.this.lambda$getPreference$0$AllAppPermissionsFragment(loadDescription, isPermissionIndividuallyControlled, preference2);
            }
        });
        return preference;
    }

    public /* synthetic */ boolean lambda$getPreference$0$AllAppPermissionsFragment(CharSequence charSequence, boolean z, Preference preference) {
        new AlertDialog.Builder(getContext()).setMessage(charSequence).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).show();
        return z;
    }

    private AppPermissionGroup getPermissionForegroundGroup(PackageInfo packageInfo, String str) {
        AppPermissionGroup appPermissionGroup;
        List<AppPermissionGroup> list = this.mGroups;
        if (list != null) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                appPermissionGroup = this.mGroups.get(i);
                if (appPermissionGroup.hasPermission(str)) {
                    break;
                }
                if (appPermissionGroup.getBackgroundPermissions() != null && appPermissionGroup.getBackgroundPermissions().hasPermission(str)) {
                    appPermissionGroup = appPermissionGroup.getBackgroundPermissions();
                    break;
                }
            }
        }
        appPermissionGroup = null;
        if (appPermissionGroup == null) {
            appPermissionGroup = AppPermissionGroup.create(getContext(), packageInfo, str, false);
            if (this.mGroups == null) {
                this.mGroups = new ArrayList();
            }
            this.mGroups.add(appPermissionGroup);
        }
        return appPermissionGroup;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class MyMultiTargetSwitchPreference extends MultiTargetSwitchPreference {
        MyMultiTargetSwitchPreference(Context context, final String str, final AppPermissionGroup appPermissionGroup) {
            super(context);
            setChecked(appPermissionGroup.areRuntimePermissionsGranted(new String[]{str}));
            setSwitchOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AllAppPermissionsFragment$MyMultiTargetSwitchPreference$yhDiL0W61VJT389ls1NPxMcBfzY
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AllAppPermissionsFragment.MyMultiTargetSwitchPreference.lambda$new$0(AppPermissionGroup.this, str, view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$new$0(AppPermissionGroup appPermissionGroup, String str, View view) {
            if (((Switch) view).isChecked()) {
                appPermissionGroup.grantRuntimePermissions(false, new String[]{str});
                if (appPermissionGroup.doesSupportRuntimePermissions()) {
                    int size = appPermissionGroup.getPermissions().size();
                    String[] strArr = null;
                    int i = 0;
                    for (int i2 = 0; i2 < size; i2++) {
                        Permission permission = appPermissionGroup.getPermissions().get(i2);
                        if (permission.isGrantedIncludingAppOp()) {
                            i++;
                        } else if (!permission.isUserFixed()) {
                            strArr = ArrayUtils.appendString(strArr, permission.getName());
                        }
                    }
                    if (strArr != null) {
                        appPermissionGroup.revokeRuntimePermissions(true, strArr);
                        return;
                    } else if (appPermissionGroup.getPermissions().size() == i) {
                        appPermissionGroup.grantRuntimePermissions(false);
                        return;
                    } else {
                        return;
                    }
                }
                return;
            }
            appPermissionGroup.revokeRuntimePermissions(true, new String[]{str});
            if (!appPermissionGroup.doesSupportRuntimePermissions() || appPermissionGroup.areRuntimePermissionsGranted()) {
                return;
            }
            appPermissionGroup.revokeRuntimePermissions(false);
        }
    }
}
