package com.android.packageinstaller.permission.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.SearchIndexablesContract;
import android.provider.SearchIndexablesProvider;
import android.util.Log;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.Objects;
import java.util.UUID;
/* loaded from: classes.dex */
public abstract class BaseSearchIndexablesProvider extends SearchIndexablesProvider {
    private static final String LOG_TAG = "BaseSearchIndexablesProvider";
    private static final Object sPasswordLock = new Object();

    public boolean onCreate() {
        return true;
    }

    public Cursor queryXmlResources(String[] strArr) {
        return new MatrixCursor(SearchIndexablesContract.INDEXABLES_XML_RES_COLUMNS);
    }

    public Cursor queryNonIndexableKeys(String[] strArr) {
        return new MatrixCursor(SearchIndexablesContract.NON_INDEXABLES_KEYS_COLUMNS);
    }

    private static String getPassword(Context context) {
        String string;
        synchronized (sPasswordLock) {
            SharedPreferences deviceProtectedSharedPreferences = Utils.getDeviceProtectedSharedPreferences(context);
            string = deviceProtectedSharedPreferences.getString("search_indexable_provider_password", null);
            if (string == null) {
                string = UUID.randomUUID().toString();
                deviceProtectedSharedPreferences.edit().putString("search_indexable_provider_password", string).apply();
            }
        }
        return string;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static String createRawDataKey(String str, Context context) {
        return getPassword(context) + context.getPackageName() + ',' + str;
    }

    public static boolean isIntentValid(Intent intent, Context context) {
        String substring = intent.getStringExtra(":settings:fragment_args_key").substring(0, 36);
        boolean equals = Objects.equals(substring, getPassword(context));
        if (!equals) {
            String str = LOG_TAG;
            Log.w(str, "Invalid password: " + substring);
        }
        return equals;
    }

    public static String getOriginalKey(Intent intent) {
        int indexOf;
        String stringExtra = intent.getStringExtra(":settings:fragment_args_key");
        if (stringExtra != null && (indexOf = stringExtra.indexOf(44) + 1) <= stringExtra.length()) {
            return stringExtra.substring(indexOf);
        }
        return null;
    }
}
