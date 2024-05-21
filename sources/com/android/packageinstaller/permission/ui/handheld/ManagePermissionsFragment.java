package com.android.packageinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.PermissionGroup;
import com.android.packageinstaller.permission.model.PermissionGroups;
import com.android.packageinstaller.permission.utils.Utils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class ManagePermissionsFragment extends PermissionsFrameFragment implements PermissionGroups.PermissionsGroupsChangeCallback, Preference.OnPreferenceClickListener {
    private Collator mCollator;
    private PermissionGroups mPermissions;

    protected abstract void updatePermissionsUi();

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLoading(true, false);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.mPermissions = new PermissionGroups(getContext(), getActivity().getLoaderManager(), this, false, true);
        this.mCollator = Collator.getInstance(getContext().getResources().getConfiguration().getLocales().get(0));
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (this.mPermissions.getGroup(key) == null) {
            return false;
        }
        Intent putExtra = new Intent("android.intent.action.MANAGE_PERMISSION_APPS").putExtra("android.intent.extra.PERMISSION_NAME", key).putExtra("com.android.packageinstaller.extra.SESSION_ID", getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID", 0L));
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
    public PreferenceScreen updatePermissionsUi(boolean z) {
        Context context = getPreferenceManager().getContext();
        if (context == null || getActivity() == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList(this.mPermissions.getGroups());
        arrayList.sort(new Comparator() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$ManagePermissionsFragment$85GgdI2U-Z_1IJRePAe6fPcf8y0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ManagePermissionsFragment.this.lambda$updatePermissionsUi$0$ManagePermissionsFragment((PermissionGroup) obj, (PermissionGroup) obj2);
            }
        });
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null) {
            preferenceScreen = getPreferenceManager().createPreferenceScreen(context);
            setPreferenceScreen(preferenceScreen);
        } else {
            preferenceScreen.removeAll();
        }
        preferenceScreen.setOrderingAsAdded(true);
        for (int i = 0; i < arrayList.size(); i++) {
            PermissionGroup permissionGroup = (PermissionGroup) arrayList.get(i);
            if (z == permissionGroup.getDeclaringPackage().equals("android")) {
                Preference findPreference = findPreference(permissionGroup.getName());
                if (findPreference == null) {
                    findPreference = new FixedSizeIconPreference(context);
                    findPreference.setOnPreferenceClickListener(this);
                    findPreference.setKey(permissionGroup.getName());
                    findPreference.setIcon(Utils.applyTint(context, permissionGroup.getIcon(), 16843817));
                    findPreference.setTitle(permissionGroup.getLabel());
                    findPreference.setSummary(" ");
                    findPreference.setPersistent(false);
                    preferenceScreen.addPreference(findPreference);
                }
                findPreference.setSummary(getString(R.string.app_permissions_group_summary, Integer.valueOf(permissionGroup.getGranted()), Integer.valueOf(permissionGroup.getTotal())));
            }
        }
        if (preferenceScreen.getPreferenceCount() != 0) {
            setLoading(false, true);
        }
        return preferenceScreen;
    }

    public /* synthetic */ int lambda$updatePermissionsUi$0$ManagePermissionsFragment(PermissionGroup permissionGroup, PermissionGroup permissionGroup2) {
        return this.mCollator.compare(permissionGroup.getLabel(), permissionGroup2.getLabel());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class FixedSizeIconPreference extends Preference {
        FixedSizeIconPreference(Context context) {
            super(context);
        }

        @Override // androidx.preference.Preference
        public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
            super.onBindViewHolder(preferenceViewHolder);
            ImageView imageView = (ImageView) preferenceViewHolder.findViewById(16908294);
            imageView.setAdjustViewBounds(true);
            int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.permission_icon_size);
            imageView.setMaxWidth(dimensionPixelSize);
            imageView.setMaxHeight(dimensionPixelSize);
            imageView.getLayoutParams().width = dimensionPixelSize;
            imageView.getLayoutParams().height = dimensionPixelSize;
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
}
