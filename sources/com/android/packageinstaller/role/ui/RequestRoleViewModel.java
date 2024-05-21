package com.android.packageinstaller.role.ui;

import android.app.Application;
import android.os.Process;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.android.packageinstaller.role.model.Role;
/* loaded from: classes.dex */
public class RequestRoleViewModel extends DefaultAppViewModel {
    public RequestRoleViewModel(Role role, Application application) {
        super(role, Process.myUserHandle(), application);
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
            return new RequestRoleViewModel(this.mRole, this.mApplication);
        }
    }
}
