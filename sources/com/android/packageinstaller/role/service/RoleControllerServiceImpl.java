package com.android.packageinstaller.role.service;

import android.app.role.RoleControllerService;
import android.app.role.RoleManager;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Process;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.packageinstaller.permission.utils.CollectionUtils;
import com.android.packageinstaller.permission.utils.Utils;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class RoleControllerServiceImpl extends RoleControllerService {
    private static final String LOG_TAG = "RoleControllerServiceImpl";
    private RoleManager mRoleManager;

    private static boolean hasFlag(int i, int i2) {
        return (i & i2) == i2;
    }

    public void onCreate() {
        super.onCreate();
        this.mRoleManager = (RoleManager) getSystemService(RoleManager.class);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r19v0, types: [android.content.Context, com.android.packageinstaller.role.service.RoleControllerServiceImpl] */
    /* JADX WARN: Type inference failed for: r3v6 */
    /* JADX WARN: Type inference failed for: r3v7, types: [int] */
    /* JADX WARN: Type inference failed for: r4v0 */
    /* JADX WARN: Type inference failed for: r4v1, types: [int] */
    public boolean onGrantDefaultRoles() {
        int i;
        int i2;
        ArrayMap<String, Role> arrayMap = Roles.get(this);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArraySet arraySet = new ArraySet();
        int size = arrayMap.size();
        boolean z = false;
        for (int i3 = 0; i3 < size; i3++) {
            Role valueAt = arrayMap.valueAt(i3);
            if (valueAt.isAvailable(this)) {
                arrayList.add(valueAt);
                String name = valueAt.getName();
                arrayList2.add(name);
                if (!this.mRoleManager.isRoleAvailable(name)) {
                    arraySet.add(name);
                }
            }
        }
        this.mRoleManager.setRoleNamesFromController(arrayList2);
        int size2 = arraySet.size();
        for (int i4 = 0; i4 < size2; i4++) {
            arrayMap.get((String) arraySet.valueAt(i4)).onRoleAdded(this);
        }
        int size3 = arrayList.size();
        int i5 = 0;
        while (true) {
            if (i5 < size3) {
                Role role = (Role) arrayList.get(i5);
                String name2 = role.getName();
                List roleHolders = this.mRoleManager.getRoleHolders(name2);
                int size4 = roleHolders.size();
                int i6 = z;
                while (i6 < size4) {
                    String str = (String) roleHolders.get(i6);
                    if (role.isPackageQualified(str, this)) {
                        i = i6;
                        i2 = size4;
                        addRoleHolderInternal(role, str, false, false, true);
                    } else {
                        i = i6;
                        i2 = size4;
                        Log.i(LOG_TAG, "Removing package that no longer qualifies for the role, package: " + str + ", role: " + name2);
                        removeRoleHolderInternal(role, str, z);
                    }
                    size4 = i2;
                    i6 = i + 1;
                }
                if (this.mRoleManager.getRoleHolders(name2).size() == 0) {
                    List<String> defaultHolders = arraySet.contains(name2) ? role.getDefaultHolders(this) : null;
                    if (defaultHolders == null || defaultHolders.isEmpty()) {
                        defaultHolders = CollectionUtils.singletonOrEmpty(role.getFallbackHolder(this));
                    }
                    int size5 = defaultHolders.size();
                    for (int i7 = z; i7 < size5; i7++) {
                        String str2 = defaultHolders.get(i7);
                        if (!role.isPackageQualified(str2, this)) {
                            Log.e(LOG_TAG, "Default/fallback role holder package doesn't qualify for the role, package: " + str2 + ", role: " + name2);
                        } else {
                            Log.i(LOG_TAG, "Adding package as default/fallback role holder, package: " + str2 + ", role: " + name2);
                            addRoleHolderInternal(role, str2, true);
                        }
                    }
                }
                List roleHolders2 = this.mRoleManager.getRoleHolders(name2);
                int size6 = roleHolders2.size();
                if (role.isExclusive() && size6 > 1) {
                    Log.w(LOG_TAG, "Multiple packages holding an exclusive role, role: " + name2);
                    for (int i8 = 1; i8 < size6; i8++) {
                        String str3 = (String) roleHolders2.get(i8);
                        Log.i(LOG_TAG, "Removing extraneous package for an exclusive role, package: " + str3 + ", role: " + name2);
                        removeRoleHolderInternal(role, str3, false);
                    }
                }
                i5++;
                z = false;
            } else {
                AsyncTask.execute(new Runnable() { // from class: com.android.packageinstaller.role.service.-$$Lambda$RoleControllerServiceImpl$gpVnmV7jtPadkAKKLWe6TalDmDg
                    @Override // java.lang.Runnable
                    public final void run() {
                        RoleControllerServiceImpl.this.lambda$onGrantDefaultRoles$0$RoleControllerServiceImpl();
                    }
                });
                return true;
            }
        }
    }

    public /* synthetic */ void lambda$onGrantDefaultRoles$0$RoleControllerServiceImpl() {
        Utils.updateUserSensitive(getApplication(), Process.myUserHandle());
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean onAddRoleHolder(String str, String str2, int i) {
        boolean z;
        if (checkFlags(i, 1)) {
            Role role = Roles.get(this).get(str);
            if (role == null) {
                Log.e(LOG_TAG, "Unknown role: " + str);
                return false;
            } else if (!role.isAvailable(this)) {
                Log.e(LOG_TAG, "Role is unavailable: " + str);
                return false;
            } else if (!role.isPackageQualified(str2, this)) {
                Log.e(LOG_TAG, "Package does not qualify for the role, package: " + str2 + ", role: " + str);
                return false;
            } else {
                if (role.isExclusive()) {
                    List roleHolders = this.mRoleManager.getRoleHolders(str);
                    int size = roleHolders.size();
                    boolean z2 = false;
                    for (int i2 = 0; i2 < size; i2++) {
                        String str3 = (String) roleHolders.get(i2);
                        if (Objects.equals(str3, str2)) {
                            Log.i(LOG_TAG, "Package is already a role holder, package: " + str2 + ", role: " + str);
                            z2 = true;
                        } else if (!removeRoleHolderInternal(role, str3, false)) {
                            return false;
                        }
                    }
                    z = z2;
                } else {
                    z = false;
                }
                if (addRoleHolderInternal(role, str2, hasFlag(i, 1), true, z)) {
                    role.onHolderAddedAsUser(str2, Process.myUserHandle(), this);
                    role.onHolderChangedAsUser(Process.myUserHandle(), this);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean onRemoveRoleHolder(String str, String str2, int i) {
        if (checkFlags(i, 1)) {
            Role role = Roles.get(this).get(str);
            if (role == null) {
                String str3 = LOG_TAG;
                Log.e(str3, "Unknown role: " + str);
                return false;
            } else if (!role.isAvailable(this)) {
                String str4 = LOG_TAG;
                Log.e(str4, "Role is unavailable: " + str);
                return false;
            } else if (removeRoleHolderInternal(role, str2, hasFlag(i, 1)) && addFallbackRoleHolderMaybe(role)) {
                role.onHolderChangedAsUser(Process.myUserHandle(), this);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean onClearRoleHolders(String str, int i) {
        if (checkFlags(i, 1)) {
            Role role = Roles.get(this).get(str);
            if (role == null) {
                String str2 = LOG_TAG;
                Log.e(str2, "Unknown role: " + str);
                return false;
            } else if (!role.isAvailable(this)) {
                String str3 = LOG_TAG;
                Log.e(str3, "Role is unavailable: " + str);
                return false;
            } else if (clearRoleHoldersInternal(role, hasFlag(i, 1)) && addFallbackRoleHolderMaybe(role)) {
                role.onHolderChangedAsUser(Process.myUserHandle(), this);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean addRoleHolderInternal(Role role, String str, boolean z) {
        return addRoleHolderInternal(role, str, false, z, false);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean addRoleHolderInternal(Role role, String str, boolean z, boolean z2, boolean z3) {
        role.grant(str, z, z2, this);
        String name = role.getName();
        if (!z3) {
            z3 = this.mRoleManager.addRoleHolderFromController(name, str);
        }
        if (!z3) {
            String str2 = LOG_TAG;
            Log.e(str2, "Failed to add role holder in RoleManager, package: " + str + ", role: " + name);
        }
        return z3;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean removeRoleHolderInternal(Role role, String str, boolean z) {
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfo(str, this);
        if (applicationInfo == null) {
            String str2 = LOG_TAG;
            Log.w(str2, "Cannot get ApplicationInfo for package: " + str);
        }
        if (applicationInfo != null) {
            role.revoke(str, z, false, this);
        }
        String name = role.getName();
        boolean removeRoleHolderFromController = this.mRoleManager.removeRoleHolderFromController(name, str);
        if (!removeRoleHolderFromController) {
            String str3 = LOG_TAG;
            Log.e(str3, "Failed to remove role holder in RoleManager, package: " + str + ", role: " + name);
        }
        return removeRoleHolderFromController;
    }

    private boolean clearRoleHoldersInternal(Role role, boolean z) {
        String name = role.getName();
        List roleHolders = this.mRoleManager.getRoleHolders(name);
        int size = roleHolders.size();
        boolean z2 = true;
        for (int i = 0; i < size; i++) {
            if (!removeRoleHolderInternal(role, (String) roleHolders.get(i), z)) {
                z2 = false;
            }
        }
        if (!z2) {
            Log.e(LOG_TAG, "Failed to clear role holders, role: " + name);
        }
        return z2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean addFallbackRoleHolderMaybe(Role role) {
        String fallbackHolder;
        String name = role.getName();
        if (this.mRoleManager.getRoleHolders(name).isEmpty() && (fallbackHolder = role.getFallbackHolder(this)) != null) {
            if (!role.isPackageQualified(fallbackHolder, this)) {
                String str = LOG_TAG;
                Log.e(str, "Fallback role holder package doesn't qualify for the role, package: " + fallbackHolder + ", role: " + name);
                return false;
            }
            String str2 = LOG_TAG;
            Log.i(str2, "Adding package as fallback role holder, package: " + fallbackHolder + ", role: " + name);
            return addRoleHolderInternal(role, fallbackHolder, true);
        }
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean onIsApplicationQualifiedForRole(String str, String str2) {
        Role role = Roles.get(this).get(str);
        if (role != null && role.isAvailable(this)) {
            return role.isPackageQualified(str2, this);
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public boolean onIsRoleVisible(String str) {
        Role role = Roles.get(this).get(str);
        if (role != null && role.isAvailable(this)) {
            return role.isVisibleAsUser(Process.myUserHandle(), this);
        }
        return false;
    }

    private static boolean checkFlags(int i, int i2) {
        if ((i & i2) != i) {
            String str = LOG_TAG;
            Log.e(str, "flags is invalid, flags: 0x" + Integer.toHexString(i) + ", allowed flags: 0x" + Integer.toHexString(i2));
            return false;
        }
        return true;
    }
}
