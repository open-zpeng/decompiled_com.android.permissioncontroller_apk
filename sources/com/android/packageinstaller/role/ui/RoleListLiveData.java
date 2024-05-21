package com.android.packageinstaller.role.ui;

import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import com.android.packageinstaller.AsyncTaskLiveData;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
import com.android.packageinstaller.role.utils.PackageUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class RoleListLiveData extends AsyncTaskLiveData<List<RoleItem>> implements OnRoleHoldersChangedListener {
    private static final String LOG_TAG = "RoleListLiveData";
    private final Context mContext;
    private final boolean mExclusive;
    private final UserHandle mUser;

    public RoleListLiveData(boolean z, UserHandle userHandle, Context context) {
        this.mExclusive = z;
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
    public List<RoleItem> loadValueInBackground() {
        ArrayMap<String, Role> arrayMap = Roles.get(this.mContext);
        ArrayList arrayList = new ArrayList();
        RoleManager roleManager = (RoleManager) this.mContext.getSystemService(RoleManager.class);
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            Role valueAt = arrayMap.valueAt(i);
            if (valueAt.isExclusive() == this.mExclusive && valueAt.isAvailableAsUser(this.mUser, this.mContext) && valueAt.isVisibleAsUser(this.mUser, this.mContext) && (!this.mExclusive || !valueAt.getQualifyingPackagesAsUser(this.mUser, this.mContext).isEmpty())) {
                ArrayList arrayList2 = new ArrayList();
                List roleHoldersAsUser = roleManager.getRoleHoldersAsUser(valueAt.getName(), this.mUser);
                int size2 = roleHoldersAsUser.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    String str = (String) roleHoldersAsUser.get(i2);
                    ApplicationInfo applicationInfoAsUser = PackageUtils.getApplicationInfoAsUser(str, this.mUser, this.mContext);
                    if (applicationInfoAsUser == null) {
                        Log.w(LOG_TAG, "Cannot get ApplicationInfo for application, package name: " + str + ", user id: " + this.mUser.getIdentifier());
                    } else {
                        arrayList2.add(applicationInfoAsUser);
                    }
                }
                arrayList.add(new RoleItem(valueAt, arrayList2));
            }
        }
        return arrayList;
    }
}
