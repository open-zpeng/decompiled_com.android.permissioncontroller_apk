package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Pair;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.ui.DefaultAppChildFragment.Parent;
import com.android.packageinstaller.role.ui.DefaultAppConfirmationDialogFragment;
import com.android.packageinstaller.role.ui.DefaultAppViewModel;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class DefaultAppChildFragment<PF extends PreferenceFragmentCompat & Parent> extends Fragment implements DefaultAppConfirmationDialogFragment.Listener, Preference.OnPreferenceClickListener {
    private Role mRole;
    private String mRoleName;
    private UserHandle mUser;
    private DefaultAppViewModel mViewModel;
    private static final String PREFERENCE_KEY_NONE = DefaultAppChildFragment.class.getName() + ".preference.NONE";
    private static final String PREFERENCE_KEY_DESCRIPTION = DefaultAppChildFragment.class.getName() + ".preference.DESCRIPTION";

    /* loaded from: classes.dex */
    public interface Parent {
        TwoStatePreference createApplicationPreference(Context context);

        Preference createFooterPreference(Context context);

        void onPreferenceScreenChanged();

        void setTitle(CharSequence charSequence);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$addPreference$0(Preference preference, Object obj) {
        return false;
    }

    public static DefaultAppChildFragment newInstance(String str, UserHandle userHandle) {
        DefaultAppChildFragment defaultAppChildFragment = new DefaultAppChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.ROLE_NAME", str);
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        defaultAppChildFragment.setArguments(bundle);
        return defaultAppChildFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mRoleName = arguments.getString("android.intent.extra.ROLE_NAME");
        this.mUser = (UserHandle) arguments.getParcelable("android.intent.extra.USER");
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        PF requirePreferenceFragment = requirePreferenceFragment();
        FragmentActivity requireActivity = requireActivity();
        this.mRole = Roles.get(requireActivity).get(this.mRoleName);
        requirePreferenceFragment.setTitle(getString(this.mRole.getLabelResource()));
        this.mViewModel = (DefaultAppViewModel) ViewModelProviders.of(this, new DefaultAppViewModel.Factory(this.mRole, this.mUser, requireActivity.getApplication())).get(DefaultAppViewModel.class);
        this.mViewModel.getRoleLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$DefaultAppChildFragment$_oNtc7S7SCa_ms7vIW-B6zO1TJ4
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                DefaultAppChildFragment.this.onRoleChanged((List) obj);
            }
        });
        this.mViewModel.getManageRoleHolderStateLiveData().observe(this, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$DefaultAppChildFragment$OA0JYkHUeryt6eAyFGwpuDkFTk4
            @Override // androidx.lifecycle.Observer
            public final void onChanged(Object obj) {
                DefaultAppChildFragment.this.onManageRoleHolderStateChanged(((Integer) obj).intValue());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRoleChanged(List<Pair<ApplicationInfo, Boolean>> list) {
        Preference preference;
        PreferenceScreen preferenceScreen;
        PF requirePreferenceFragment = requirePreferenceFragment();
        PreferenceManager preferenceManager = requirePreferenceFragment.getPreferenceManager();
        Context context = preferenceManager.getContext();
        PreferenceScreen preferenceScreen2 = requirePreferenceFragment.getPreferenceScreen();
        ArrayMap<String, Preference> arrayMap = new ArrayMap<>();
        if (preferenceScreen2 == null) {
            PreferenceScreen createPreferenceScreen = preferenceManager.createPreferenceScreen(context);
            requirePreferenceFragment.setPreferenceScreen(createPreferenceScreen);
            preferenceScreen = createPreferenceScreen;
            preference = null;
        } else {
            Preference findPreference = preferenceScreen2.findPreference(PREFERENCE_KEY_DESCRIPTION);
            if (findPreference != null) {
                preferenceScreen2.removePreference(findPreference);
                findPreference.setOrder(Preference.DEFAULT_ORDER);
            }
            for (int preferenceCount = preferenceScreen2.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
                Preference preference2 = preferenceScreen2.getPreference(preferenceCount);
                preferenceScreen2.removePreference(preference2);
                preference2.setOrder(Preference.DEFAULT_ORDER);
                arrayMap.put(preference2.getKey(), preference2);
            }
            preference = findPreference;
            preferenceScreen = preferenceScreen2;
        }
        if (this.mRole.shouldShowNone()) {
            addPreference(PREFERENCE_KEY_NONE, AppCompatResources.getDrawable(context, R.drawable.ic_remove_circle), getString(R.string.default_app_none), !hasHolderApplication(list), null, arrayMap, preferenceScreen, context);
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Pair<ApplicationInfo, Boolean> pair = list.get(i);
            ApplicationInfo applicationInfo = (ApplicationInfo) pair.first;
            addPreference(applicationInfo.packageName, Utils.getBadgedIcon(context, applicationInfo), Utils.getFullAppLabel(applicationInfo, context), ((Boolean) pair.second).booleanValue(), applicationInfo, arrayMap, preferenceScreen, context);
        }
        if (preference == null) {
            preference = requirePreferenceFragment.createFooterPreference(context);
            preference.setKey(PREFERENCE_KEY_DESCRIPTION);
            preference.setSummary(this.mRole.getDescriptionResource());
        }
        preferenceScreen.addPreference(preference);
        requirePreferenceFragment.onPreferenceScreenChanged();
    }

    private static boolean hasHolderApplication(List<Pair<ApplicationInfo, Boolean>> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (((Boolean) list.get(i).second).booleanValue()) {
                return true;
            }
        }
        return false;
    }

    private void addPreference(String str, Drawable drawable, CharSequence charSequence, boolean z, ApplicationInfo applicationInfo, ArrayMap<String, Preference> arrayMap, PreferenceScreen preferenceScreen, Context context) {
        TwoStatePreference twoStatePreference = (TwoStatePreference) arrayMap.get(str);
        if (twoStatePreference == null) {
            twoStatePreference = requirePreferenceFragment().createApplicationPreference(context);
            twoStatePreference.setKey(str);
            twoStatePreference.setIcon(drawable);
            twoStatePreference.setTitle(charSequence);
            twoStatePreference.setPersistent(false);
            twoStatePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$DefaultAppChildFragment$jS2MkrMcVrxJvHgPGvp1Icff1DY
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public final boolean onPreferenceChange(Preference preference, Object obj) {
                    return DefaultAppChildFragment.lambda$addPreference$0(preference, obj);
                }
            });
            twoStatePreference.setOnPreferenceClickListener(this);
        }
        twoStatePreference.setChecked(z);
        if (applicationInfo != null) {
            this.mRole.prepareApplicationPreferenceAsUser(twoStatePreference, applicationInfo, this.mUser, context);
        }
        preferenceScreen.addPreference(twoStatePreference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onManageRoleHolderStateChanged(int i) {
        ManageRoleHolderStateLiveData manageRoleHolderStateLiveData = this.mViewModel.getManageRoleHolderStateLiveData();
        if (i != 2) {
            if (i != 3) {
                return;
            }
            manageRoleHolderStateLiveData.resetState();
            return;
        }
        String lastPackageName = manageRoleHolderStateLiveData.getLastPackageName();
        if (lastPackageName != null) {
            this.mRole.onHolderSelectedAsUser(lastPackageName, manageRoleHolderStateLiveData.getLastUser(), requireContext());
        }
        manageRoleHolderStateLiveData.resetState();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (Objects.equals(key, PREFERENCE_KEY_NONE)) {
            this.mViewModel.setNoneDefaultApp();
            return true;
        }
        CharSequence confirmationMessage = this.mRole.getConfirmationMessage(key, requireContext());
        if (confirmationMessage != null) {
            DefaultAppConfirmationDialogFragment.show(key, confirmationMessage, this);
            return true;
        }
        setDefaultApp(key);
        return true;
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppConfirmationDialogFragment.Listener
    public void setDefaultApp(String str) {
        this.mViewModel.setDefaultApp(str);
    }

    private PF requirePreferenceFragment() {
        return (PF) ((PreferenceFragmentCompat) requireParentFragment());
    }
}
