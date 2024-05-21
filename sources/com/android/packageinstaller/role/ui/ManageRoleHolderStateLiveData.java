package com.android.packageinstaller.role.ui;

import android.app.role.RoleManager;
import android.content.Context;
import android.os.UserHandle;
import android.util.Log;
import androidx.lifecycle.LiveData;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class ManageRoleHolderStateLiveData extends LiveData<Integer> {
    private static final String LOG_TAG = "ManageRoleHolderStateLiveData";
    private boolean mLastAdd;
    private int mLastFlags;
    private String mLastPackageName;
    private UserHandle mLastUser;

    public ManageRoleHolderStateLiveData() {
        setValue(0);
    }

    public void setRoleHolderAsUser(final String str, final String str2, final boolean z, int i, UserHandle userHandle, Context context) {
        if (getValue().intValue() != 0) {
            String str3 = LOG_TAG;
            Log.e(str3, "Already (tried) managing role holders, requested role: " + str + ", requested package: " + str2);
            return;
        }
        this.mLastPackageName = str2;
        this.mLastAdd = z;
        this.mLastFlags = i;
        this.mLastUser = userHandle;
        setValue(1);
        RoleManager roleManager = (RoleManager) context.getSystemService(RoleManager.class);
        Executor mainExecutor = context.getMainExecutor();
        Consumer consumer = new Consumer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$ManageRoleHolderStateLiveData$IZxYpJJ01sGPe_Ce2DPeU2ll_S4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ManageRoleHolderStateLiveData.this.lambda$setRoleHolderAsUser$0$ManageRoleHolderStateLiveData(z, str, str2, (Boolean) obj);
            }
        };
        if (z) {
            roleManager.addRoleHolderAsUser(str, str2, i, userHandle, mainExecutor, consumer);
        } else {
            roleManager.removeRoleHolderAsUser(str, str2, i, userHandle, mainExecutor, consumer);
        }
    }

    public /* synthetic */ void lambda$setRoleHolderAsUser$0$ManageRoleHolderStateLiveData(boolean z, String str, String str2, Boolean bool) {
        if (bool.booleanValue()) {
            setValue(2);
        } else {
            setValue(3);
        }
    }

    public void clearRoleHoldersAsUser(final String str, int i, UserHandle userHandle, Context context) {
        if (getValue().intValue() != 0) {
            String str2 = LOG_TAG;
            Log.e(str2, "Already (tried) managing role holders, requested role: " + str);
            return;
        }
        this.mLastPackageName = null;
        this.mLastAdd = false;
        this.mLastFlags = i;
        this.mLastUser = userHandle;
        setValue(1);
        ((RoleManager) context.getSystemService(RoleManager.class)).clearRoleHoldersAsUser(str, i, userHandle, context.getMainExecutor(), new Consumer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$ManageRoleHolderStateLiveData$pjokRrL_CkAdUN7xpbHo5OWwEvI
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ManageRoleHolderStateLiveData.this.lambda$clearRoleHoldersAsUser$1$ManageRoleHolderStateLiveData(str, (Boolean) obj);
            }
        });
    }

    public /* synthetic */ void lambda$clearRoleHoldersAsUser$1$ManageRoleHolderStateLiveData(String str, Boolean bool) {
        if (bool.booleanValue()) {
            setValue(2);
        } else {
            setValue(3);
        }
    }

    public String getLastPackageName() {
        return this.mLastPackageName;
    }

    public boolean isLastAdd() {
        return this.mLastAdd;
    }

    public UserHandle getLastUser() {
        return this.mLastUser;
    }

    public void resetState() {
        int intValue = getValue().intValue();
        if (intValue != 2 && intValue != 3) {
            Log.e(LOG_TAG, "Trying to reset state when the current state is not STATE_SUCCESS or STATE_FAILURE");
            return;
        }
        this.mLastPackageName = null;
        this.mLastAdd = false;
        this.mLastFlags = 0;
        this.mLastUser = null;
        setValue(0);
    }
}
