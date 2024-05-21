package com.android.packageinstaller.permission.ui.television;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.PermissionApps;
import com.android.packageinstaller.permission.ui.ReviewPermissionsActivity;
import com.android.packageinstaller.permission.utils.LocationUtils;
import com.android.packageinstaller.permission.utils.SafetyNetLogger;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.Iterator;
/* loaded from: classes.dex */
public final class PermissionAppsFragment extends SettingsWithHeader implements PermissionApps.Callback, Preference.OnPreferenceChangeListener {
    private PreferenceScreen mExtraScreen;
    private boolean mHasConfirmedRevoke;
    private boolean mHasSystemApps;
    private MenuItem mHideSystemMenu;
    private PermissionApps.Callback mOnPermissionsLoadedListener;
    private PermissionApps mPermissionApps;
    private boolean mShowSystem;
    private MenuItem mShowSystemMenu;
    private ArraySet<AppPermissionGroup> mToggledGroups;

    /*  JADX ERROR: NullPointerException in pass: MarkMethodsForInline
        java.lang.NullPointerException
        	at jadx.core.dex.instructions.args.RegisterArg.sameRegAndSVar(RegisterArg.java:173)
        	at jadx.core.dex.instructions.args.InsnArg.isSameVar(InsnArg.java:269)
        	at jadx.core.dex.visitors.MarkMethodsForInline.isSyntheticAccessPattern(MarkMethodsForInline.java:118)
        	at jadx.core.dex.visitors.MarkMethodsForInline.inlineMth(MarkMethodsForInline.java:86)
        	at jadx.core.dex.visitors.MarkMethodsForInline.process(MarkMethodsForInline.java:53)
        	at jadx.core.dex.visitors.MarkMethodsForInline.visit(MarkMethodsForInline.java:37)
        */
    static /* synthetic */ android.app.Fragment access$000(android.app.Fragment r0, java.lang.String r1) {
        /*
            setPermissionName(r0, r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.access$000(android.app.Fragment, java.lang.String):android.app.Fragment");
    }

    public static PermissionAppsFragment newInstance(String str) {
        PermissionAppsFragment permissionAppsFragment = new PermissionAppsFragment();
        setPermissionName(permissionAppsFragment, str);
        return permissionAppsFragment;
    }

    private static <T extends Fragment> T setPermissionName(T t, String str) {
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PERMISSION_NAME", str);
        t.setArguments(bundle);
        return t;
    }

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setLoading(true, false);
        this.mPermissionApps = new PermissionApps(getActivity(), getArguments().getString("android.intent.extra.PERMISSION_NAME"), this);
    }

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onStart() {
        super.onStart();
        setHasOptionsMenu(true);
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.mPermissionApps.refresh(true);
    }

    @Override // android.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (this.mHasSystemApps) {
            this.mShowSystemMenu = menu.add(0, 1, 0, R.string.menu_show_system);
            this.mHideSystemMenu = menu.add(0, 2, 0, R.string.menu_hide_system);
            updateMenu();
        }
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1 || itemId == 2) {
            this.mShowSystem = menuItem.getItemId() == 1;
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

    @Override // com.android.packageinstaller.permission.ui.television.PermissionsFrameFragment
    protected void onSetEmptyText(TextView textView) {
        textView.setText(R.string.no_apps);
    }

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        bindUi(this, this.mPermissionApps);
    }

    private static void bindUi(SettingsWithHeader settingsWithHeader, PermissionApps permissionApps) {
        permissionApps.getIcon();
        settingsWithHeader.setHeader(null, null, null, settingsWithHeader.getString(R.string.permission_apps_decor_title, permissionApps.getLabel()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOnPermissionsLoadedListener(PermissionApps.Callback callback) {
        this.mOnPermissionsLoadedListener = callback;
    }

    @Override // com.android.packageinstaller.permission.model.PermissionApps.Callback
    public void onPermissionsLoaded(PermissionApps permissionApps) {
        Preference findPreference;
        PreferenceScreen preferenceScreen;
        Context context = getPreferenceManager().getContext();
        if (context == null) {
            return;
        }
        boolean isTelevision = DeviceUtils.isTelevision(context);
        PreferenceScreen preferenceScreen2 = getPreferenceScreen();
        ArraySet arraySet = new ArraySet();
        int preferenceCount = preferenceScreen2.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            arraySet.add(preferenceScreen2.getPreference(i).getKey());
        }
        PreferenceScreen preferenceScreen3 = this.mExtraScreen;
        if (preferenceScreen3 != null) {
            int preferenceCount2 = preferenceScreen3.getPreferenceCount();
            for (int i2 = 0; i2 < preferenceCount2; i2++) {
                arraySet.add(this.mExtraScreen.getPreference(i2).getKey());
            }
        }
        this.mHasSystemApps = false;
        Iterator<PermissionApps.PermissionApp> it = permissionApps.getApps().iterator();
        boolean z = false;
        while (true) {
            boolean z2 = true;
            if (!it.hasNext()) {
                break;
            }
            PermissionApps.PermissionApp next = it.next();
            if (Utils.shouldShowPermission(getContext(), next.getPermissionGroup())) {
                String key = next.getKey();
                arraySet.remove(key);
                Preference findPreference2 = preferenceScreen2.findPreference(key);
                if (findPreference2 == null && (preferenceScreen = this.mExtraScreen) != null) {
                    findPreference2 = preferenceScreen.findPreference(key);
                }
                boolean z3 = !Utils.isGroupOrBgGroupUserSensitive(next.getPermissionGroup());
                if (z3 && !z) {
                    this.mHasSystemApps = true;
                    getActivity().invalidateOptionsMenu();
                    z = true;
                }
                if (!z3 || isTelevision || this.mShowSystem) {
                    if (findPreference2 != null) {
                        if (next.isSystemFixed()) {
                            findPreference2.setSummary(getString(R.string.permission_summary_enabled_system_fixed));
                        } else if (next.isPolicyFixed()) {
                            findPreference2.setSummary(getString(R.string.permission_summary_enforced_by_policy));
                        }
                        findPreference2.setPersistent(false);
                        findPreference2.setEnabled((next.isSystemFixed() || next.isPolicyFixed()) ? false : false);
                        if (findPreference2 instanceof SwitchPreference) {
                            ((SwitchPreference) findPreference2).setChecked(next.areRuntimePermissionsGranted());
                        }
                    } else {
                        SwitchPreference switchPreference = new SwitchPreference(context);
                        switchPreference.setOnPreferenceChangeListener(this);
                        switchPreference.setKey(next.getKey());
                        switchPreference.setIcon(next.getIcon());
                        switchPreference.setTitle(next.getLabel());
                        if (next.isSystemFixed()) {
                            switchPreference.setSummary(getString(R.string.permission_summary_enabled_system_fixed));
                        } else if (next.isPolicyFixed()) {
                            switchPreference.setSummary(getString(R.string.permission_summary_enforced_by_policy));
                        }
                        switchPreference.setPersistent(false);
                        switchPreference.setEnabled((next.isSystemFixed() || next.isPolicyFixed()) ? false : false);
                        switchPreference.setChecked(next.areRuntimePermissionsGranted());
                        if (z3 && isTelevision) {
                            if (this.mExtraScreen == null) {
                                this.mExtraScreen = getPreferenceManager().createPreferenceScreen(context);
                            }
                            this.mExtraScreen.addPreference(switchPreference);
                        } else {
                            preferenceScreen2.addPreference(switchPreference);
                        }
                    }
                } else if (findPreference2 != null) {
                    preferenceScreen2.removePreference(findPreference2);
                }
            }
        }
        if (this.mExtraScreen != null) {
            arraySet.remove("_showSystem");
            Preference findPreference3 = preferenceScreen2.findPreference("_showSystem");
            if (findPreference3 == null) {
                findPreference3 = new Preference(context);
                findPreference3.setKey("_showSystem");
                findPreference3.setIcon(Utils.applyTint(context, (int) R.drawable.ic_toc, 16843817));
                findPreference3.setTitle(R.string.preference_show_system_apps);
                findPreference3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.1
                    /*  JADX ERROR: JadxRuntimeException in pass: InlineMethods
                        jadx.core.utils.exceptions.JadxRuntimeException: Failed to process method for inline: com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.access$000(android.app.Fragment, java.lang.String):android.app.Fragment
                        	at jadx.core.dex.visitors.InlineMethods.processInvokeInsn(InlineMethods.java:76)
                        	at jadx.core.dex.visitors.InlineMethods.visit(InlineMethods.java:51)
                        Caused by: java.lang.NullPointerException
                        	at jadx.core.dex.instructions.args.RegisterArg.sameRegAndSVar(RegisterArg.java:173)
                        	at jadx.core.dex.instructions.args.InsnArg.isSameVar(InsnArg.java:269)
                        	at jadx.core.dex.visitors.MarkMethodsForInline.isSyntheticAccessPattern(MarkMethodsForInline.java:118)
                        	at jadx.core.dex.visitors.MarkMethodsForInline.inlineMth(MarkMethodsForInline.java:86)
                        	at jadx.core.dex.visitors.MarkMethodsForInline.process(MarkMethodsForInline.java:53)
                        	at jadx.core.dex.visitors.InlineMethods.processInvokeInsn(InlineMethods.java:65)
                        	... 1 more
                        */
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(androidx.preference.Preference r3) {
                        /*
                            r2 = this;
                            com.android.packageinstaller.permission.ui.television.PermissionAppsFragment$SystemAppsFragment r3 = new com.android.packageinstaller.permission.ui.television.PermissionAppsFragment$SystemAppsFragment
                            r3.<init>()
                            com.android.packageinstaller.permission.ui.television.PermissionAppsFragment r0 = com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.this
                            android.os.Bundle r0 = r0.getArguments()
                            java.lang.String r1 = "android.intent.extra.PERMISSION_NAME"
                            java.lang.String r0 = r0.getString(r1)
                            com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.access$000(r3, r0)
                            com.android.packageinstaller.permission.ui.television.PermissionAppsFragment r0 = com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.this
                            r1 = 0
                            r3.setTargetFragment(r0, r1)
                            com.android.packageinstaller.permission.ui.television.PermissionAppsFragment r0 = com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.this
                            android.app.FragmentManager r0 = r0.getFragmentManager()
                            android.app.FragmentTransaction r0 = r0.beginTransaction()
                            r1 = 16908290(0x1020002, float:2.3877235E-38)
                            android.app.FragmentTransaction r3 = r0.replace(r1, r3)
                            java.lang.String r0 = "SystemApps"
                            android.app.FragmentTransaction r3 = r3.addToBackStack(r0)
                            r3.commit()
                            r3 = 1
                            return r3
                        */
                        throw new UnsupportedOperationException("Method not decompiled: com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.AnonymousClass1.onPreferenceClick(androidx.preference.Preference):boolean");
                    }
                });
                preferenceScreen2.addPreference(findPreference3);
            }
            int preferenceCount3 = this.mExtraScreen.getPreferenceCount();
            int i3 = 0;
            for (int i4 = 0; i4 < preferenceCount3; i4++) {
                if (((SwitchPreference) this.mExtraScreen.getPreference(i4)).isChecked()) {
                    i3++;
                }
            }
            findPreference3.setSummary(getString(R.string.app_permissions_group_summary, Integer.valueOf(i3), Integer.valueOf(this.mExtraScreen.getPreferenceCount())));
        }
        Iterator it2 = arraySet.iterator();
        while (it2.hasNext()) {
            String str = (String) it2.next();
            Preference findPreference4 = preferenceScreen2.findPreference(str);
            if (findPreference4 != null) {
                preferenceScreen2.removePreference(findPreference4);
            } else {
                PreferenceScreen preferenceScreen4 = this.mExtraScreen;
                if (preferenceScreen4 != null && (findPreference = preferenceScreen4.findPreference(str)) != null) {
                    this.mExtraScreen.removePreference(findPreference);
                }
            }
        }
        setLoading(false, true);
        PermissionApps.Callback callback = this.mOnPermissionsLoadedListener;
        if (callback != null) {
            callback.onPermissionsLoaded(permissionApps);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(final Preference preference, Object obj) {
        final PermissionApps.PermissionApp app = this.mPermissionApps.getApp(preference.getKey());
        if (app == null) {
            return false;
        }
        if (LocationUtils.isLocationGroupAndProvider(getContext(), this.mPermissionApps.getGroupName(), app.getPackageName())) {
            LocationUtils.showLocationDialog(getContext(), app.getLabel());
            return false;
        }
        addToggledGroup(app.getPackageName(), app.getPermissionGroup());
        if (app.isReviewRequired()) {
            Intent intent = new Intent(getActivity(), ReviewPermissionsActivity.class);
            intent.putExtra("android.intent.extra.PACKAGE_NAME", app.getPackageName());
            startActivity(intent);
            return false;
        } else if (obj == Boolean.TRUE) {
            app.grantRuntimePermissions();
            return true;
        } else {
            final boolean hasGrantedByDefaultPermissions = app.hasGrantedByDefaultPermissions();
            if (hasGrantedByDefaultPermissions || (!app.doesSupportRuntimePermissions() && !this.mHasConfirmedRevoke)) {
                new AlertDialog.Builder(getContext()).setMessage(hasGrantedByDefaultPermissions ? R.string.system_warning : R.string.old_sdk_deny_warning).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.television.PermissionAppsFragment.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((SwitchPreference) preference).setChecked(false);
                        app.revokeRuntimePermissions();
                        if (hasGrantedByDefaultPermissions) {
                            return;
                        }
                        PermissionAppsFragment.this.mHasConfirmedRevoke = true;
                    }
                }).show();
                return false;
            }
            app.revokeRuntimePermissions();
            return true;
        }
    }

    @Override // androidx.preference.PreferenceFragment, android.app.Fragment
    public void onStop() {
        super.onStop();
        logToggledGroups();
    }

    private void addToggledGroup(String str, AppPermissionGroup appPermissionGroup) {
        if (this.mToggledGroups == null) {
            this.mToggledGroups = new ArraySet<>();
        }
        this.mToggledGroups.add(appPermissionGroup);
    }

    private void logToggledGroups() {
        ArraySet<AppPermissionGroup> arraySet = this.mToggledGroups;
        if (arraySet != null) {
            SafetyNetLogger.logPermissionsToggled(arraySet);
            this.mToggledGroups = null;
        }
    }

    /* loaded from: classes.dex */
    public static class SystemAppsFragment extends SettingsWithHeader implements PermissionApps.Callback {
        PermissionAppsFragment mOuterFragment;

        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle bundle) {
            this.mOuterFragment = (PermissionAppsFragment) getTargetFragment();
            setLoading(true, false);
            super.onCreate(bundle);
        }

        @Override // com.android.packageinstaller.permission.ui.television.PermissionsFrameFragment, androidx.preference.PreferenceFragment
        public void onCreatePreferences(Bundle bundle, String str) {
            if (this.mOuterFragment.mExtraScreen == null) {
                this.mOuterFragment.setOnPermissionsLoadedListener(this);
            } else {
                setPreferenceScreen();
            }
        }

        @Override // androidx.preference.PreferenceFragment, android.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            bindUi(this, new PermissionApps(getActivity(), getArguments().getString("android.intent.extra.PERMISSION_NAME"), null));
        }

        @Override // android.app.Fragment
        public void onResume() {
            super.onResume();
            this.mOuterFragment.mPermissionApps.refresh(true);
        }

        @Override // android.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            this.mOuterFragment.setOnPermissionsLoadedListener(null);
        }

        private static void bindUi(SettingsWithHeader settingsWithHeader, PermissionApps permissionApps) {
            settingsWithHeader.setHeader(null, null, null, settingsWithHeader.getString(R.string.system_apps_decor_title, permissionApps.getLabel()));
        }

        @Override // com.android.packageinstaller.permission.model.PermissionApps.Callback
        public void onPermissionsLoaded(PermissionApps permissionApps) {
            setPreferenceScreen();
        }

        private void setPreferenceScreen() {
            setPreferenceScreen(this.mOuterFragment.mExtraScreen);
            setLoading(false, true);
        }
    }
}
