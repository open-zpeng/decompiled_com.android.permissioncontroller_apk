package com.android.packageinstaller.role.ui;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.ui.SpecialAppAccessViewModel;
import com.android.packageinstaller.role.utils.UserUtils;
import java.util.List;
/* loaded from: classes.dex */
public class SpecialAppAccessViewModel extends AndroidViewModel {
    private static final String LOG_TAG = "SpecialAppAccessViewModel";
    private final ArrayMap<String, ManageRoleHolderStateLiveData> mManageRoleHolderStateLiveDatas;
    private final Role mRole;
    private final LiveData<List<Pair<ApplicationInfo, Boolean>>> mRoleLiveData;

    /* loaded from: classes.dex */
    public interface ManageRoleHolderStateObserver {
        void onManageRoleHolderStateChanged(ManageRoleHolderStateLiveData manageRoleHolderStateLiveData, int i);
    }

    public SpecialAppAccessViewModel(Role role, Application application) {
        super(application);
        this.mManageRoleHolderStateLiveDatas = new ArrayMap<>();
        this.mRole = role;
        RoleLiveData roleLiveData = new RoleLiveData(role, Process.myUserHandle(), application);
        UserHandle workProfile = UserUtils.getWorkProfile(application);
        RoleSortFunction roleSortFunction = new RoleSortFunction(application);
        if (workProfile == null) {
            this.mRoleLiveData = Transformations.map(roleLiveData, roleSortFunction);
        } else {
            this.mRoleLiveData = Transformations.map(new MergeRoleLiveData(roleLiveData, new RoleLiveData(role, workProfile, application)), roleSortFunction);
        }
    }

    public LiveData<List<Pair<ApplicationInfo, Boolean>>> getRoleLiveData() {
        return this.mRoleLiveData;
    }

    public void observeManageRoleHolderState(LifecycleOwner lifecycleOwner, final ManageRoleHolderStateObserver manageRoleHolderStateObserver) {
        int size = this.mManageRoleHolderStateLiveDatas.size();
        for (int i = 0; i < size; i++) {
            final ManageRoleHolderStateLiveData valueAt = this.mManageRoleHolderStateLiveDatas.valueAt(i);
            valueAt.observe(lifecycleOwner, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$SpecialAppAccessViewModel$fVF10iW-SbVtyzRH5XCZlJqd2fM
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    SpecialAppAccessViewModel.ManageRoleHolderStateObserver.this.onManageRoleHolderStateChanged(valueAt, ((Integer) obj).intValue());
                }
            });
        }
    }

    private ManageRoleHolderStateLiveData getManageRoleHolderStateLiveData(String str, LifecycleOwner lifecycleOwner, final ManageRoleHolderStateObserver manageRoleHolderStateObserver) {
        ManageRoleHolderStateLiveData manageRoleHolderStateLiveData = this.mManageRoleHolderStateLiveDatas.get(str);
        if (manageRoleHolderStateLiveData == null) {
            final ManageRoleHolderStateLiveData manageRoleHolderStateLiveData2 = new ManageRoleHolderStateLiveData();
            manageRoleHolderStateLiveData2.observe(lifecycleOwner, new Observer() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$SpecialAppAccessViewModel$eI5upCZsUPb8ve6wDAk1QV4TruA
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    SpecialAppAccessViewModel.ManageRoleHolderStateObserver.this.onManageRoleHolderStateChanged(manageRoleHolderStateLiveData2, ((Integer) obj).intValue());
                }
            });
            this.mManageRoleHolderStateLiveDatas.put(str, manageRoleHolderStateLiveData2);
            return manageRoleHolderStateLiveData2;
        }
        return manageRoleHolderStateLiveData;
    }

    public void setSpecialAppAccessAsUser(String str, boolean z, UserHandle userHandle, String str2, LifecycleOwner lifecycleOwner, ManageRoleHolderStateObserver manageRoleHolderStateObserver) {
        ManageRoleHolderStateLiveData manageRoleHolderStateLiveData = getManageRoleHolderStateLiveData(str2, lifecycleOwner, manageRoleHolderStateObserver);
        if (manageRoleHolderStateLiveData.getValue().intValue() != 0) {
            Log.i(LOG_TAG, "Trying to set special app access while another request is on-going");
        } else {
            manageRoleHolderStateLiveData.setRoleHolderAsUser(this.mRole.getName(), str, z, 0, userHandle, getApplication());
        }
    }

    /* loaded from: classes.dex */
    public static class Factory implements ViewModelProvider.Factory {
        private Application mApplication;
        private Role mRole;

        public Factory(Role role, Application application) {
            this.mRole = role;
            this.mApplication = application;
        }

        @Override // androidx.lifecycle.ViewModelProvider.Factory
        public <T extends ViewModel> T create(Class<T> cls) {
            return new SpecialAppAccessViewModel(this.mRole, this.mApplication);
        }
    }
}
