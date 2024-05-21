package com.android.packageinstaller.role.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.ui.DefaultAppListChildFragment.Parent;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class DefaultAppListChildFragment<PF extends PreferenceFragmentCompat & Parent> extends Fragment implements Preference.OnPreferenceClickListener {
    private DefaultAppListViewModel mViewModel;
    private static final String PREFERENCE_KEY_MORE_DEFAULT_APPS = DefaultAppListChildFragment.class.getName() + ".preference.MORE_DEFAULT_APPS";
    private static final String PREFERENCE_KEY_MANAGE_DOMAIN_URLS = DefaultAppListChildFragment.class.getName() + ".preference.MANAGE_DOMAIN_URLS";
    private static final String PREFERENCE_KEY_WORK_CATEGORY = DefaultAppListChildFragment.class.getName() + ".preference.WORK_CATEGORY";

    /* loaded from: classes.dex */
    public interface Parent {
        TwoTargetPreference createPreference(Context context);

        void onPreferenceScreenChanged();
    }

    public static DefaultAppListChildFragment newInstance() {
        return new DefaultAppListChildFragment();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mViewModel = (DefaultAppListViewModel) ViewModelProviders.of(this).get(DefaultAppListViewModel.class);
        this.mViewModel.getLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$DefaultAppListChildFragment$onzyFKrGJ5nDdn7eBnz6ko_pLrU
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                DefaultAppListChildFragment.this.lambda$onActivityCreated$0$DefaultAppListChildFragment((List) obj);
            }
        });
        if (this.mViewModel.hasWorkProfile()) {
            this.mViewModel.getWorkLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$DefaultAppListChildFragment$lyvytn5OwkD6fkER0qY69dTe3TM
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    DefaultAppListChildFragment.this.lambda$onActivityCreated$1$DefaultAppListChildFragment((List) obj);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onActivityCreated$0$DefaultAppListChildFragment(List list) {
        onRoleListChanged();
    }

    public /* synthetic */ void lambda$onActivityCreated$1$DefaultAppListChildFragment(List list) {
        onRoleListChanged();
    }

    private void onRoleListChanged() {
        List<RoleItem> list;
        PreferenceCategory preferenceCategory;
        PreferenceScreen preferenceScreen;
        PreferenceCategory preferenceCategory2;
        List<RoleItem> value = this.mViewModel.getLiveData().getValue();
        if (value == null) {
            return;
        }
        boolean hasWorkProfile = this.mViewModel.hasWorkProfile();
        if (hasWorkProfile) {
            List<RoleItem> value2 = this.mViewModel.getWorkLiveData().getValue();
            if (value2 == null) {
                return;
            }
            list = value2;
        } else {
            list = null;
        }
        PF requirePreferenceFragment = requirePreferenceFragment();
        PreferenceManager preferenceManager = requirePreferenceFragment.getPreferenceManager();
        Context context = preferenceManager.getContext();
        PreferenceScreen preferenceScreen2 = requirePreferenceFragment.getPreferenceScreen();
        ArrayMap<String, Preference> arrayMap = new ArrayMap<>();
        ArrayMap<String, Preference> arrayMap2 = new ArrayMap<>();
        if (preferenceScreen2 == null) {
            PreferenceScreen createPreferenceScreen = preferenceManager.createPreferenceScreen(context);
            requirePreferenceFragment.setPreferenceScreen(createPreferenceScreen);
            preferenceCategory = null;
            preferenceScreen = createPreferenceScreen;
        } else {
            PreferenceCategory preferenceCategory3 = (PreferenceCategory) preferenceScreen2.findPreference(PREFERENCE_KEY_WORK_CATEGORY);
            if (preferenceCategory3 != null) {
                clearPreferences(preferenceCategory3, arrayMap2);
                preferenceScreen2.removePreference(preferenceCategory3);
                preferenceCategory3.setOrder(Preference.DEFAULT_ORDER);
            }
            clearPreferences(preferenceScreen2, arrayMap);
            preferenceCategory = preferenceCategory3;
            preferenceScreen = preferenceScreen2;
        }
        addPreferences(preferenceScreen, value, arrayMap, this, this.mViewModel.getUser(), context);
        addMoreDefaultAppsPreference(preferenceScreen, arrayMap, context);
        addManageDomainUrlsPreference(preferenceScreen, arrayMap, context);
        if (hasWorkProfile && !list.isEmpty()) {
            if (preferenceCategory == null) {
                PreferenceCategory preferenceCategory4 = new PreferenceCategory(context);
                preferenceCategory4.setKey(PREFERENCE_KEY_WORK_CATEGORY);
                preferenceCategory4.setTitle(R.string.default_apps_for_work);
                preferenceCategory2 = preferenceCategory4;
            } else {
                preferenceCategory2 = preferenceCategory;
            }
            preferenceScreen.addPreference(preferenceCategory2);
            addPreferences(preferenceCategory2, list, arrayMap2, this, this.mViewModel.getWorkProfile(), context);
        }
        requirePreferenceFragment.onPreferenceScreenChanged();
    }

    private static void clearPreferences(PreferenceGroup preferenceGroup, ArrayMap<String, Preference> arrayMap) {
        for (int preferenceCount = preferenceGroup.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
            Preference preference = preferenceGroup.getPreference(preferenceCount);
            preferenceGroup.removePreference(preference);
            preference.setOrder(Preference.DEFAULT_ORDER);
            arrayMap.put(preference.getKey(), preference);
        }
    }

    private void addPreferences(PreferenceGroup preferenceGroup, List<RoleItem> list, ArrayMap<String, Preference> arrayMap, Preference.OnPreferenceClickListener onPreferenceClickListener, UserHandle userHandle, Context context) {
        PF requirePreferenceFragment = requirePreferenceFragment();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            RoleItem roleItem = list.get(i);
            Role role = roleItem.getRole();
            TwoTargetPreference twoTargetPreference = (TwoTargetPreference) arrayMap.get(role.getName());
            if (twoTargetPreference == null) {
                twoTargetPreference = requirePreferenceFragment.createPreference(context);
                twoTargetPreference.setKey(role.getName());
                twoTargetPreference.setIconSpaceReserved(true);
                twoTargetPreference.setTitle(role.getShortLabelResource());
                twoTargetPreference.setPersistent(false);
                twoTargetPreference.setOnPreferenceClickListener(onPreferenceClickListener);
                twoTargetPreference.getExtras().putParcelable("android.intent.extra.USER", userHandle);
            }
            List<ApplicationInfo> holderApplicationInfos = roleItem.getHolderApplicationInfos();
            if (holderApplicationInfos.isEmpty()) {
                twoTargetPreference.setIcon((Drawable) null);
                twoTargetPreference.setSummary(R.string.default_app_none);
            } else {
                ApplicationInfo applicationInfo = holderApplicationInfos.get(0);
                twoTargetPreference.setIcon(Utils.getBadgedIcon(context, applicationInfo));
                twoTargetPreference.setSummary(Utils.getAppLabel(applicationInfo, context));
            }
            role.preparePreferenceAsUser(twoTargetPreference, userHandle, context);
            preferenceGroup.addPreference(twoTargetPreference);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Context requireContext = requireContext();
        UserHandle userHandle = (UserHandle) preference.getExtras().getParcelable("android.intent.extra.USER");
        Intent manageIntentAsUser = Roles.get(requireContext).get(key).getManageIntentAsUser(userHandle, requireContext);
        if (manageIntentAsUser == null) {
            manageIntentAsUser = DefaultAppActivity.createIntent(key, userHandle, requireContext);
        }
        startActivity(manageIntentAsUser);
        return true;
    }

    private static void addMoreDefaultAppsPreference(PreferenceGroup preferenceGroup, ArrayMap<String, Preference> arrayMap, final Context context) {
        final Intent intent = new Intent("android.settings.MANAGE_MORE_DEFAULT_APPS_SETTINGS");
        if (isIntentResolvedToSettings(intent, context)) {
            Preference preference = arrayMap.get(PREFERENCE_KEY_MORE_DEFAULT_APPS);
            if (preference == null) {
                preference = new Preference(context);
                preference.setKey(PREFERENCE_KEY_MORE_DEFAULT_APPS);
                preference.setIconSpaceReserved(true);
                preference.setTitle(context.getString(R.string.default_apps_more));
                preference.setPersistent(false);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$DefaultAppListChildFragment$EGrYjB6aghdU7t9-uuRIIwQ-ldo
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference2) {
                        return DefaultAppListChildFragment.lambda$addMoreDefaultAppsPreference$2(context, intent, preference2);
                    }
                });
            }
            preferenceGroup.addPreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$addMoreDefaultAppsPreference$2(Context context, Intent intent, Preference preference) {
        context.startActivity(intent);
        return true;
    }

    private static void addManageDomainUrlsPreference(PreferenceGroup preferenceGroup, ArrayMap<String, Preference> arrayMap, final Context context) {
        final Intent intent = new Intent("android.settings.MANAGE_DOMAIN_URLS");
        if (isIntentResolvedToSettings(intent, context)) {
            Preference preference = arrayMap.get(PREFERENCE_KEY_MANAGE_DOMAIN_URLS);
            if (preference == null) {
                preference = new Preference(context);
                preference.setKey(PREFERENCE_KEY_MANAGE_DOMAIN_URLS);
                preference.setIconSpaceReserved(true);
                preference.setTitle(context.getString(R.string.default_apps_manage_domain_urls));
                preference.setPersistent(false);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$DefaultAppListChildFragment$4Afdfl9IpQveko6aOqlmhgBA3Kg
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference2) {
                        return DefaultAppListChildFragment.lambda$addManageDomainUrlsPreference$3(context, intent, preference2);
                    }
                });
            }
            preferenceGroup.addPreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$addManageDomainUrlsPreference$3(Context context, Intent intent, Preference preference) {
        context.startActivity(intent);
        return true;
    }

    private static boolean isIntentResolvedToSettings(Intent intent, Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName resolveActivity = intent.resolveActivity(packageManager);
        if (resolveActivity == null) {
            return false;
        }
        return Objects.equals(resolveActivity.getPackageName(), new Intent("android.settings.SETTINGS").resolveActivity(packageManager).getPackageName());
    }

    private PF requirePreferenceFragment() {
        return (PF) ((PreferenceFragmentCompat) requireParentFragment());
    }
}
