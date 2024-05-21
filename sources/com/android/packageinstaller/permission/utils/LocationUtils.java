package com.android.packageinstaller.permission.utils;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class LocationUtils {
    private static final String TAG = "LocationUtils";

    public static void showLocationDialog(final Context context, CharSequence charSequence) {
        new AlertDialog.Builder(context).setIcon(R.drawable.ic_dialog_alert_material).setTitle(17039380).setMessage(context.getString(R.string.location_warning, charSequence)).setNegativeButton(R.string.ok, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.utils.LocationUtils.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        }).show();
    }

    public static void startLocationControllerExtraPackageSettings(Context context) {
        try {
            context.startActivity(new Intent("android.settings.LOCATION_CONTROLLER_EXTRA_PACKAGE_SETTINGS"));
        } catch (ActivityNotFoundException unused) {
            Log.e(TAG, "No activity to handle android.settings.LOCATION_CONTROLLER_EXTRA_PACKAGE_SETTINGS");
        }
    }

    public static boolean isLocationEnabled(Context context) {
        return ((LocationManager) context.getSystemService(LocationManager.class)).isLocationEnabled();
    }

    public static boolean isLocationGroupAndProvider(Context context, String str, String str2) {
        return "android.permission-group.LOCATION".equals(str) && ((LocationManager) context.getSystemService(LocationManager.class)).isProviderPackage(str2);
    }

    public static boolean isLocationGroupAndControllerExtraPackage(Context context, String str, String str2) {
        return "android.permission-group.LOCATION".equals(str) && str2.equals(((LocationManager) context.getSystemService(LocationManager.class)).getExtraLocationControllerPackage());
    }

    public static boolean isExtraLocationControllerPackageEnabled(Context context) {
        try {
            return ((LocationManager) context.getSystemService(LocationManager.class)).isExtraLocationControllerPackageEnabled();
        } catch (Exception unused) {
            return false;
        }
    }
}
