package com.android.packageinstaller.role.service;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Binder;
import android.provider.SearchIndexablesContract;
import android.util.ArrayMap;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.service.BaseSearchIndexablesProvider;
import com.android.packageinstaller.role.model.Role;
import com.android.packageinstaller.role.model.Roles;
/* loaded from: classes.dex */
public class RoleSearchIndexablesProvider extends BaseSearchIndexablesProvider {
    public Cursor queryRawData(String[] strArr) {
        MatrixCursor matrixCursor = new MatrixCursor(SearchIndexablesContract.INDEXABLES_RAW_COLUMNS);
        Context context = getContext();
        ArrayMap<String, Role> arrayMap = Roles.get(context);
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            Role valueAt = arrayMap.valueAt(i);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (valueAt.isAvailable(context) && valueAt.isVisible(context)) {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    String string = context.getString(valueAt.getLabelResource());
                    boolean isExclusive = valueAt.isExclusive();
                    MatrixCursor.RowBuilder add = matrixCursor.newRow().add("rank", 0).add("title", string);
                    StringBuilder sb = new StringBuilder();
                    sb.append(string);
                    sb.append(", ");
                    sb.append(getContext().getString(isExclusive ? R.string.default_app_search_keyword : R.string.special_app_access_search_keyword));
                    add.add("keywords", sb.toString()).add("key", BaseSearchIndexablesProvider.createRawDataKey(valueAt.getName(), context)).add("intentAction", isExclusive ? "com.android.permissioncontroller.settingssearch.action.MANAGE_DEFAULT_APP" : "com.android.permissioncontroller.settingssearch.action.MANAGE_SPECIAL_APP_ACCESS");
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return matrixCursor;
    }
}
