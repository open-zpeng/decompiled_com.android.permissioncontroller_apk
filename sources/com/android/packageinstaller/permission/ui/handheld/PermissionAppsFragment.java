package com.android.packageinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.PermissionControllerStatsLog;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.PermissionApps;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.settingslib.HelpUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
/* loaded from: classes.dex */
public final class PermissionAppsFragment extends SettingsWithLargeHeader implements PermissionApps.Callback {
    private Collator mCollator;
    private boolean mCreationLogged;
    private PreferenceScreen mExtraScreen;
    private boolean mHasSystemApps;
    private MenuItem mHideSystemMenu;
    private PermissionApps.Callback mOnPermissionsLoadedListener;
    private PermissionApps mPermissionApps;
    private boolean mShowSystem;
    private MenuItem mShowSystemMenu;
    private static final String SHOW_SYSTEM_KEY = PermissionAppsFragment.class.getName() + "_showSystem";
    private static final String CREATION_LOGGED = PermissionAppsFragment.class.getName() + "_creationLogged";

    public static PermissionAppsFragment newInstance(String str, long j) {
        PermissionAppsFragment permissionAppsFragment = new PermissionAppsFragment();
        setPermissionNameAndSessionId(permissionAppsFragment, str, j);
        return permissionAppsFragment;
    }

    private static <T extends Fragment> T setPermissionNameAndSessionId(T t, String str, long j) {
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PERMISSION_NAME", str);
        bundle.putLong("com.android.packageinstaller.extra.SESSION_ID", j);
        t.setArguments(bundle);
        return t;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mShowSystem = bundle.getBoolean(SHOW_SYSTEM_KEY);
            this.mCreationLogged = bundle.getBoolean(CREATION_LOGGED);
        }
        setLoading(true, false);
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.mPermissionApps = new PermissionApps(getActivity(), getArguments().getString("android.intent.extra.PERMISSION_NAME"), this);
        this.mPermissionApps.refresh(true);
        this.mCollator = Collator.getInstance(getContext().getResources().getConfiguration().getLocales().get(0));
        addPreferencesFromResource(R.xml.allowed_denied);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(SHOW_SYSTEM_KEY, this.mShowSystem);
        bundle.putBoolean(CREATION_LOGGED, this.mCreationLogged);
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mPermissionApps.refresh(true);
    }

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        if (this.mHasSystemApps) {
            this.mShowSystemMenu = menu.add(0, 3, 0, R.string.menu_show_system);
            this.mHideSystemMenu = menu.add(0, 4, 0, R.string.menu_hide_system);
            updateMenu();
        }
        HelpUtils.prepareHelpMenuItem(getActivity(), menu, (int) R.string.help_app_permissions, PermissionAppsFragment.class.getName());
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 3 || itemId == 4) {
            this.mShowSystem = menuItem.getItemId() == 3;
            if (this.mPermissionApps.getApps() != null) {
                onPermissionsLoaded(this.mPermissionApps);
            }
            updateMenu();
        } else if (itemId == 16908332) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void updateMenu() {
        this.mShowSystemMenu.setVisible(!this.mShowSystem);
        this.mHideSystemMenu.setVisible(this.mShowSystem);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        bindUi(this, this.mPermissionApps, getArguments().getString("android.intent.extra.PERMISSION_NAME"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void bindUi(SettingsWithLargeHeader settingsWithLargeHeader, PermissionApps permissionApps, String str) {
        Drawable icon = permissionApps.getIcon();
        CharSequence fullLabel = permissionApps.getFullLabel();
        settingsWithLargeHeader.setHeader(icon, fullLabel, null, null, true);
        settingsWithLargeHeader.setSummary(Utils.getPermissionGroupDescriptionString(settingsWithLargeHeader.getActivity(), str, permissionApps.getDescription()), null);
        ActionBar actionBar = settingsWithLargeHeader.getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(fullLabel);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOnPermissionsLoadedListener(PermissionApps.Callback callback) {
        this.mOnPermissionsLoadedListener = callback;
    }

    @Override // com.android.packageinstaller.permission.model.PermissionApps.Callback
    public void onPermissionsLoaded(PermissionApps permissionApps) {
        boolean z;
        long j;
        int i;
        String str;
        ArrayMap arrayMap;
        ArrayList arrayList;
        boolean z2;
        PreferenceCategory preferenceCategory;
        boolean z3;
        Context context = getPreferenceManager().getContext();
        if (context == null || getActivity() == null) {
            return;
        }
        boolean isTelevision = DeviceUtils.isTelevision(context);
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("allowed");
        String str2 = "allowed_foreground";
        PreferenceCategory preferenceCategory3 = (PreferenceCategory) findPreference("allowed_foreground");
        PreferenceCategory preferenceCategory4 = (PreferenceCategory) findPreference("denied");
        boolean z4 = true;
        preferenceCategory2.setOrderingAsAdded(true);
        preferenceCategory3.setOrderingAsAdded(true);
        preferenceCategory4.setOrderingAsAdded(true);
        ArrayMap arrayMap2 = new ArrayMap();
        int preferenceCount = preferenceCategory2.getPreferenceCount();
        for (int i2 = 0; i2 < preferenceCount; i2++) {
            Preference preference = preferenceCategory2.getPreference(i2);
            arrayMap2.put(preference.getKey(), preference);
        }
        preferenceCategory2.removeAll();
        int preferenceCount2 = preferenceCategory3.getPreferenceCount();
        for (int i3 = 0; i3 < preferenceCount2; i3++) {
            Preference preference2 = preferenceCategory3.getPreference(i3);
            arrayMap2.put(preference2.getKey(), preference2);
        }
        preferenceCategory3.removeAll();
        int preferenceCount3 = preferenceCategory4.getPreferenceCount();
        for (int i4 = 0; i4 < preferenceCount3; i4++) {
            Preference preference3 = preferenceCategory4.getPreference(i4);
            arrayMap2.put(preference3.getKey(), preference3);
        }
        preferenceCategory4.removeAll();
        PreferenceScreen preferenceScreen = this.mExtraScreen;
        if (preferenceScreen != null) {
            int preferenceCount4 = preferenceScreen.getPreferenceCount();
            for (int i5 = 0; i5 < preferenceCount4; i5++) {
                Preference preference4 = this.mExtraScreen.getPreference(i5);
                arrayMap2.put(preference4.getKey(), preference4);
            }
            this.mExtraScreen.removeAll();
        }
        this.mHasSystemApps = false;
        ArrayList arrayList2 = new ArrayList(permissionApps.getApps());
        arrayList2.sort(new Comparator() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionAppsFragment$QLPDISZS1B_tVnzxvtuXpW906E0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return PermissionAppsFragment.this.lambda$onPermissionsLoaded$0$PermissionAppsFragment((PermissionApps.PermissionApp) obj, (PermissionApps.PermissionApp) obj2);
            }
        });
        long nextLong = new Random().nextLong();
        long j2 = getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID", 0L);
        boolean z5 = false;
        boolean z6 = false;
        int i6 = 0;
        while (i6 < arrayList2.size()) {
            PermissionApps.PermissionApp permissionApp = (PermissionApps.PermissionApp) arrayList2.get(i6);
            AppPermissionGroup permissionGroup = permissionApp.getPermissionGroup();
            boolean z7 = (z5 || permissionGroup.hasPermissionWithBackgroundMode()) ? z4 : false;
            if (Utils.shouldShowPermission(getContext(), permissionGroup) && permissionApp.getAppInfo().enabled) {
                String key = permissionApp.getKey();
                Preference preference5 = (Preference) arrayMap2.get(key);
                ArrayList arrayList3 = arrayList2;
                if (preference5 != null) {
                    preference5.setOrder(Preference.DEFAULT_ORDER);
                }
                boolean z8 = !Utils.isGroupOrBgGroupUserSensitive(permissionGroup);
                if (!z8 || z6) {
                    z2 = z6;
                } else {
                    this.mHasSystemApps = true;
                    getActivity().invalidateOptionsMenu();
                    z2 = true;
                }
                if (!z8 || isTelevision || this.mShowSystem) {
                    if (permissionGroup.areRuntimePermissionsGranted()) {
                        preferenceCategory = (!permissionGroup.hasPermissionWithBackgroundMode() || (permissionGroup.getBackgroundPermissions() != null && permissionGroup.getBackgroundPermissions().areRuntimePermissionsGranted())) ? preferenceCategory2 : preferenceCategory3;
                    } else {
                        preferenceCategory = preferenceCategory4;
                    }
                    if (preference5 != null) {
                        preferenceCategory.addPreference(preference5);
                    } else {
                        arrayMap = arrayMap2;
                        str = str2;
                        PreferenceCategory preferenceCategory5 = preferenceCategory;
                        j = j2;
                        i = i6;
                        PermissionControlPreference permissionControlPreference = new PermissionControlPreference(context, permissionGroup, PermissionAppsFragment.class.getName(), j);
                        permissionControlPreference.setKey(key);
                        permissionControlPreference.setIcon(permissionApp.getIcon());
                        permissionControlPreference.setTitle(Utils.getFullAppLabel(permissionApp.getAppInfo(), context));
                        permissionControlPreference.setEllipsizeEnd();
                        permissionControlPreference.useSmallerIcon();
                        if (z8 && isTelevision) {
                            if (this.mExtraScreen == null) {
                                this.mExtraScreen = getPreferenceManager().createPreferenceScreen(context);
                            }
                            this.mExtraScreen.addPreference(permissionControlPreference);
                        } else {
                            preferenceCategory5.addPreference(permissionControlPreference);
                            if (!this.mCreationLogged) {
                                boolean z9 = preferenceCategory5 == preferenceCategory2;
                                boolean z10 = preferenceCategory5 == preferenceCategory3;
                                if (preferenceCategory5 == preferenceCategory4) {
                                    arrayList = arrayList3;
                                    z3 = true;
                                } else {
                                    arrayList = arrayList3;
                                    z3 = false;
                                }
                                logPermissionAppsFragmentCreated(permissionApp, nextLong, z9, z10, z3);
                                z6 = z2;
                            }
                        }
                        arrayList = arrayList3;
                        z6 = z2;
                    }
                }
                j = j2;
                i = i6;
                str = str2;
                arrayMap = arrayMap2;
                arrayList = arrayList3;
                z6 = z2;
            } else {
                j = j2;
                i = i6;
                str = str2;
                arrayMap = arrayMap2;
                arrayList = arrayList2;
            }
            i6 = i + 1;
            arrayList2 = arrayList;
            z5 = z7;
            str2 = str;
            arrayMap2 = arrayMap;
            j2 = j;
            z4 = true;
        }
        final long j3 = j2;
        String str3 = str2;
        this.mCreationLogged = z4;
        if (this.mExtraScreen != null) {
            Preference findPreference = preferenceCategory2.findPreference("_showSystem");
            int preferenceCount5 = this.mExtraScreen.getPreferenceCount();
            int i7 = 0;
            for (int i8 = 0; i8 < preferenceCount5; i8++) {
                if (((SwitchPreferenceCompat) this.mExtraScreen.getPreference(i8)).isChecked()) {
                    i7++;
                }
            }
            if (findPreference == null) {
                findPreference = new Preference(context);
                findPreference.setKey("_showSystem");
                findPreference.setIcon(Utils.applyTint(context, (int) R.drawable.ic_toc, 16843817));
                findPreference.setTitle(R.string.preference_show_system_apps);
                findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionAppsFragment$IGyAJvgoyG2Gs3Yas0FwQSb2qzo
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference6) {
                        return PermissionAppsFragment.this.lambda$onPermissionsLoaded$1$PermissionAppsFragment(j3, preference6);
                    }
                });
                (i7 > 0 ? preferenceCategory2 : preferenceCategory4).addPreference(findPreference);
            }
            Integer valueOf = Integer.valueOf(i7);
            z = false;
            findPreference.setSummary(getString(R.string.app_permissions_group_summary, valueOf, Integer.valueOf(this.mExtraScreen.getPreferenceCount())));
        } else {
            z = false;
        }
        if (z5) {
            preferenceCategory2.setTitle(R.string.allowed_always_header);
        }
        if (preferenceCategory2.getPreferenceCount() == 0) {
            Preference preference6 = new Preference(context);
            preference6.setTitle(getString(R.string.no_apps_allowed));
            preference6.setSelectable(z);
            preferenceCategory2.addPreference(preference6);
        }
        if (preferenceCategory3.getPreferenceCount() == 0) {
            findPreference(str3).setVisible(z);
        } else {
            findPreference(str3).setVisible(true);
        }
        if (preferenceCategory4.getPreferenceCount() == 0) {
            Preference preference7 = new Preference(context);
            preference7.setTitle(getString(R.string.no_apps_denied));
            preference7.setSelectable(z);
            preferenceCategory4.addPreference(preference7);
        }
        setLoading(z, true);
        PermissionApps.Callback callback = this.mOnPermissionsLoadedListener;
        if (callback != null) {
            callback.onPermissionsLoaded(permissionApps);
        }
    }

    public /* synthetic */ int lambda$onPermissionsLoaded$0$PermissionAppsFragment(PermissionApps.PermissionApp permissionApp, PermissionApps.PermissionApp permissionApp2) {
        int compare = this.mCollator.compare(permissionApp.getLabel(), permissionApp2.getLabel());
        return compare == 0 ? permissionApp.getUid() - permissionApp2.getUid() : compare;
    }

    public /* synthetic */ boolean lambda$onPermissionsLoaded$1$PermissionAppsFragment(long j, Preference preference) {
        SystemAppsFragment systemAppsFragment = new SystemAppsFragment();
        setPermissionNameAndSessionId(systemAppsFragment, getArguments().getString("android.intent.extra.PERMISSION_NAME"), j);
        systemAppsFragment.setTargetFragment(this, 0);
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        beginTransaction.replace(16908290, systemAppsFragment);
        beginTransaction.addToBackStack("SystemApps");
        beginTransaction.commit();
        return true;
    }

    private void logPermissionAppsFragmentCreated(PermissionApps.PermissionApp permissionApp, long j, boolean z, boolean z2, boolean z3) {
        long j2 = getArguments().getLong("com.android.packageinstaller.extra.SESSION_ID", 0L);
        int i = z ? 1 : z2 ? 2 : z3 ? 3 : 0;
        PermissionControllerStatsLog.write(218, j2, j, this.mPermissionApps.getGroupName(), permissionApp.getUid(), permissionApp.getPackageName(), i);
        Log.v("PermissionAppsFragment", "PermissionAppsFragment created with sessionId=" + j2 + " permissionGroupName=" + this.mPermissionApps.getGroupName() + " appUid=" + permissionApp.getUid() + " packageName=" + permissionApp.getPackageName() + " category=" + i);
    }

    /* loaded from: classes.dex */
    public static class SystemAppsFragment extends SettingsWithLargeHeader implements PermissionApps.Callback {
        PermissionAppsFragment mOuterFragment;

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            this.mOuterFragment = (PermissionAppsFragment) getTargetFragment();
            setLoading(true, false);
            super.onCreate(bundle);
            PermissionAppsFragment permissionAppsFragment = this.mOuterFragment;
            setHeader(permissionAppsFragment.mIcon, permissionAppsFragment.mLabel, null, null, true);
            if (this.mOuterFragment.mExtraScreen == null) {
                this.mOuterFragment.setOnPermissionsLoadedListener(this);
            } else {
                setPreferenceScreen();
            }
        }

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            String string = getArguments().getString("android.intent.extra.PERMISSION_NAME");
            PermissionAppsFragment.bindUi(this, new PermissionApps(getActivity(), string, null), string);
        }

        @Override // com.android.packageinstaller.permission.model.PermissionApps.Callback
        public void onPermissionsLoaded(PermissionApps permissionApps) {
            setPreferenceScreen();
            this.mOuterFragment.setOnPermissionsLoadedListener(null);
        }

        private void setPreferenceScreen() {
            setPreferenceScreen(this.mOuterFragment.mExtraScreen);
            setLoading(false, true);
        }
    }
}
