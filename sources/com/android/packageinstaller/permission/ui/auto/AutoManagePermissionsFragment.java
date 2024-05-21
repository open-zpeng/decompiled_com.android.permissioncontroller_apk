package com.android.packageinstaller.permission.ui.auto;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import com.android.car.ui.R;
import com.android.packageinstaller.auto.AutoSettingsFrameFragment;
import com.android.packageinstaller.permission.model.PermissionGroup;
import com.android.packageinstaller.permission.model.PermissionGroups;
import com.android.packageinstaller.permission.utils.Utils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class AutoManagePermissionsFragment extends AutoSettingsFrameFragment implements PermissionGroups.PermissionsGroupsChangeCallback, Preference.OnPreferenceClickListener {
    private Collator mCollator;
    private PermissionGroups mPermissions;

    protected abstract int getScreenHeaderRes();

    protected abstract void updatePermissionsUi();

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLoading(true);
        this.mPermissions = new PermissionGroups(getContext(), requireActivity().getLoaderManager(), this, false, true);
        this.mCollator = Collator.getInstance(getContext().getResources().getConfiguration().getLocales().get(0));
        setHeaderLabel(getString(getScreenHeaderRes()));
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (this.mPermissions.getGroup(key) == null) {
            return false;
        }
        Intent putExtra = new Intent("android.intent.action.MANAGE_PERMISSION_APPS").putExtra("android.intent.extra.PERMISSION_NAME", key);
        try {
            getActivity().startActivity(putExtra);
            return true;
        } catch (ActivityNotFoundException unused) {
            Log.w("ManagePermissionsFragment", "No app to handle " + putExtra);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PermissionGroups getPermissions() {
        return this.mPermissions;
    }

    public void onPermissionGroupsChanged() {
        updatePermissionsUi();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updatePermissionsUi(boolean z) {
        Context context = getPreferenceManager().getContext();
        if (context == null || getActivity() == null) {
            return;
        }
        ArrayList arrayList = new ArrayList(this.mPermissions.getGroups());
        arrayList.sort(new Comparator() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoManagePermissionsFragment$xJVfioi5gYak_cgRtnkHsmSpYnw
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AutoManagePermissionsFragment.this.lambda$updatePermissionsUi$0$AutoManagePermissionsFragment((PermissionGroup) obj, (PermissionGroup) obj2);
            }
        });
        getPreferenceScreen().removeAll();
        getPreferenceScreen().setOrderingAsAdded(true);
        for (int i = 0; i < arrayList.size(); i++) {
            PermissionGroup permissionGroup = (PermissionGroup) arrayList.get(i);
            if (z == permissionGroup.getDeclaringPackage().equals("android")) {
                Preference findPreference = findPreference(permissionGroup.getName());
                if (findPreference == null) {
                    findPreference = new Preference(context);
                    findPreference.setOnPreferenceClickListener(this);
                    findPreference.setKey(permissionGroup.getName());
                    findPreference.setIcon(Utils.applyTint(context, permissionGroup.getIcon(), 16843817));
                    findPreference.setTitle(permissionGroup.getLabel());
                    findPreference.setSummary(" ");
                    findPreference.setPersistent(false);
                    getPreferenceScreen().addPreference(findPreference);
                }
                findPreference.setSummary(getString(R.string.app_permissions_group_summary, Integer.valueOf(permissionGroup.getGranted()), Integer.valueOf(permissionGroup.getTotal())));
            }
        }
        if (getPreferenceScreen().getPreferenceCount() != 0) {
            setLoading(false);
        }
    }

    public /* synthetic */ int lambda$updatePermissionsUi$0$AutoManagePermissionsFragment(PermissionGroup permissionGroup, PermissionGroup permissionGroup2) {
        return this.mCollator.compare(permissionGroup.getLabel(), permissionGroup2.getLabel());
    }
}
