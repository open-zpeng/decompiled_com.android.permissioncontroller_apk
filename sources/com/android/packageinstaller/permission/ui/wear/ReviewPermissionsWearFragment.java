package com.android.packageinstaller.permission.ui.wear;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;
import androidx.wear.ble.view.WearableDialogHelper;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class ReviewPermissionsWearFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private AppPermissions mAppPermissions;
    private boolean mHasConfirmedRevoke;
    private PreferenceCategory mNewPermissionsCategory;

    public static ReviewPermissionsWearFragment newInstance(PackageInfo packageInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.android.packageinstaller.permission.ui.extra.PACKAGE_INFO", packageInfo);
        ReviewPermissionsWearFragment reviewPermissionsWearFragment = new ReviewPermissionsWearFragment();
        reviewPermissionsWearFragment.setArguments(bundle);
        reviewPermissionsWearFragment.setRetainInstance(true);
        return reviewPermissionsWearFragment;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        PackageInfo packageInfo = (PackageInfo) getArguments().getParcelable("com.android.packageinstaller.permission.ui.extra.PACKAGE_INFO");
        if (packageInfo == null) {
            activity.finish();
            return;
        }
        boolean z = false;
        this.mAppPermissions = new AppPermissions(activity, packageInfo, false, new Runnable() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$ReviewPermissionsWearFragment$wnzAxTZjgBLe_st8TIvhFz1vC3w
            @Override // java.lang.Runnable
            public final void run() {
                ReviewPermissionsWearFragment.this.lambda$onCreatePreferences$0$ReviewPermissionsWearFragment();
            }
        });
        if (this.mAppPermissions.getPermissionGroups().isEmpty()) {
            activity.finish();
            return;
        }
        Iterator<AppPermissionGroup> it = this.mAppPermissions.getPermissionGroups().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            } else if (it.next().isReviewRequired()) {
                z = true;
                break;
            }
        }
        if (z) {
            return;
        }
        activity.finish();
    }

    public /* synthetic */ void lambda$onCreatePreferences$0$ReviewPermissionsWearFragment() {
        getActivity().finish();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppPermissions.refresh();
        loadPreferences();
    }

    private void loadPreferences() {
        SwitchPreference switchPreference;
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null) {
            preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
            setPreferenceScreen(preferenceScreen);
        } else {
            preferenceScreen.removeAll();
        }
        PreferenceCategory preferenceCategory = this.mNewPermissionsCategory;
        this.mNewPermissionsCategory = null;
        boolean isPackageUpdated = isPackageUpdated();
        int i = 100;
        PreferenceCategory preferenceCategory2 = null;
        for (AppPermissionGroup appPermissionGroup : this.mAppPermissions.getPermissionGroups()) {
            if (Utils.shouldShowPermission(getContext(), appPermissionGroup) && "android".equals(appPermissionGroup.getDeclaringPackage())) {
                Preference findPreference = preferenceCategory != null ? preferenceCategory.findPreference(appPermissionGroup.getName()) : null;
                if (findPreference instanceof SwitchPreference) {
                    switchPreference = (SwitchPreference) findPreference;
                } else {
                    switchPreference = new SwitchPreference(getActivity());
                    switchPreference.setKey(appPermissionGroup.getName());
                    switchPreference.setTitle(appPermissionGroup.getLabel());
                    switchPreference.setPersistent(false);
                    switchPreference.setOrder(i);
                    switchPreference.setOnPreferenceChangeListener(this);
                    i++;
                }
                switchPreference.setChecked(appPermissionGroup.areRuntimePermissionsGranted());
                if (appPermissionGroup.isSystemFixed() || appPermissionGroup.isPolicyFixed()) {
                    switchPreference.setEnabled(false);
                } else {
                    switchPreference.setEnabled(true);
                }
                if (!appPermissionGroup.isReviewRequired()) {
                    if (preferenceCategory2 == null) {
                        preferenceCategory2 = new PreferenceCategory(activity);
                        preferenceCategory2.setTitle(R.string.current_permissions_category);
                        preferenceCategory2.setOrder(2);
                        preferenceScreen.addPreference(preferenceCategory2);
                    }
                    preferenceCategory2.addPreference(switchPreference);
                } else if (!isPackageUpdated) {
                    preferenceScreen.addPreference(switchPreference);
                } else {
                    if (this.mNewPermissionsCategory == null) {
                        this.mNewPermissionsCategory = new PreferenceCategory(activity);
                        this.mNewPermissionsCategory.setTitle(R.string.new_permissions_category);
                        this.mNewPermissionsCategory.setOrder(1);
                        preferenceScreen.addPreference(this.mNewPermissionsCategory);
                    }
                    this.mNewPermissionsCategory.addPreference(switchPreference);
                }
            }
        }
        addTitlePreferenceToScreen(preferenceScreen);
        addActionPreferencesToScreen(preferenceScreen);
    }

    private boolean isPackageUpdated() {
        List<AppPermissionGroup> permissionGroups = this.mAppPermissions.getPermissionGroups();
        int size = permissionGroups.size();
        for (int i = 0; i < size; i++) {
            if (!permissionGroups.get(i).isReviewRequired()) {
                return true;
            }
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Log.d("ReviewPermWear", "onPreferenceChange " + ((Object) preference.getTitle()));
        if (this.mHasConfirmedRevoke) {
            return true;
        }
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            if (switchPreference.isChecked()) {
                showWarnRevokeDialog(switchPreference);
                return false;
            }
            return true;
        }
        return false;
    }

    private void showWarnRevokeDialog(final SwitchPreference switchPreference) {
        WearableDialogHelper.DialogBuilder dialogBuilder = new WearableDialogHelper.DialogBuilder(getContext());
        dialogBuilder.setPositiveIcon(R.drawable.cancel_button);
        dialogBuilder.setNegativeIcon(R.drawable.confirm_button);
        dialogBuilder.setPositiveButton(R.string.cancel, (DialogInterface.OnClickListener) null).setNegativeButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$ReviewPermissionsWearFragment$hP1ExdAnWYPwN0GupDM8g1XkvCw
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ReviewPermissionsWearFragment.this.lambda$showWarnRevokeDialog$1$ReviewPermissionsWearFragment(switchPreference, dialogInterface, i);
            }
        }).setMessage(R.string.old_sdk_deny_warning).show();
    }

    public /* synthetic */ void lambda$showWarnRevokeDialog$1$ReviewPermissionsWearFragment(SwitchPreference switchPreference, DialogInterface dialogInterface, int i) {
        switchPreference.setChecked(false);
        this.mHasConfirmedRevoke = true;
    }

    private void confirmPermissionsReview() {
        PreferenceGroup preferenceGroup = this.mNewPermissionsCategory;
        if (preferenceGroup == null) {
            preferenceGroup = getPreferenceScreen();
        }
        int preferenceCount = preferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceGroup.getPreference(i);
            if (preference instanceof TwoStatePreference) {
                TwoStatePreference twoStatePreference = (TwoStatePreference) preference;
                AppPermissionGroup permissionGroup = this.mAppPermissions.getPermissionGroup(preference.getKey());
                if (twoStatePreference.isChecked()) {
                    permissionGroup.grantRuntimePermissions(false);
                } else {
                    permissionGroup.revokeRuntimePermissions(false);
                }
                permissionGroup.unsetReviewRequired();
            }
        }
    }

    private void addTitlePreferenceToScreen(PreferenceScreen preferenceScreen) {
        FragmentActivity activity = getActivity();
        Preference preference = new Preference(activity);
        preferenceScreen.addPreference(preference);
        preference.setIcon(this.mAppPermissions.getPackageInfo().applicationInfo.loadIcon(activity.getPackageManager()));
        String charSequence = this.mAppPermissions.getAppLabel().toString();
        SpannableString spannableString = new SpannableString(getString(isPackageUpdated() ? R.string.permission_review_title_template_update : R.string.permission_review_title_template_install, charSequence));
        int indexOf = spannableString.toString().indexOf(charSequence, 0);
        int length = charSequence.length();
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(16843829, typedValue, true);
        spannableString.setSpan(new ForegroundColorSpan(activity.getColor(typedValue.resourceId)), indexOf, length + indexOf, 0);
        preference.setTitle(spannableString);
        preference.setSelectable(false);
        preference.setLayoutResource(R.layout.wear_review_permission_title_pref);
    }

    private void addActionPreferencesToScreen(PreferenceScreen preferenceScreen) {
        final FragmentActivity activity = getActivity();
        Preference preference = new Preference(activity);
        preference.setTitle(R.string.review_button_cancel);
        preference.setOrder(100000);
        preference.setEnabled(true);
        preference.setLayoutResource(R.layout.wear_review_permission_action_pref);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$ReviewPermissionsWearFragment$s_qPUL4cHrE6rJhgKydWaV5DXpA
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                return ReviewPermissionsWearFragment.this.lambda$addActionPreferencesToScreen$2$ReviewPermissionsWearFragment(activity, preference2);
            }
        });
        preferenceScreen.addPreference(preference);
        Preference preference2 = new Preference(activity);
        preference2.setTitle(R.string.review_button_continue);
        preference2.setOrder(100001);
        preference2.setEnabled(true);
        preference2.setLayoutResource(R.layout.wear_review_permission_action_pref);
        preference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.wear.-$$Lambda$ReviewPermissionsWearFragment$rmU-KC0qeZ5mWAFgZq7gUN2n1XE
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference3) {
                return ReviewPermissionsWearFragment.this.lambda$addActionPreferencesToScreen$3$ReviewPermissionsWearFragment(preference3);
            }
        });
        preferenceScreen.addPreference(preference2);
    }

    public /* synthetic */ boolean lambda$addActionPreferencesToScreen$2$ReviewPermissionsWearFragment(Activity activity, Preference preference) {
        executeCallback(false);
        activity.setResult(0);
        activity.finish();
        return true;
    }

    public /* synthetic */ boolean lambda$addActionPreferencesToScreen$3$ReviewPermissionsWearFragment(Preference preference) {
        confirmPermissionsReview();
        executeCallback(true);
        getActivity().finish();
        return true;
    }

    private void executeCallback(boolean z) {
        IntentSender intentSender;
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (z && (intentSender = (IntentSender) activity.getIntent().getParcelableExtra("android.intent.extra.INTENT")) != null) {
            try {
                int i = activity.getIntent().getBooleanExtra("android.intent.extra.RESULT_NEEDED", false) ? 33554432 : 0;
                activity.startIntentSenderForResult(intentSender, -1, null, i, i, 0);
                return;
            } catch (IntentSender.SendIntentException unused) {
                return;
            }
        }
        RemoteCallback parcelableExtra = activity.getIntent().getParcelableExtra("android.intent.extra.REMOTE_CALLBACK");
        if (parcelableExtra != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("android.intent.extra.RETURN_RESULT", z);
            parcelableExtra.sendResult(bundle);
        }
    }
}
