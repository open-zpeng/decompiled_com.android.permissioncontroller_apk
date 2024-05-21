package com.android.packageinstaller.permission.ui.auto;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.car.ui.AlertDialogBuilder;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.ui.auto.AutoAllAppPermissionsFragment;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class AutoAllAppPermissionsFragment extends AutoSettingsFrameFragment {
    private List<AppPermissionGroup> mGroups;

    public static AutoAllAppPermissionsFragment newInstance(String str, UserHandle userHandle) {
        return newInstance(str, null, userHandle);
    }

    public static AutoAllAppPermissionsFragment newInstance(String str, String str2, UserHandle userHandle) {
        AutoAllAppPermissionsFragment autoAllAppPermissionsFragment = new AutoAllAppPermissionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        bundle.putString("android.intent.extra.PERMISSION_GROUP_NAME", str2);
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        autoAllAppPermissionsFragment.setArguments(bundle);
        return autoAllAppPermissionsFragment;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        if (getArguments().getString("android.intent.extra.PERMISSION_GROUP_NAME") == null) {
            setHeaderLabel(getContext().getString(R.string.all_permissions));
        } else {
            setHeaderLabel(getContext().getString(R.string.app_permissions));
        }
        updateUi();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        getPreferenceScreen().removeAll();
    }

    private void updateUi() {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setKey("other_perms");
        preferenceCategory.setTitle(R.string.other_permissions);
        getPreferenceScreen().addPreference(preferenceCategory);
        ArrayList<Preference> arrayList = new ArrayList<>();
        arrayList.add(preferenceCategory);
        String string = getArguments().getString("android.intent.extra.PACKAGE_NAME");
        String string2 = getArguments().getString("android.intent.extra.PERMISSION_GROUP_NAME");
        preferenceCategory.removeAll();
        PackageManager packageManager = getContext().getPackageManager();
        PackageInfo packageInfo = AutoPermissionsUtils.getPackageInfo(requireActivity(), string, (UserHandle) getArguments().getParcelable("android.intent.extra.USER"));
        if (packageInfo == null) {
            return;
        }
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        Preference createHeaderPreference = AutoPermissionsUtils.createHeaderPreference(getContext(), applicationInfo);
        int i = 0;
        createHeaderPreference.setOrder(0);
        getPreferenceScreen().addPreference(createHeaderPreference);
        if (packageInfo.requestedPermissions != null) {
            int i2 = 0;
            while (true) {
                String[] strArr = packageInfo.requestedPermissions;
                if (i2 >= strArr.length) {
                    break;
                }
                try {
                    PermissionInfo permissionInfo = packageManager.getPermissionInfo(strArr[i2], 0);
                    int i3 = permissionInfo.flags;
                    if ((1073741824 & i3) != 0 && (i3 & 2) == 0 && ((!applicationInfo.isInstantApp() || (permissionInfo.protectionLevel & 4096) != 0) && (applicationInfo.targetSdkVersion >= 23 || (permissionInfo.protectionLevel & 8192) == 0))) {
                        int i4 = permissionInfo.protectionLevel;
                        if ((i4 & 15) == 1) {
                            PackageItemInfo group = getGroup(Utils.getGroupOfPermission(permissionInfo), packageManager);
                            if (group == null) {
                                group = permissionInfo;
                            }
                            if (string2 == null || group.name.equals(string2)) {
                                findOrCreate(group, packageManager, arrayList).addPreference(getPreference(packageInfo, permissionInfo, group, packageManager));
                            }
                        } else if (string2 == null && (i4 & 15) == 0) {
                            preferenceCategory.addPreference(getPreference(packageInfo, permissionInfo, getGroup(permissionInfo.group, packageManager), packageManager));
                        }
                        if (string2 != null) {
                            getPreferenceScreen().removePreference(preferenceCategory);
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("AllAppPermsFrag", "Can't get permission info for " + packageInfo.requestedPermissions[i2], e);
                }
                i2++;
            }
        }
        Collections.sort(arrayList, new Comparator() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAllAppPermissionsFragment$yuN2bpmJw2p7TMmWcYPfJ0rZOf8
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AutoAllAppPermissionsFragment.lambda$updateUi$0((Preference) obj, (Preference) obj2);
            }
        });
        while (i < arrayList.size()) {
            i++;
            arrayList.get(i).setOrder(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$updateUi$0(Preference preference, Preference preference2) {
        String key = preference.getKey();
        String key2 = preference2.getKey();
        if (key.equals("other_perms")) {
            return 1;
        }
        if (key2.equals("other_perms")) {
            return -1;
        }
        if (Utils.isModernPermissionGroup(key) != Utils.isModernPermissionGroup(key2)) {
            return Utils.isModernPermissionGroup(key) ? -1 : 1;
        }
        return preference.getTitle().toString().compareTo(preference2.getTitle().toString());
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
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAllAppPermissionsFragment$Z30W6gQYB3_RfHYKPC8jyswJ_5Y
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                return AutoAllAppPermissionsFragment.this.lambda$getPreference$1$AutoAllAppPermissionsFragment(loadDescription, isPermissionIndividuallyControlled, preference2);
            }
        });
        return preference;
    }

    public /* synthetic */ boolean lambda$getPreference$1$AutoAllAppPermissionsFragment(CharSequence charSequence, boolean z, Preference preference) {
        new AlertDialogBuilder(getContext()).setMessage(charSequence).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).show();
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
    public static final class MyMultiTargetSwitchPreference extends SwitchPreference {
        private View.OnClickListener mSwitchOnClickLister;

        MyMultiTargetSwitchPreference(Context context, final String str, final AppPermissionGroup appPermissionGroup) {
            super(context);
            setChecked(appPermissionGroup.areRuntimePermissionsGranted(new String[]{str}));
            setSwitchOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAllAppPermissionsFragment$MyMultiTargetSwitchPreference$qcBjK1xez1qKnh6RRWPNMR3HAHE
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AutoAllAppPermissionsFragment.MyMultiTargetSwitchPreference.lambda$new$0(AppPermissionGroup.this, str, view);
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

        @Override // androidx.preference.TwoStatePreference
        public void setChecked(boolean z) {
            if (this.mSwitchOnClickLister == null) {
                super.setChecked(z);
            }
        }

        void setSwitchOnClickListener(View.OnClickListener onClickListener) {
            this.mSwitchOnClickLister = onClickListener;
        }

        @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            Switch r2 = (Switch) preferenceViewHolder.itemView.findViewById(16908352);
            if (r2 != null) {
                r2.setOnClickListener(this.mSwitchOnClickLister);
            }
        }
    }
}
