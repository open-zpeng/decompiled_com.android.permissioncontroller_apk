package com.android.packageinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseIntArray;
import android.widget.ImageView;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.data.BooleanSharedPreferenceLiveData;
import com.android.packageinstaller.permission.data.ForcedUserSensitiveUidsLiveData;
import com.android.packageinstaller.permission.data.NonSensitivePackagesLiveData;
import com.android.packageinstaller.permission.ui.handheld.AdjustUserSensitiveFragment;
import com.android.packageinstaller.permission.utils.Utils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class AdjustUserSensitiveFragment extends PermissionsFrameFragment {
    private SwitchPreference mAssistantRecordAudioIsUserSensitiveSwitch;
    private Collator mCollator;
    private SwitchPreference mGlobalUserSensitiveSwitch;
    private ArrayMap<String, String> mLabelCache;
    private ArrayList<ApplicationInfo> mSortedNonSensitivityPackages;
    private UserSensitiveOverrideViewModel mViewModel;

    public static AdjustUserSensitiveFragment newInstance() {
        return new AdjustUserSensitiveFragment();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLoading(true, false);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.adjust_user_sensitive_title));
        }
        this.mCollator = Collator.getInstance(getContext().getResources().getConfiguration().getLocales().get(0));
        this.mViewModel = (UserSensitiveOverrideViewModel) ViewModelProviders.of(this, new UserSensitiveOverrideViewModel.Factory(getActivity().getApplication())).get(UserSensitiveOverrideViewModel.class);
        this.mViewModel.getNonSensitivePackagesLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$IjGlYmpeipDPU3mAFeVEPgMm0jY
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                AdjustUserSensitiveFragment.this.lambda$onCreate$0$AdjustUserSensitiveFragment((ArrayList) obj);
            }
        });
        this.mViewModel.getAllowOverrideUserSensitiveLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$Otu1YOGPeI5irvJkAIDfZ3avx1k
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                AdjustUserSensitiveFragment.this.lambda$onCreate$1$AdjustUserSensitiveFragment((Boolean) obj);
            }
        });
        this.mViewModel.getForcedUserSensitiveUidsLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$bQdsJpsnF2AoP3iMzKQwxzZ_nKo
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                AdjustUserSensitiveFragment.this.lambda$onCreate$2$AdjustUserSensitiveFragment((SparseIntArray) obj);
            }
        });
        this.mViewModel.getAssistantRecordAudioIsUserSensitiveLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$ojVZWfUSpillh4FMzHEk5C4ZMTw
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                AdjustUserSensitiveFragment.this.lambda$onCreate$3$AdjustUserSensitiveFragment((Boolean) obj);
            }
        });
        addPreferencesFromResource(R.xml.adjust_user_sensitive);
        this.mGlobalUserSensitiveSwitch = (SwitchPreference) findPreference("global");
        this.mGlobalUserSensitiveSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$Ztt7KammUDUk_c-zgkFdfp-9OFU
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return AdjustUserSensitiveFragment.this.lambda$onCreate$4$AdjustUserSensitiveFragment(preference, obj);
            }
        });
        this.mAssistantRecordAudioIsUserSensitiveSwitch = (SwitchPreference) findPreference("assistantrecordaudio");
        this.mAssistantRecordAudioIsUserSensitiveSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$iwQkFvO3SL2YHfNl29TqTqAgTJE
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                return AdjustUserSensitiveFragment.this.lambda$onCreate$5$AdjustUserSensitiveFragment(preference, obj);
            }
        });
    }

    public /* synthetic */ void lambda$onCreate$0$AdjustUserSensitiveFragment(ArrayList arrayList) {
        updateOverrideUi();
    }

    public /* synthetic */ void lambda$onCreate$1$AdjustUserSensitiveFragment(Boolean bool) {
        updateOverrideSwitches();
    }

    public /* synthetic */ void lambda$onCreate$2$AdjustUserSensitiveFragment(SparseIntArray sparseIntArray) {
        updatePerPackageOverrideSwitches();
    }

    public /* synthetic */ void lambda$onCreate$3$AdjustUserSensitiveFragment(Boolean bool) {
        this.mAssistantRecordAudioIsUserSensitiveSwitch.setChecked(bool.booleanValue());
    }

    public /* synthetic */ boolean lambda$onCreate$4$AdjustUserSensitiveFragment(Preference preference, Object obj) {
        this.mViewModel.setAllowOverrideUserSensitive(((Boolean) obj).booleanValue());
        return true;
    }

    public /* synthetic */ boolean lambda$onCreate$5$AdjustUserSensitiveFragment(Preference preference, Object obj) {
        this.mViewModel.setAssistantRecordAudioIsUserSensitive(((Boolean) obj).booleanValue());
        return true;
    }

    private void updateOverrideUi() {
        this.mSortedNonSensitivityPackages = this.mViewModel.getNonSensitivePackagesLiveData().getValue();
        int size = this.mSortedNonSensitivityPackages.size();
        this.mLabelCache = new ArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            ApplicationInfo applicationInfo = this.mSortedNonSensitivityPackages.get(i);
            this.mLabelCache.put(applicationInfo.packageName, Utils.getFullAppLabel(applicationInfo, getContext()));
        }
        this.mSortedNonSensitivityPackages.sort(new Comparator() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$_t2rKJY7TDfz1NPpEv93ZZmKyAE
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AdjustUserSensitiveFragment.this.lambda$updateOverrideUi$6$AdjustUserSensitiveFragment((ApplicationInfo) obj, (ApplicationInfo) obj2);
            }
        });
        updateOverrideSwitches();
    }

    public /* synthetic */ int lambda$updateOverrideUi$6$AdjustUserSensitiveFragment(ApplicationInfo applicationInfo, ApplicationInfo applicationInfo2) {
        return this.mCollator.compare(this.mLabelCache.get(applicationInfo.packageName), this.mLabelCache.get(applicationInfo2.packageName));
    }

    private void updateOverrideSwitches() {
        this.mGlobalUserSensitiveSwitch.setChecked(this.mViewModel.getAllowOverrideUserSensitiveLiveData().getValue().booleanValue());
        updatePerPackageOverrideSwitches();
    }

    private void updatePerPackageOverrideSwitches() {
        if (this.mSortedNonSensitivityPackages == null || this.mViewModel.getForcedUserSensitiveUidsLiveData().getValue() == null) {
            return;
        }
        Context context = getContext();
        setLoading(false, true);
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("perapp");
        ArrayMap arrayMap = new ArrayMap();
        int preferenceCount = preferenceCategory.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            NonUserSensitiveAppPreference nonUserSensitiveAppPreference = (NonUserSensitiveAppPreference) preferenceCategory.getPreference(i);
            arrayMap.put(nonUserSensitiveAppPreference.getKey(), nonUserSensitiveAppPreference);
        }
        int size = this.mSortedNonSensitivityPackages.size();
        for (int i2 = 0; i2 < size; i2++) {
            final ApplicationInfo applicationInfo = this.mSortedNonSensitivityPackages.get(i2);
            NonUserSensitiveAppPreference nonUserSensitiveAppPreference2 = (NonUserSensitiveAppPreference) arrayMap.remove(applicationInfo.packageName + applicationInfo.uid);
            if (nonUserSensitiveAppPreference2 == null) {
                nonUserSensitiveAppPreference2 = new NonUserSensitiveAppPreference(context, this.mLabelCache.get(applicationInfo.packageName), Utils.getBadgedIcon(context, applicationInfo));
                nonUserSensitiveAppPreference2.setKey(applicationInfo.packageName + applicationInfo.uid);
                nonUserSensitiveAppPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$khmyAJUtzD4ZTLkgu3ZW5LfyweI
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return AdjustUserSensitiveFragment.this.lambda$updatePerPackageOverrideSwitches$7$AdjustUserSensitiveFragment(applicationInfo, preference, obj);
                    }
                });
                preferenceCategory.addPreference(nonUserSensitiveAppPreference2);
            }
            nonUserSensitiveAppPreference2.setOrder(i2);
            nonUserSensitiveAppPreference2.setChecked(this.mViewModel.getForcedUserSensitiveUidsLiveData().getValue().indexOfKey(applicationInfo.uid) >= 0);
            nonUserSensitiveAppPreference2.setEnabled(this.mViewModel.getAllowOverrideUserSensitiveLiveData().getValue().booleanValue());
        }
        int size2 = arrayMap.size();
        for (int i3 = 0; i3 < size2; i3++) {
            preferenceCategory.removePreference((Preference) arrayMap.valueAt(i3));
        }
    }

    public /* synthetic */ boolean lambda$updatePerPackageOverrideSwitches$7$AdjustUserSensitiveFragment(ApplicationInfo applicationInfo, Preference preference, Object obj) {
        this.mViewModel.setUidUserSensitive(applicationInfo.uid, ((Boolean) obj).booleanValue());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NonUserSensitiveAppPreference extends SwitchPreference {
        private final int mIconSize;
        private boolean mIsIconSizeSet;

        NonUserSensitiveAppPreference(Context context, String str, Drawable drawable) {
            super(context);
            this.mIsIconSizeSet = false;
            this.mIconSize = context.getResources().getDimensionPixelSize(R.dimen.secondary_app_icon_size);
            setTitle(str);
            setIcon(drawable);
        }

        @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            if (!this.mIsIconSizeSet) {
                ImageView imageView = (ImageView) preferenceViewHolder.findViewById(16908294);
                imageView.setMaxWidth(this.mIconSize);
                imageView.setMaxHeight(this.mIconSize);
                this.mIsIconSizeSet = true;
            }
            super.onBindViewHolder(preferenceViewHolder);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UserSensitiveOverrideViewModel extends AndroidViewModel {
        private final BooleanSharedPreferenceLiveData mAllowOverrideUserSensitive;
        private final BooleanSharedPreferenceLiveData mAssistantRecordAudioIsUserSensitive;
        private final ForcedUserSensitiveUidsLiveData mForcedUserSensitiveUids;
        private final NonSensitivePackagesLiveData mNonSensitivePackages;
        private final SharedPreferences mPrefs;

        UserSensitiveOverrideViewModel(Application application) {
            super(application);
            this.mPrefs = Utils.getParentUserContext(application).getSharedPreferences("preferences", 0);
            this.mNonSensitivePackages = NonSensitivePackagesLiveData.get(application);
            this.mAllowOverrideUserSensitive = BooleanSharedPreferenceLiveData.get("allow_override_user_sensitive_key", application);
            this.mForcedUserSensitiveUids = ForcedUserSensitiveUidsLiveData.get(application);
            this.mAssistantRecordAudioIsUserSensitive = BooleanSharedPreferenceLiveData.get("assistant_record_audio_is_user_sensitive_key", application);
        }

        NonSensitivePackagesLiveData getNonSensitivePackagesLiveData() {
            return this.mNonSensitivePackages;
        }

        BooleanSharedPreferenceLiveData getAllowOverrideUserSensitiveLiveData() {
            return this.mAllowOverrideUserSensitive;
        }

        ForcedUserSensitiveUidsLiveData getForcedUserSensitiveUidsLiveData() {
            return this.mForcedUserSensitiveUids;
        }

        BooleanSharedPreferenceLiveData getAssistantRecordAudioIsUserSensitiveLiveData() {
            return this.mAssistantRecordAudioIsUserSensitive;
        }

        private void updatePermissionFlags(final UserHandle userHandle) {
            AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$UserSensitiveOverrideViewModel$r_8VsFC0BdsLJsO5SL96xgib2f0
                @Override // java.lang.Runnable
                public final void run() {
                    AdjustUserSensitiveFragment.UserSensitiveOverrideViewModel.this.lambda$updatePermissionFlags$0$AdjustUserSensitiveFragment$UserSensitiveOverrideViewModel(userHandle);
                }
            });
        }

        public /* synthetic */ void lambda$updatePermissionFlags$0$AdjustUserSensitiveFragment$UserSensitiveOverrideViewModel(UserHandle userHandle) {
            Utils.updateUserSensitive(getApplication(), userHandle);
        }

        private void updatePermissionFlags() {
            AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$AdjustUserSensitiveFragment$UserSensitiveOverrideViewModel$FR8QecU5huScUPxKTYFsX0yBYXQ
                @Override // java.lang.Runnable
                public final void run() {
                    AdjustUserSensitiveFragment.UserSensitiveOverrideViewModel.this.lambda$updatePermissionFlags$1$AdjustUserSensitiveFragment$UserSensitiveOverrideViewModel();
                }
            });
        }

        public /* synthetic */ void lambda$updatePermissionFlags$1$AdjustUserSensitiveFragment$UserSensitiveOverrideViewModel() {
            List<UserHandle> userProfiles = ((UserManager) getApplication().getSystemService(UserManager.class)).getUserProfiles();
            int size = userProfiles.size();
            for (int i = 0; i < size; i++) {
                Utils.updateUserSensitive(getApplication(), userProfiles.get(i));
            }
        }

        void setUidUserSensitive(int i, boolean z) {
            Set<String> arraySet;
            Set<String> stringSet = this.mPrefs.getStringSet("forced_user_sensitive_uids_key", null);
            String valueOf = String.valueOf(i);
            if (z) {
                if (stringSet == null) {
                    arraySet = Collections.singleton(valueOf);
                } else {
                    arraySet = new ArraySet<>(stringSet);
                    arraySet.add(valueOf);
                }
            } else if (stringSet == null) {
                return;
            } else {
                arraySet = new ArraySet<>(stringSet);
                arraySet.remove(valueOf);
            }
            if (arraySet.isEmpty()) {
                this.mPrefs.edit().remove("forced_user_sensitive_uids_key").apply();
            } else {
                this.mPrefs.edit().putStringSet("forced_user_sensitive_uids_key", arraySet).apply();
            }
            updatePermissionFlags(UserHandle.getUserHandleForUid(i));
        }

        void setAllowOverrideUserSensitive(boolean z) {
            SharedPreferences.Editor edit = this.mPrefs.edit();
            if (z) {
                edit.putBoolean("allow_override_user_sensitive_key", true);
                ArraySet arraySet = new ArraySet();
                ArrayList<ApplicationInfo> value = getNonSensitivePackagesLiveData().getValue();
                int size = value.size();
                for (int i = 0; i < size; i++) {
                    arraySet.add(String.valueOf(value.get(i).uid));
                }
                edit.putStringSet("forced_user_sensitive_uids_key", arraySet);
            } else {
                edit.remove("allow_override_user_sensitive_key");
                edit.remove("forced_user_sensitive_uids_key");
            }
            edit.apply();
            updatePermissionFlags();
        }

        void setAssistantRecordAudioIsUserSensitive(boolean z) {
            SharedPreferences.Editor edit = this.mPrefs.edit();
            if (z) {
                edit.putBoolean("assistant_record_audio_is_user_sensitive_key", true);
            } else {
                edit.remove("assistant_record_audio_is_user_sensitive_key");
            }
            edit.apply();
            updatePermissionFlags();
        }

        /* loaded from: classes.dex */
        public static class Factory implements ViewModelProvider.Factory {
            private Application mApplication;

            Factory(Application application) {
                this.mApplication = application;
            }

            @Override // androidx.lifecycle.ViewModelProvider.Factory
            public <T extends ViewModel> T create(Class<T> cls) {
                return new UserSensitiveOverrideViewModel(this.mApplication);
            }
        }
    }
}
