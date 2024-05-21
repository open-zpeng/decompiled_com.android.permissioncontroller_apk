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
public class DefaultAppListViewModel extends AndroidViewModel {
    private final LiveData<List<RoleItem>> mLiveData;
    private final UserHandle mUser;
    private final LiveData<List<RoleItem>> mWorkLiveData;
    private final UserHandle mWorkProfile;

    public DefaultAppListViewModel(Application application) {
        super(application);
        this.mUser = Process.myUserHandle();
        RoleListSortFunction roleListSortFunction = new RoleListSortFunction(application);
        this.mLiveData = Transformations.map(new RoleListLiveData(true, this.mUser, application), roleListSortFunction);
        this.mWorkProfile = UserUtils.getWorkProfile(application);
        UserHandle userHandle = this.mWorkProfile;
        this.mWorkLiveData = userHandle != null ? Transformations.map(new RoleListLiveData(true, userHandle, application), roleListSortFunction) : null;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public LiveData<List<RoleItem>> getLiveData() {
        return this.mLiveData;
    }

    public boolean hasWorkProfile() {
        return this.mWorkProfile != null;
    }

    public UserHandle getWorkProfile() {
        return this.mWorkProfile;
    }

    public LiveData<List<RoleItem>> getWorkLiveData() {
        return this.mWorkLiveData;
    }
}
