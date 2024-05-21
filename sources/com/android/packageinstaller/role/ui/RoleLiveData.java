package com.android.packageinstaller.role.ui;

import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.UserHandle;
import android.util.Log;
import android.util.Pair;
import com.android.packageinstaller.AsyncTaskLiveData;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class RoleLiveData extends AsyncTaskLiveData<List<Pair<ApplicationInfo, Boolean>>> implements OnRoleHoldersChangedListener {
    private static final String LOG_TAG = "RoleLiveData";
    private final Context mContext;
    private final Role mRole;
    private final UserHandle mUser;

    public RoleLiveData(Role role, UserHandle userHandle, Context context) {
        this.mRole = role;
        this.mUser = userHandle;
        this.mContext = context;
    }

    @Override // androidx.lifecycle.LiveData
    protected void onActive() {
        loadValue();
        ((RoleManager) this.mContext.getSystemService(RoleManager.class)).addOnRoleHoldersChangedListenerAsUser(this.mContext.getMainExecutor(), this, this.mUser);
    }

    @Override // androidx.lifecycle.LiveData
    protected void onInactive() {
        ((RoleManager) this.mContext.getSystemService(RoleManager.class)).removeOnRoleHoldersChangedListenerAsUser(this, this.mUser);
    }

    public void onRoleHoldersChanged(String str, UserHandle userHandle) {
        loadValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.packageinstaller.AsyncTaskLiveData
    public List<Pair<ApplicationInfo, Boolean>> loadValueInBackground() {
        List roleHoldersAsUser = ((RoleManager) this.mContext.getSystemService(RoleManager.class)).getRoleHoldersAsUser(this.mRole.getName(), this.mUser);
        List<String> qualifyingPackagesAsUser = this.mRole.getQualifyingPackagesAsUser(this.mUser, this.mContext);
        ArrayList arrayList = new ArrayList();
        int size = qualifyingPackagesAsUser.size();
        for (int i = 0; i < size; i++) {
            String str = qualifyingPackagesAsUser.get(i);
            ApplicationInfo applicationInfoAsUser = PackageUtils.getApplicationInfoAsUser(str, this.mUser, this.mContext);
            if (applicationInfoAsUser == null) {
                String str2 = LOG_TAG;
                Log.w(str2, "Cannot get ApplicationInfo for application, skipping: " + str);
            } else if (this.mRole.isApplicationVisibleAsUser(applicationInfoAsUser, this.mUser, this.mContext)) {
                arrayList.add(new Pair(applicationInfoAsUser, Boolean.valueOf(roleHoldersAsUser.contains(str))));
            }
        }
        return arrayList;
    }
}
