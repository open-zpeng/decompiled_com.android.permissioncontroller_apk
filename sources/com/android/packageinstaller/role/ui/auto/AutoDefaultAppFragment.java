package com.android.packageinstaller.role.ui.auto;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.role.ui.DefaultAppChildFragment;
/* loaded from: classes.dex */
public class AutoDefaultAppFragment extends AutoSettingsFrameFragment implements DefaultAppChildFragment.Parent {
    private String mRoleName;
    private UserHandle mUser;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppChildFragment.Parent
    public void onPreferenceScreenChanged() {
    }

    public static AutoDefaultAppFragment newInstance(String str, UserHandle userHandle) {
        AutoDefaultAppFragment autoDefaultAppFragment = new AutoDefaultAppFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.ROLE_NAME", str);
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        autoDefaultAppFragment.setArguments(bundle);
        return autoDefaultAppFragment;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mRoleName = arguments.getString("android.intent.extra.ROLE_NAME");
        this.mUser = (UserHandle) arguments.getParcelable("android.intent.extra.USER");
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle == null) {
            DefaultAppChildFragment newInstance = DefaultAppChildFragment.newInstance(this.mRoleName, this.mUser);
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.add(newInstance, (String) null);
            beginTransaction.commit();
        }
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppChildFragment.Parent
    public void setTitle(CharSequence charSequence) {
        setHeaderLabel(charSequence);
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppChildFragment.Parent
    public TwoStatePreference createApplicationPreference(Context context) {
        return new AutoDefaultAppPreference(context);
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppChildFragment.Parent
    public Preference createFooterPreference(Context context) {
        Preference preference = new Preference(context);
        preference.setIcon(R.drawable.ic_info_outline);
        preference.setSelectable(false);
        return preference;
    }
}
