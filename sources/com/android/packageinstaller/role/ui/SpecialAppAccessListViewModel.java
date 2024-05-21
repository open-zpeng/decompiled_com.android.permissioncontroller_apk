package com.android.packageinstaller.role.ui;

import android.app.Application;
import android.os.Process;
import android.os.UserHandle;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.android.packageinstaller.role.utils.UserUtils;
import java.util.List;
/* loaded from: classes.dex */
public class SpecialAppAccessListViewModel extends AndroidViewModel {
    private final LiveData<List<RoleItem>> mLiveData;

    public SpecialAppAccessListViewModel(Application application) {
        super(application);
        RoleListLiveData roleListLiveData = new RoleListLiveData(false, Process.myUserHandle(), application);
        UserHandle workProfile = UserUtils.getWorkProfile(application);
        RoleListSortFunction roleListSortFunction = new RoleListSortFunction(application);
        if (workProfile == null) {
            this.mLiveData = Transformations.map(roleListLiveData, roleListSortFunction);
        } else {
            this.mLiveData = Transformations.map(new MergeRoleListLiveData(roleListLiveData, new RoleListLiveData(false, workProfile, application)), roleListSortFunction);
        }
    }

    public LiveData<List<RoleItem>> getLiveData() {
        return this.mLiveData;
    }
}
