package com.android.packageinstaller.role.ui;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.UserHandle;
import android.util.Log;
import android.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.android.packageinstaller.role.model.Role;
import java.util.List;
/* loaded from: classes.dex */
public class DefaultAppViewModel extends AndroidViewModel {
    private static final String LOG_TAG = "DefaultAppViewModel";
    private final ManageRoleHolderStateLiveData mManageRoleHolderStateLiveData;
    private final Role mRole;
    private final LiveData<List<Pair<ApplicationInfo, Boolean>>> mRoleLiveData;
    private final UserHandle mUser;

    public DefaultAppViewModel(Role role, UserHandle userHandle, Application application) {
        super(application);
        this.mManageRoleHolderStateLiveData = new ManageRoleHolderStateLiveData();
        this.mRole = role;
        this.mUser = userHandle;
        this.mRoleLiveData = Transformations.map(new RoleLiveData(this.mRole, this.mUser, application), new RoleSortFunction(application));
    }

    public LiveData<List<Pair<ApplicationInfo, Boolean>>> getRoleLiveData() {
        return this.mRoleLiveData;
    }

    public ManageRoleHolderStateLiveData getManageRoleHolderStateLiveData() {
        return this.mManageRoleHolderStateLiveData;
    }

    public void setDefaultApp(String str) {
        if (this.mManageRoleHolderStateLiveData.getValue().intValue() != 0) {
            Log.i(LOG_TAG, "Trying to set default app while another request is on-going");
        } else {
            this.mManageRoleHolderStateLiveData.setRoleHolderAsUser(this.mRole.getName(), str, true, 0, this.mUser, getApplication());
        }
    }

    public void setNoneDefaultApp() {
        Application application = getApplication();
        this.mRole.onNoneHolderSelectedAsUser(this.mUser, application);
        if (this.mManageRoleHolderStateLiveData.getValue().intValue() != 0) {
            Log.i(LOG_TAG, "Trying to set default app while another request is on-going");
        } else {
            this.mManageRoleHolderStateLiveData.clearRoleHoldersAsUser(this.mRole.getName(), 0, this.mUser, application);
        }
    }

    /* loaded from: classes.dex */
    public static class Factory implements ViewModelProvider.Factory {
        private Application mApplication;
        private Role mRole;
        private UserHandle mUser;

        public Factory(Role role, UserHandle userHandle, Application application) {
            this.mRole = role;
            this.mUser = userHandle;
            this.mApplication = application;
        }

        @Override // androidx.lifecycle.ViewModelProvider.Factory
        public <T extends ViewModel> T create(Class<T> cls) {
            return new DefaultAppViewModel(this.mRole, this.mUser, this.mApplication);
        }
    }
}
