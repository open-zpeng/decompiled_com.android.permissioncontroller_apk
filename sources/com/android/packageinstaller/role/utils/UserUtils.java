package com.android.packageinstaller.role.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class UserUtils {
    public static boolean isWorkProfile(UserHandle userHandle, Context context) {
        return ((UserManager) context.getSystemService(UserManager.class)).isManagedProfile(userHandle.getIdentifier());
    }

    public static UserHandle getWorkProfile(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        List<UserHandle> userProfiles = userManager.getUserProfiles();
        UserHandle myUserHandle = Process.myUserHandle();
        int size = userProfiles.size();
        for (int i = 0; i < size; i++) {
            UserHandle userHandle = userProfiles.get(i);
            if (!Objects.equals(userHandle, myUserHandle) && userManager.isManagedProfile(userHandle.getIdentifier())) {
                return userHandle;
            }
        }
        return null;
    }

    public static Context getUserContext(Context context, UserHandle userHandle) {
        if (Process.myUserHandle().equals(userHandle)) {
            return context;
        }
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, userHandle);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
