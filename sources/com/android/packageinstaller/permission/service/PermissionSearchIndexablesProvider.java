package com.android.packageinstaller.permission.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexablesContract;
import android.util.Log;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.List;
/* loaded from: classes.dex */
public class PermissionSearchIndexablesProvider extends BaseSearchIndexablesProvider {
    private static final String LOG_TAG = "PermissionSearchIndexablesProvider";

    public Cursor queryRawData(String[] strArr) {
        CharSequence permissionGroupLabel;
        Context context = getContext();
        PackageManager packageManager = context.getPackageManager();
        List<String> platformPermissionGroups = Utils.getPlatformPermissionGroups();
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
        int size = platformPermissionGroups.size();
        for (int i = 0; i < size; i++) {
            String str = platformPermissionGroups.get(i);
            matrixCursor.newRow().add("rank", 0).add("title", getPermissionGroupLabel(str, packageManager)).add("keywords", ((Object) permissionGroupLabel) + ", " + context.getString(R.string.permission_search_keyword)).add("key", BaseSearchIndexablesProvider.createRawDataKey(str, context)).add("intentAction", "com.android.permissioncontroller.settingssearch.action.MANAGE_PERMISSION_APPS");
        }
        return matrixCursor;
    }

    private CharSequence getPermissionGroupLabel(String str, PackageManager packageManager) {
        try {
            return packageManager.getPermissionGroupInfo(str, 0).loadLabel(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            String str2 = LOG_TAG;
            Log.w(str2, "Cannot find group label for " + str, e);
            return null;
        }
    }
}
