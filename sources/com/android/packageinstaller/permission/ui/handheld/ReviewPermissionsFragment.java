package com.android.packageinstaller.permission.ui.handheld;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.os.UserHandle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.car.ui.R;
import com.android.packageinstaller.PermissionControllerStatsLog;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.model.Permission;
import com.android.packageinstaller.permission.ui.handheld.PermissionPreference;
import com.android.packageinstaller.permission.utils.ArrayUtils;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/* loaded from: classes.dex */
public final class ReviewPermissionsFragment extends PreferenceFragmentCompat implements View.OnClickListener, PermissionPreference.PermissionPreferenceChangeListener, PermissionPreference.PermissionPreferenceOwnerFragment {
    private static final String LOG_TAG = "ReviewPermissionsFragment";
    private AppPermissions mAppPermissions;
    private Button mCancelButton;
    private Button mContinueButton;
    private PreferenceCategory mCurrentPermissionsCategory;
    private boolean mHasConfirmedRevoke;
    private Button mMoreInfoButton;
    private PreferenceCategory mNewPermissionsCategory;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    public static ReviewPermissionsFragment newInstance(PackageInfo packageInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("com.android.packageinstaller.permission.ui.extra.PACKAGE_INFO", packageInfo);
        ReviewPermissionsFragment reviewPermissionsFragment = new ReviewPermissionsFragment();
        reviewPermissionsFragment.setArguments(bundle);
        reviewPermissionsFragment.setRetainInstance(true);
        return reviewPermissionsFragment;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        PackageInfo packageInfo = (PackageInfo) getArguments().getParcelable("com.android.packageinstaller.permission.ui.extra.PACKAGE_INFO");
        if (packageInfo == null) {
            activity.finish();
            return;
        }
        this.mAppPermissions = new AppPermissions(activity, packageInfo, false, true, new Runnable() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$ReviewPermissionsFragment$PNa9X3g8pAh5qKG3seITbXRZFkE
            @Override // java.lang.Runnable
            public final void run() {
                ReviewPermissionsFragment.this.lambda$onCreate$0$ReviewPermissionsFragment();
            }
        });
        boolean z = false;
        for (AppPermissionGroup appPermissionGroup : this.mAppPermissions.getPermissionGroups()) {
            if (appPermissionGroup.isReviewRequired() || (appPermissionGroup.getBackgroundPermissions() != null && appPermissionGroup.getBackgroundPermissions().isReviewRequired())) {
                z = true;
                break;
            }
        }
        if (z) {
            return;
        }
        confirmPermissionsReview();
        executeCallback(true);
        activity.finish();
    }

    public /* synthetic */ void lambda$onCreate$0$ReviewPermissionsFragment() {
        getActivity().finish();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        bindUi();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppPermissions.refresh();
        loadPreferences();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (view == this.mContinueButton) {
            confirmPermissionsReview();
            executeCallback(true);
        } else if (view == this.mCancelButton) {
            executeCallback(false);
            activity.setResult(0);
        } else if (view == this.mMoreInfoButton) {
            Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
            intent.putExtra("android.intent.extra.PACKAGE_NAME", this.mAppPermissions.getPackageInfo().packageName);
            intent.putExtra("android.intent.extra.USER", UserHandle.getUserHandleForUid(this.mAppPermissions.getPackageInfo().applicationInfo.uid));
            intent.putExtra("com.android.packageinstaller.extra.ALL_PERMISSIONS", true);
            getActivity().startActivity(intent);
        }
        activity.finish();
    }

    private void grantReviewedPermission(AppPermissionGroup appPermissionGroup) {
        int size = appPermissionGroup.getPermissions().size();
        String[] strArr = null;
        for (int i = 0; i < size; i++) {
            Permission permission = appPermissionGroup.getPermissions().get(i);
            if (permission.isReviewRequired()) {
                strArr = ArrayUtils.appendString(strArr, permission.getName());
            }
        }
        if (strArr != null) {
            appPermissionGroup.grantRuntimePermissions(false, strArr);
        }
    }

    private void confirmPermissionsReview() {
        String[] strArr;
        ArrayList arrayList = new ArrayList();
        PreferenceCategory preferenceCategory = this.mNewPermissionsCategory;
        if (preferenceCategory != null) {
            arrayList.add(preferenceCategory);
            arrayList.add(this.mCurrentPermissionsCategory);
        } else {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen != null) {
                arrayList.add(preferenceScreen);
            }
        }
        int size = arrayList.size();
        long nextLong = new Random().nextLong();
        for (int i = 0; i < size; i++) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) arrayList.get(i);
            int preferenceCount = preferenceGroup.getPreferenceCount();
            for (int i2 = 0; i2 < preferenceCount; i2++) {
                Preference preference = preferenceGroup.getPreference(i2);
                if (preference instanceof PermissionReviewPreference) {
                    PermissionReviewPreference permissionReviewPreference = (PermissionReviewPreference) preference;
                    AppPermissionGroup group = permissionReviewPreference.getGroup();
                    if (group.isReviewRequired() && !permissionReviewPreference.wasChanged()) {
                        grantReviewedPermission(group);
                    }
                    logReviewPermissionsFragmentResult(nextLong, group);
                    AppPermissionGroup backgroundPermissions = group.getBackgroundPermissions();
                    if (backgroundPermissions != null) {
                        if (backgroundPermissions.isReviewRequired() && !permissionReviewPreference.wasChanged()) {
                            grantReviewedPermission(backgroundPermissions);
                        }
                        logReviewPermissionsFragmentResult(nextLong, backgroundPermissions);
                    }
                }
            }
        }
        this.mAppPermissions.persistChanges(true);
        PackageManager packageManager = getContext().getPackageManager();
        PackageInfo packageInfo = this.mAppPermissions.getPackageInfo();
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(packageInfo.applicationInfo.uid);
        for (String str : packageInfo.requestedPermissions) {
            try {
                packageManager.updatePermissionFlags(str, packageInfo.packageName, 64, 0, userHandleForUid);
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, "Cannot unmark " + str + " requested by " + packageInfo.packageName + " as review required", e);
            }
        }
    }

    private void logReviewPermissionsFragmentResult(long j, AppPermissionGroup appPermissionGroup) {
        ArrayList<Permission> permissions = appPermissionGroup.getPermissions();
        int size = permissions.size();
        for (int i = 0; i < size; i++) {
            Permission permission = permissions.get(i);
            PermissionControllerStatsLog.write(211, j, appPermissionGroup.getApp().applicationInfo.uid, appPermissionGroup.getApp().packageName, permission.getName(), permission.isGrantedIncludingAppOp());
            String str = LOG_TAG;
            Log.v(str, "Permission grant via permission review changeId=" + j + " uid=" + appPermissionGroup.getApp().applicationInfo.uid + " packageName=" + appPermissionGroup.getApp().packageName + " permission=" + permission.getName() + " granted=" + permission.isGrantedIncludingAppOp());
        }
    }

    private void bindUi() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        ((ImageView) activity.requireViewById(R.id.app_icon)).setImageDrawable(this.mAppPermissions.getPackageInfo().applicationInfo.loadIcon(activity.getPackageManager()));
        Spanned fromHtml = Html.fromHtml(getString(isPackageUpdated() ? R.string.permission_review_title_template_update : R.string.permission_review_title_template_install, this.mAppPermissions.getAppLabel()), 0);
        activity.setTitle(fromHtml.toString());
        ((TextView) activity.requireViewById(R.id.permissions_message)).setText(fromHtml);
        this.mContinueButton = (Button) getActivity().requireViewById(R.id.continue_button);
        this.mContinueButton.setOnClickListener(this);
        this.mCancelButton = (Button) getActivity().requireViewById(R.id.cancel_button);
        this.mCancelButton.setOnClickListener(this);
        if (activity.getPackageManager().arePermissionsIndividuallyControlled()) {
            this.mMoreInfoButton = (Button) getActivity().requireViewById(R.id.permission_more_info_button);
            this.mMoreInfoButton.setOnClickListener(this);
            this.mMoreInfoButton.setVisibility(0);
        }
    }

    private PermissionReviewPreference getPreference(String str) {
        PreferenceCategory preferenceCategory;
        PreferenceCategory preferenceCategory2 = this.mNewPermissionsCategory;
        if (preferenceCategory2 != null) {
            PermissionReviewPreference permissionReviewPreference = (PermissionReviewPreference) preferenceCategory2.findPreference(str);
            return (permissionReviewPreference != null || (preferenceCategory = this.mCurrentPermissionsCategory) == null) ? permissionReviewPreference : (PermissionReviewPreference) preferenceCategory.findPreference(str);
        }
        return (PermissionReviewPreference) getPreferenceScreen().findPreference(str);
    }

    private void loadPreferences() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null) {
            preferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());
            setPreferenceScreen(preferenceScreen);
        } else {
            preferenceScreen.removeAll();
        }
        this.mCurrentPermissionsCategory = null;
        this.mNewPermissionsCategory = null;
        boolean isPackageUpdated = isPackageUpdated();
        for (AppPermissionGroup appPermissionGroup : this.mAppPermissions.getPermissionGroups()) {
            if (Utils.shouldShowPermission(getContext(), appPermissionGroup) && "android".equals(appPermissionGroup.getDeclaringPackage())) {
                PermissionReviewPreference preference = getPreference(appPermissionGroup.getName());
                if (preference == null) {
                    preference = new PermissionReviewPreference(this, appPermissionGroup, this);
                    preference.setKey(appPermissionGroup.getName());
                    preference.setIcon(Utils.applyTint(getContext(), Utils.loadDrawable(activity.getPackageManager(), appPermissionGroup.getIconPkg(), appPermissionGroup.getIconResId()), 16843817));
                    preference.setTitle(appPermissionGroup.getLabel());
                } else {
                    preference.updateUi();
                }
                if (!appPermissionGroup.isReviewRequired() && (appPermissionGroup.getBackgroundPermissions() == null || !appPermissionGroup.getBackgroundPermissions().isReviewRequired())) {
                    if (this.mCurrentPermissionsCategory == null) {
                        this.mCurrentPermissionsCategory = new PreferenceCategory(activity);
                        this.mCurrentPermissionsCategory.setTitle(R.string.current_permissions_category);
                        this.mCurrentPermissionsCategory.setOrder(2);
                        preferenceScreen.addPreference(this.mCurrentPermissionsCategory);
                    }
                    this.mCurrentPermissionsCategory.addPreference(preference);
                } else if (!isPackageUpdated) {
                    preferenceScreen.addPreference(preference);
                } else {
                    if (this.mNewPermissionsCategory == null) {
                        this.mNewPermissionsCategory = new PreferenceCategory(activity);
                        this.mNewPermissionsCategory.setTitle(R.string.new_permissions_category);
                        this.mNewPermissionsCategory.setOrder(1);
                        preferenceScreen.addPreference(this.mNewPermissionsCategory);
                    }
                    this.mNewPermissionsCategory.addPreference(preference);
                }
            }
        }
    }

    private boolean isPackageUpdated() {
        List<AppPermissionGroup> permissionGroups = this.mAppPermissions.getPermissionGroups();
        int size = permissionGroups.size();
        for (int i = 0; i < size; i++) {
            AppPermissionGroup appPermissionGroup = permissionGroups.get(i);
            if (!appPermissionGroup.isReviewRequired() && (appPermissionGroup.getBackgroundPermissions() == null || !appPermissionGroup.getBackgroundPermissions().isReviewRequired())) {
                return true;
            }
        }
        return false;
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

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionPreference.PermissionPreferenceChangeListener
    public boolean shouldConfirmDefaultPermissionRevoke() {
        return !this.mHasConfirmedRevoke;
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionPreference.PermissionPreferenceChangeListener
    public void hasConfirmDefaultPermissionRevoke() {
        this.mHasConfirmedRevoke = true;
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionPreference.PermissionPreferenceChangeListener
    public void onPreferenceChanged(String str) {
        getPreference(str).setChanged();
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionPreference.PermissionPreferenceOwnerFragment
    public void onDenyAnyWay(String str, int i) {
        getPreference(str).onDenyAnyWay(i);
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionPreference.PermissionPreferenceOwnerFragment
    public void onBackgroundAccessChosen(String str, int i) {
        getPreference(str).onBackgroundAccessChosen(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PermissionReviewPreference extends PermissionPreference {
        private final AppPermissionGroup mGroup;
        private boolean mWasChanged;

        PermissionReviewPreference(PreferenceFragmentCompat preferenceFragmentCompat, AppPermissionGroup appPermissionGroup, PermissionPreference.PermissionPreferenceChangeListener permissionPreferenceChangeListener) {
            super(preferenceFragmentCompat, appPermissionGroup, permissionPreferenceChangeListener, 0);
            this.mGroup = appPermissionGroup;
            updateUi();
        }

        AppPermissionGroup getGroup() {
            return this.mGroup;
        }

        void setChanged() {
            this.mWasChanged = true;
            updateUi();
        }

        boolean wasChanged() {
            return this.mWasChanged;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.android.packageinstaller.permission.ui.handheld.PermissionPreference
        public void updateUi() {
            if (this.mGroup == null) {
                return;
            }
            super.updateUi();
            if (isEnabled()) {
                if (this.mGroup.isReviewRequired() && !this.mWasChanged) {
                    setSummary(this.mGroup.getDescription());
                    setCheckedOverride(true);
                } else if (TextUtils.isEmpty(getSummary())) {
                    setSummary(this.mGroup.getDescription());
                }
            }
        }
    }
}
