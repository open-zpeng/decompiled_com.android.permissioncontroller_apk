package com.android.packageinstaller.permission.ui.auto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.utils.Utils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
/* loaded from: classes.dex */
public class AutoAppPermissionsFragment extends AutoSettingsFrameFragment {
    private AppPermissions mAppPermissions;
    private Collator mCollator;
    private PreferenceScreen mExtraScreen;

    public static AutoAppPermissionsFragment newInstance(String str, UserHandle userHandle) {
        AutoAppPermissionsFragment autoAppPermissionsFragment = new AutoAppPermissionsFragment();
        setPackageNameAndUserHandle(autoAppPermissionsFragment, str, userHandle);
        return autoAppPermissionsFragment;
    }

    private static <T extends Fragment> T setPackageNameAndUserHandle(T t, String str, UserHandle userHandle) {
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        t.setArguments(bundle);
        return t;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLoading(true);
        FragmentActivity requireActivity = requireActivity();
        PackageInfo packageInfo = AutoPermissionsUtils.getPackageInfo(requireActivity, getArguments().getString("android.intent.extra.PACKAGE_NAME"), (UserHandle) getArguments().getParcelable("android.intent.extra.USER"));
        if (packageInfo == null) {
            Toast.makeText(getContext(), (int) R.string.app_not_found_dlg_title, 1).show();
            requireActivity.finish();
            return;
        }
        setHeaderLabel(getContext().getString(R.string.app_permissions));
        setAction(getContext().getString(R.string.all_permissions), new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionsFragment$WB3VgfrT9M5ftGVbdBTJTjiyaNw
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AutoAppPermissionsFragment.this.lambda$onCreate$0$AutoAppPermissionsFragment(view);
            }
        });
        this.mAppPermissions = new AppPermissions(requireActivity, packageInfo, true, new Runnable() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionsFragment$OLk_MIsxu2qLg2LxQD03lojDBu4
            @Override // java.lang.Runnable
            public final void run() {
                AutoAppPermissionsFragment.this.lambda$onCreate$1$AutoAppPermissionsFragment();
            }
        });
        this.mCollator = Collator.getInstance(getContext().getResources().getConfiguration().getLocales().get(0));
    }

    public /* synthetic */ void lambda$onCreate$0$AutoAppPermissionsFragment(View view) {
        showAllPermissions();
    }

    public /* synthetic */ void lambda$onCreate$1$AutoAppPermissionsFragment() {
        getActivity().finish();
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mAppPermissions.refresh();
        bindUi(this.mAppPermissions.getPackageInfo());
        updatePreferences();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        getPreferenceScreen().removeAll();
    }

    private void showAllPermissions() {
        AutoAllAppPermissionsFragment newInstance = AutoAllAppPermissionsFragment.newInstance(getArguments().getString("android.intent.extra.PACKAGE_NAME"), (UserHandle) getArguments().getParcelable("android.intent.extra.USER"));
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, newInstance);
        beginTransaction.addToBackStack("AllPerms");
        beginTransaction.commit();
    }

    protected void bindUi(PackageInfo packageInfo) {
        getPreferenceScreen().addPreference(AutoPermissionsUtils.createHeaderPreference(getContext(), packageInfo.applicationInfo));
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setKey("allowed_permissions_group");
        preferenceCategory.setTitle(R.string.allowed_header);
        getPreferenceScreen().addPreference(preferenceCategory);
        PreferenceCategory preferenceCategory2 = new PreferenceCategory(getContext());
        preferenceCategory2.setKey("denied_permissions_group");
        preferenceCategory2.setTitle(R.string.denied_header);
        getPreferenceScreen().addPreference(preferenceCategory2);
    }

    private void updatePreferences() {
        Context context = getPreferenceManager().getContext();
        if (context == null) {
            return;
        }
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("allowed_permissions_group");
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("denied_permissions_group");
        preferenceCategory.removeAll();
        preferenceCategory2.removeAll();
        PreferenceScreen preferenceScreen = this.mExtraScreen;
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
            this.mExtraScreen.addPreference(AutoPermissionsUtils.createHeaderPreference(getContext(), this.mAppPermissions.getPackageInfo().applicationInfo));
        }
        Preference preference = new Preference(context);
        preference.setIcon(R.drawable.ic_toc);
        preference.setTitle(R.string.additional_permissions);
        ArrayList arrayList = new ArrayList(this.mAppPermissions.getPermissionGroups());
        arrayList.sort(new Comparator() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionsFragment$0IgdyIBXBcORlFaRp1-n9lhVPhY
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AutoAppPermissionsFragment.this.lambda$updatePreferences$2$AutoAppPermissionsFragment((AppPermissionGroup) obj, (AppPermissionGroup) obj2);
            }
        });
        preferenceCategory.setOrderingAsAdded(true);
        preferenceCategory2.setOrderingAsAdded(true);
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            AppPermissionGroup appPermissionGroup = (AppPermissionGroup) arrayList.get(i);
            if (Utils.shouldShowPermission(getContext(), appPermissionGroup)) {
                boolean equals = appPermissionGroup.getDeclaringPackage().equals("android");
                Preference createPermissionPreference = createPermissionPreference(getContext(), appPermissionGroup);
                if (equals) {
                    (appPermissionGroup.areRuntimePermissionsGranted() ? preferenceCategory : preferenceCategory2).addPreference(createPermissionPreference);
                } else {
                    if (this.mExtraScreen == null) {
                        this.mExtraScreen = getPreferenceManager().createPreferenceScreen(context);
                        this.mExtraScreen.addPreference(AutoPermissionsUtils.createHeaderPreference(getContext(), this.mAppPermissions.getPackageInfo().applicationInfo));
                    }
                    this.mExtraScreen.addPreference(createPermissionPreference);
                    if (appPermissionGroup.areRuntimePermissionsGranted()) {
                        z = true;
                    }
                }
            }
        }
        if (this.mExtraScreen != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionsFragment$hvwwDUtLNDDcXnYYkS-A8cLgiUE
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference2) {
                    return AutoAppPermissionsFragment.this.lambda$updatePreferences$3$AutoAppPermissionsFragment(preference2);
                }
            });
            int preferenceCount = this.mExtraScreen.getPreferenceCount() - 1;
            preference.setSummary(getResources().getQuantityString(R.plurals.additional_permissions_more, preferenceCount, Integer.valueOf(preferenceCount)));
            (z ? preferenceCategory : preferenceCategory2).addPreference(preference);
        }
        if (preferenceCategory.getPreferenceCount() == 0) {
            Preference preference2 = new Preference(context);
            preference2.setTitle(getString(R.string.no_permissions_allowed));
            preference2.setSelectable(false);
            preferenceCategory.addPreference(preference2);
        }
        if (preferenceCategory2.getPreferenceCount() == 0) {
            Preference preference3 = new Preference(context);
            preference3.setTitle(getString(R.string.no_permissions_denied));
            preference3.setSelectable(false);
            preferenceCategory2.addPreference(preference3);
        }
        setLoading(false);
    }

    public /* synthetic */ int lambda$updatePreferences$2$AutoAppPermissionsFragment(AppPermissionGroup appPermissionGroup, AppPermissionGroup appPermissionGroup2) {
        return this.mCollator.compare(appPermissionGroup.getLabel(), appPermissionGroup2.getLabel());
    }

    public /* synthetic */ boolean lambda$updatePreferences$3$AutoAppPermissionsFragment(Preference preference) {
        AdditionalPermissionsFragment additionalPermissionsFragment = new AdditionalPermissionsFragment();
        setPackageNameAndUserHandle(additionalPermissionsFragment, getArguments().getString("android.intent.extra.PACKAGE_NAME"), (UserHandle) getArguments().getParcelable("android.intent.extra.USER"));
        additionalPermissionsFragment.setTargetFragment(this, 0);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, additionalPermissionsFragment);
        beginTransaction.addToBackStack(null);
        beginTransaction.commit();
        return true;
    }

    private Preference createPermissionPreference(final Context context, final AppPermissionGroup appPermissionGroup) {
        Preference preference = new Preference(context);
        Drawable loadDrawable = Utils.loadDrawable(context.getPackageManager(), appPermissionGroup.getIconPkg(), appPermissionGroup.getIconResId());
        preference.setKey(appPermissionGroup.getName());
        preference.setTitle(appPermissionGroup.getFullLabel());
        preference.setIcon(Utils.applyTint(context, loadDrawable, 16843817));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoAppPermissionsFragment$NAa77C4aaL9psYPIOUVWVy69tZk
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                return AutoAppPermissionsFragment.lambda$createPermissionPreference$4(AppPermissionGroup.this, context, preference2);
            }
        });
        return preference;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$createPermissionPreference$4(AppPermissionGroup appPermissionGroup, Context context, Preference preference) {
        Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSION");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", appPermissionGroup.getApp().packageName);
        intent.putExtra("android.intent.extra.PERMISSION_NAME", appPermissionGroup.getPermissions().get(0).getName());
        intent.putExtra("android.intent.extra.USER", appPermissionGroup.getUser());
        intent.putExtra("com.android.packageinstaller.extra.CALLER_NAME", AutoAppPermissionsFragment.class.getName());
        context.startActivity(intent);
        return true;
    }

    /* loaded from: classes.dex */
    public static class AdditionalPermissionsFragment extends AutoSettingsFrameFragment {
        AutoAppPermissionsFragment mOuterFragment;

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            this.mOuterFragment = (AutoAppPermissionsFragment) getTargetFragment();
            super.onCreate(bundle);
            setHeaderLabel(this.mOuterFragment.getHeaderLabel());
        }

        @Override // androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            setPreferenceScreen(this.mOuterFragment.mExtraScreen);
        }
    }
}
