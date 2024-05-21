package com.android.packageinstaller.permission.ui.auto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.PermissionApps;
import com.android.packageinstaller.permission.ui.handheld.PermissionAppsFragment;
import com.android.packageinstaller.permission.ui.handheld.PermissionControlPreference;
import com.android.packageinstaller.permission.utils.Utils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
/* loaded from: classes.dex */
public class AutoPermissionAppsFragment extends AutoSettingsFrameFragment implements PermissionApps.Callback {
    private static final String SHOW_SYSTEM_KEY = AutoPermissionAppsFragment.class.getName() + "_showSystem";
    private Collator mCollator;
    private boolean mHasSystemApps;
    private PermissionApps mPermissionApps;
    private boolean mShowSystem;

    public static AutoPermissionAppsFragment newInstance(String str) {
        AutoPermissionAppsFragment autoPermissionAppsFragment = new AutoPermissionAppsFragment();
        setPermissionName(autoPermissionAppsFragment, str);
        return autoPermissionAppsFragment;
    }

    private static <T extends Fragment> T setPermissionName(T t, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PERMISSION_NAME", str);
        t.setArguments(bundle);
        return t;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mShowSystem = bundle.getBoolean(SHOW_SYSTEM_KEY);
        }
        setLoading(true);
        String string = getArguments().getString("android.intent.extra.PERMISSION_NAME");
        this.mPermissionApps = new PermissionApps(getActivity(), string, this);
        this.mPermissionApps.refresh(true);
        this.mCollator = Collator.getInstance(getContext().getResources().getConfiguration().getLocales().get(0));
        setShowSystemAppsToggle();
        bindUi(this.mPermissionApps, string);
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(SHOW_SYSTEM_KEY, this.mShowSystem);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mPermissionApps.refresh(true);
    }

    private void setShowSystemAppsToggle() {
        String string;
        if (!this.mHasSystemApps) {
            setAction(null, null);
            return;
        }
        if (this.mShowSystem) {
            string = getString(R.string.menu_hide_system);
        } else {
            string = getString(R.string.menu_show_system);
        }
        setAction(string, new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoPermissionAppsFragment$cc8FMyC099QQAPy2lDrg_OyB3DM
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AutoPermissionAppsFragment.this.lambda$setShowSystemAppsToggle$0$AutoPermissionAppsFragment(view);
            }
        });
    }

    public /* synthetic */ void lambda$setShowSystemAppsToggle$0$AutoPermissionAppsFragment(View view) {
        this.mShowSystem = !this.mShowSystem;
        if (this.mPermissionApps.getApps() != null) {
            onPermissionsLoaded(this.mPermissionApps);
        }
        setShowSystemAppsToggle();
    }

    private void bindUi(PermissionApps permissionApps, String str) {
        CharSequence fullLabel = permissionApps.getFullLabel();
        setHeaderLabel(fullLabel);
        Drawable icon = permissionApps.getIcon();
        Preference preference = new Preference(getContext());
        preference.setTitle(fullLabel);
        preference.setIcon(icon);
        preference.setSummary(Utils.getPermissionGroupDescriptionString(getContext(), str, permissionApps.getDescription()));
        getPreferenceScreen().addPreference(preference);
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setKey("allowed_permissions_group");
        preferenceCategory.setTitle(R.string.allowed_header);
        preferenceCategory.setVisible(false);
        getPreferenceScreen().addPreference(preferenceCategory);
        PreferenceCategory preferenceCategory2 = new PreferenceCategory(getContext());
        preferenceCategory2.setKey("allowed_foreground_permissions_group");
        preferenceCategory2.setTitle(R.string.allowed_foreground_header);
        preferenceCategory2.setVisible(false);
        getPreferenceScreen().addPreference(preferenceCategory2);
        PreferenceCategory preferenceCategory3 = new PreferenceCategory(getContext());
        preferenceCategory3.setKey("denied_permissions_group");
        preferenceCategory3.setTitle(R.string.denied_header);
        preferenceCategory3.setVisible(false);
        getPreferenceScreen().addPreference(preferenceCategory3);
    }

    @Override // com.android.packageinstaller.permission.model.PermissionApps.Callback
    public void onPermissionsLoaded(PermissionApps permissionApps) {
        PreferenceCategory preferenceCategory;
        Context context = getPreferenceManager().getContext();
        if (context == null || getActivity() == null) {
            return;
        }
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("allowed_permissions_group");
        PreferenceCategory preferenceCategory3 = (PreferenceCategory) findPreference("allowed_foreground_permissions_group");
        PreferenceCategory preferenceCategory4 = (PreferenceCategory) findPreference("denied_permissions_group");
        boolean z = true;
        preferenceCategory2.setOrderingAsAdded(true);
        preferenceCategory3.setOrderingAsAdded(true);
        preferenceCategory4.setOrderingAsAdded(true);
        ArrayMap arrayMap = new ArrayMap();
        int preferenceCount = preferenceCategory2.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceCategory2.getPreference(i);
            arrayMap.put(preference.getKey(), preference);
        }
        preferenceCategory2.removeAll();
        int preferenceCount2 = preferenceCategory3.getPreferenceCount();
        for (int i2 = 0; i2 < preferenceCount2; i2++) {
            Preference preference2 = preferenceCategory3.getPreference(i2);
            arrayMap.put(preference2.getKey(), preference2);
        }
        preferenceCategory3.removeAll();
        int preferenceCount3 = preferenceCategory4.getPreferenceCount();
        for (int i3 = 0; i3 < preferenceCount3; i3++) {
            Preference preference3 = preferenceCategory4.getPreference(i3);
            arrayMap.put(preference3.getKey(), preference3);
        }
        preferenceCategory4.removeAll();
        this.mHasSystemApps = false;
        ArrayList arrayList = new ArrayList(permissionApps.getApps());
        arrayList.sort(new Comparator() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoPermissionAppsFragment$UsiEzdIgF7busMWm0A6bSdU1MqM
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AutoPermissionAppsFragment.this.lambda$onPermissionsLoaded$1$AutoPermissionAppsFragment((PermissionApps.PermissionApp) obj, (PermissionApps.PermissionApp) obj2);
            }
        });
        int i4 = 0;
        boolean z2 = false;
        while (i4 < arrayList.size()) {
            PermissionApps.PermissionApp permissionApp = (PermissionApps.PermissionApp) arrayList.get(i4);
            AppPermissionGroup permissionGroup = permissionApp.getPermissionGroup();
            z2 = (z2 || permissionGroup.hasPermissionWithBackgroundMode()) ? z : false;
            if (Utils.shouldShowPermission(getContext(), permissionGroup) && permissionApp.getAppInfo().enabled) {
                String key = permissionApp.getKey();
                Preference preference4 = (Preference) arrayMap.get(key);
                if (preference4 != null) {
                    preference4.setOrder(Preference.DEFAULT_ORDER);
                }
                boolean isGroupOrBgGroupUserSensitive = Utils.isGroupOrBgGroupUserSensitive(permissionGroup) ^ z;
                if (isGroupOrBgGroupUserSensitive) {
                    this.mHasSystemApps = z;
                }
                if (!isGroupOrBgGroupUserSensitive || this.mShowSystem) {
                    if (permissionGroup.areRuntimePermissionsGranted()) {
                        preferenceCategory = (!permissionGroup.hasPermissionWithBackgroundMode() || (permissionGroup.getBackgroundPermissions() != null && permissionGroup.getBackgroundPermissions().areRuntimePermissionsGranted())) ? preferenceCategory2 : preferenceCategory3;
                    } else {
                        preferenceCategory = preferenceCategory4;
                    }
                    if (preference4 != null) {
                        preferenceCategory.addPreference(preference4);
                    } else {
                        PermissionControlPreference permissionControlPreference = new PermissionControlPreference(context, permissionGroup, PermissionAppsFragment.class.getName());
                        permissionControlPreference.setKey(key);
                        permissionControlPreference.setIcon(permissionApp.getIcon());
                        permissionControlPreference.setTitle(Utils.getFullAppLabel(permissionApp.getAppInfo(), context));
                        permissionControlPreference.setEllipsizeEnd();
                        permissionControlPreference.useSmallerIcon();
                        preferenceCategory.addPreference(permissionControlPreference);
                    }
                }
            }
            i4++;
            z = true;
        }
        if (z2) {
            preferenceCategory2.setTitle(R.string.allowed_always_header);
        }
        if (preferenceCategory2.getPreferenceCount() == 0) {
            Preference preference5 = new Preference(context);
            preference5.setTitle(R.string.no_apps_allowed);
            preference5.setSelectable(false);
            preferenceCategory2.addPreference(preference5);
        }
        preferenceCategory2.setVisible(true);
        preferenceCategory3.setVisible(preferenceCategory3.getPreferenceCount() > 0);
        if (preferenceCategory4.getPreferenceCount() == 0) {
            Preference preference6 = new Preference(context);
            preference6.setTitle(R.string.no_apps_denied);
            preference6.setSelectable(false);
            preferenceCategory4.addPreference(preference6);
        }
        preferenceCategory4.setVisible(true);
        setShowSystemAppsToggle();
        setLoading(false);
    }

    public /* synthetic */ int lambda$onPermissionsLoaded$1$AutoPermissionAppsFragment(PermissionApps.PermissionApp permissionApp, PermissionApps.PermissionApp permissionApp2) {
        int compare = this.mCollator.compare(permissionApp.getLabel(), permissionApp2.getLabel());
        return compare == 0 ? permissionApp.getUid() - permissionApp2.getUid() : compare;
    }
}
