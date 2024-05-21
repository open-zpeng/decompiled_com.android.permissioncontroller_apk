package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import com.android.car.ui.R;
import com.android.packageinstaller.role.ui.DefaultAppChildFragment;
/* loaded from: classes.dex */
public class HandheldDefaultAppFragment extends SettingsFragment implements DefaultAppChildFragment.Parent {
    private String mRoleName;
    private UserHandle mUser;

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment
    protected int getEmptyTextResource() {
        return R.string.default_app_no_apps;
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.preference.PreferenceFragmentCompat
    public /* bridge */ /* synthetic */ void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ boolean onOptionsItemSelected(MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public /* bridge */ /* synthetic */ void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public static HandheldDefaultAppFragment newInstance(String str, UserHandle userHandle) {
        HandheldDefaultAppFragment handheldDefaultAppFragment = new HandheldDefaultAppFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.ROLE_NAME", str);
        bundle.putParcelable("android.intent.extra.USER", userHandle);
        handheldDefaultAppFragment.setArguments(bundle);
        return handheldDefaultAppFragment;
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mRoleName = arguments.getString("android.intent.extra.ROLE_NAME");
        this.mUser = (UserHandle) arguments.getParcelable("android.intent.extra.USER");
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsFragment, androidx.fragment.app.Fragment
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
        requireActivity().setTitle(charSequence);
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppChildFragment.Parent
    public TwoStatePreference createApplicationPreference(Context context) {
        return new AppIconRadioButtonPreference(context);
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppChildFragment.Parent
    public Preference createFooterPreference(Context context) {
        return new FooterPreference(context);
    }

    @Override // com.android.packageinstaller.role.ui.DefaultAppChildFragment.Parent
    public void onPreferenceScreenChanged() {
        updateState();
    }
}
