package com.android.packageinstaller.role.ui.auto;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.TwoStatePreference;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.role.ui.SpecialAppAccessChildFragment;
/* loaded from: classes.dex */
public class AutoSpecialAppAccessFragment extends AutoSettingsFrameFragment implements SpecialAppAccessChildFragment.Parent {
    private String mRoleName;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessChildFragment.Parent
    public void onPreferenceScreenChanged() {
    }

    public static AutoSpecialAppAccessFragment newInstance(String str) {
        AutoSpecialAppAccessFragment autoSpecialAppAccessFragment = new AutoSpecialAppAccessFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.ROLE_NAME", str);
        autoSpecialAppAccessFragment.setArguments(bundle);
        return autoSpecialAppAccessFragment;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mRoleName = getArguments().getString("android.intent.extra.ROLE_NAME");
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle == null) {
            SpecialAppAccessChildFragment newInstance = SpecialAppAccessChildFragment.newInstance(this.mRoleName);
            FragmentTransaction beginTransaction = getChildFragmentManager().beginTransaction();
            beginTransaction.add(newInstance, (String) null);
            beginTransaction.commit();
        }
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessChildFragment.Parent
    public void setTitle(CharSequence charSequence) {
        setHeaderLabel(charSequence);
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessChildFragment.Parent
    public TwoStatePreference createApplicationPreference(Context context) {
        return new SwitchPreference(context);
    }

    @Override // com.android.packageinstaller.role.ui.SpecialAppAccessChildFragment.Parent
    public Preference createFooterPreference(Context context) {
        Preference preference = new Preference(context);
        preference.setIcon(R.drawable.ic_info_outline);
        preference.setSelectable(false);
        return preference;
    }
}
