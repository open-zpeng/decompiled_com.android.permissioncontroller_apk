package com.android.packageinstaller.permission.utils;

import android.app.Application;
import android.app.role.RoleManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcelable;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import androidx.core.text.BidiFormatter;
import androidx.core.util.Preconditions;
import com.android.car.ui.R;
import com.android.launcher3.icons.IconFactory;
import com.android.packageinstaller.permission.data.PerUserUidToSensitivityLiveData;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
/* loaded from: classes.dex */
public final class Utils {
    private static final Intent LAUNCHER_INTENT = new Intent("android.intent.action.MAIN", (Uri) null).addCategory("android.intent.category.LAUNCHER");
    private static final ArrayMap<String, String> PLATFORM_PERMISSIONS = new ArrayMap<>();
    private static final ArrayMap<String, ArrayList<String>> PLATFORM_PERMISSION_GROUPS;

    static {
        PLATFORM_PERMISSIONS.put("android.permission.READ_CONTACTS", "android.permission-group.CONTACTS");
        PLATFORM_PERMISSIONS.put("android.permission.WRITE_CONTACTS", "android.permission-group.CONTACTS");
        PLATFORM_PERMISSIONS.put("android.permission.GET_ACCOUNTS", "android.permission-group.CONTACTS");
        PLATFORM_PERMISSIONS.put("android.permission.READ_CALENDAR", "android.permission-group.CALENDAR");
        PLATFORM_PERMISSIONS.put("android.permission.WRITE_CALENDAR", "android.permission-group.CALENDAR");
        PLATFORM_PERMISSIONS.put("android.permission.SEND_SMS", "android.permission-group.SMS");
        PLATFORM_PERMISSIONS.put("android.permission.RECEIVE_SMS", "android.permission-group.SMS");
        PLATFORM_PERMISSIONS.put("android.permission.READ_SMS", "android.permission-group.SMS");
        PLATFORM_PERMISSIONS.put("android.permission.RECEIVE_MMS", "android.permission-group.SMS");
        PLATFORM_PERMISSIONS.put("android.permission.RECEIVE_WAP_PUSH", "android.permission-group.SMS");
        PLATFORM_PERMISSIONS.put("android.permission.READ_CELL_BROADCASTS", "android.permission-group.SMS");
        PLATFORM_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", "android.permission-group.STORAGE");
        PLATFORM_PERMISSIONS.put("android.permission.WRITE_EXTERNAL_STORAGE", "android.permission-group.STORAGE");
        PLATFORM_PERMISSIONS.put("android.permission.ACCESS_MEDIA_LOCATION", "android.permission-group.STORAGE");
        PLATFORM_PERMISSIONS.put("android.permission.ACCESS_FINE_LOCATION", "android.permission-group.LOCATION");
        PLATFORM_PERMISSIONS.put("android.permission.ACCESS_COARSE_LOCATION", "android.permission-group.LOCATION");
        PLATFORM_PERMISSIONS.put("android.permission.ACCESS_BACKGROUND_LOCATION", "android.permission-group.LOCATION");
        PLATFORM_PERMISSIONS.put("android.permission.READ_CALL_LOG", "android.permission-group.CALL_LOG");
        PLATFORM_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", "android.permission-group.CALL_LOG");
        PLATFORM_PERMISSIONS.put("android.permission.PROCESS_OUTGOING_CALLS", "android.permission-group.CALL_LOG");
        PLATFORM_PERMISSIONS.put("android.permission.READ_PHONE_STATE", "android.permission-group.PHONE");
        PLATFORM_PERMISSIONS.put("android.permission.READ_PHONE_NUMBERS", "android.permission-group.PHONE");
        PLATFORM_PERMISSIONS.put("android.permission.CALL_PHONE", "android.permission-group.PHONE");
        PLATFORM_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", "android.permission-group.PHONE");
        PLATFORM_PERMISSIONS.put("android.permission.USE_SIP", "android.permission-group.PHONE");
        PLATFORM_PERMISSIONS.put("android.permission.ANSWER_PHONE_CALLS", "android.permission-group.PHONE");
        PLATFORM_PERMISSIONS.put("android.permission.ACCEPT_HANDOVER", "android.permission-group.PHONE");
        PLATFORM_PERMISSIONS.put("android.permission.RECORD_AUDIO", "android.permission-group.MICROPHONE");
        PLATFORM_PERMISSIONS.put("android.permission.ACTIVITY_RECOGNITION", "android.permission-group.ACTIVITY_RECOGNITION");
        PLATFORM_PERMISSIONS.put("android.permission.CAMERA", "android.permission-group.CAMERA");
        PLATFORM_PERMISSIONS.put("android.permission.BODY_SENSORS", "android.permission-group.SENSORS");
        PLATFORM_PERMISSION_GROUPS = new ArrayMap<>();
        int size = PLATFORM_PERMISSIONS.size();
        for (int i = 0; i < size; i++) {
            String keyAt = PLATFORM_PERMISSIONS.keyAt(i);
            String valueAt = PLATFORM_PERMISSIONS.valueAt(i);
            ArrayList<String> arrayList = PLATFORM_PERMISSION_GROUPS.get(valueAt);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                PLATFORM_PERMISSION_GROUPS.put(valueAt, arrayList);
            }
            arrayList.add(keyAt);
        }
    }

    public static <M> M getSystemServiceSafe(Context context, Class<M> cls) {
        M m = (M) context.getSystemService(cls);
        Preconditions.checkNotNull(m, "Could not resolve " + cls.getSimpleName());
        return m;
    }

    public static <M> M getSystemServiceSafe(Context context, Class<M> cls, UserHandle userHandle) {
        try {
            M m = (M) context.createPackageContextAsUser(context.getPackageName(), 0, userHandle).getSystemService(cls);
            Preconditions.checkNotNull(m, "Could not resolve " + cls.getSimpleName());
            return m;
        } catch (PackageManager.NameNotFoundException unused) {
            throw new IllegalStateException();
        }
    }

    public static <T extends Parcelable> T getParcelableExtraSafe(Intent intent, String str) {
        Parcelable parcelableExtra = intent.getParcelableExtra(str);
        Preconditions.checkNotNull(parcelableExtra, "Could not get parcelable extra for " + str);
        return (T) parcelableExtra;
    }

    public static String getStringExtraSafe(Intent intent, String str) {
        String stringExtra = intent.getStringExtra(str);
        Preconditions.checkNotNull(stringExtra, "Could not get string extra for " + str);
        return stringExtra;
    }

    public static String getGroupOfPlatformPermission(String str) {
        return PLATFORM_PERMISSIONS.get(str);
    }

    public static String getGroupOfPermission(PermissionInfo permissionInfo) {
        String groupOfPlatformPermission = getGroupOfPlatformPermission(permissionInfo.name);
        return groupOfPlatformPermission == null ? permissionInfo.group : groupOfPlatformPermission;
    }

    public static List<String> getPlatformPermissionNamesOfGroup(String str) {
        ArrayList<String> arrayList = PLATFORM_PERMISSION_GROUPS.get(str);
        return arrayList != null ? arrayList : Collections.emptyList();
    }

    public static List<PermissionInfo> getPlatformPermissionsOfGroup(PackageManager packageManager, String str) {
        ArrayList arrayList = new ArrayList();
        ArrayList<String> arrayList2 = PLATFORM_PERMISSION_GROUPS.get(str);
        if (arrayList2 == null) {
            return Collections.emptyList();
        }
        int size = arrayList2.size();
        for (int i = 0; i < size; i++) {
            String str2 = arrayList2.get(i);
            try {
                arrayList.add(packageManager.getPermissionInfo(str2, 0));
            } catch (PackageManager.NameNotFoundException e) {
                throw new IllegalStateException(str2 + " not defined by platform", e);
            }
        }
        return arrayList;
    }

    public static List<PermissionInfo> getPermissionInfosForGroup(PackageManager packageManager, String str) throws PackageManager.NameNotFoundException {
        ArrayList arrayList = new ArrayList();
        for (PermissionInfo permissionInfo : packageManager.queryPermissionsByGroup(str, 0)) {
            if (getGroupOfPermission(permissionInfo).equals(str)) {
                arrayList.add(permissionInfo);
            }
        }
        arrayList.addAll(getPlatformPermissionsOfGroup(packageManager, str));
        return arrayList;
    }

    public static PackageItemInfo getGroupInfo(String str, Context context) {
        try {
            try {
                return context.getPackageManager().getPermissionGroupInfo(str, 0);
            } catch (PackageManager.NameNotFoundException unused) {
                return context.getPackageManager().getPermissionInfo(str, 0);
            }
        } catch (PackageManager.NameNotFoundException unused2) {
            return null;
        }
    }

    public static List<PermissionInfo> getGroupPermissionInfos(String str, Context context) {
        try {
            try {
                return getPermissionInfosForGroup(context.getPackageManager(), str);
            } catch (PackageManager.NameNotFoundException unused) {
                PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(str, 0);
                ArrayList arrayList = new ArrayList();
                arrayList.add(permissionInfo);
                return arrayList;
            }
        } catch (PackageManager.NameNotFoundException unused2) {
            return null;
        }
    }

    public static String getAppLabel(ApplicationInfo applicationInfo, Context context) {
        return getAppLabel(applicationInfo, 500.0f, context);
    }

    public static String getFullAppLabel(ApplicationInfo applicationInfo, Context context) {
        return getAppLabel(applicationInfo, 0.0f, context);
    }

    private static String getAppLabel(ApplicationInfo applicationInfo, float f, Context context) {
        return BidiFormatter.getInstance().unicodeWrap(applicationInfo.loadSafeLabel(context.getPackageManager(), f, 5).toString());
    }

    public static Drawable loadDrawable(PackageManager packageManager, String str, int i) {
        try {
            return packageManager.getResourcesForApplication(str).getDrawable(i, null);
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
            Log.d("Utils", "Couldn't get resource", e);
            return null;
        }
    }

    public static boolean isModernPermissionGroup(String str) {
        return PLATFORM_PERMISSION_GROUPS.containsKey(str);
    }

    public static List<String> getPlatformPermissionGroups() {
        return new ArrayList(PLATFORM_PERMISSION_GROUPS.keySet());
    }

    public static Set<String> getPlatformPermissions() {
        return PLATFORM_PERMISSIONS.keySet();
    }

    public static boolean shouldShowPermission(Context context, AppPermissionGroup appPermissionGroup) {
        if (appPermissionGroup.isGrantingAllowed()) {
            return !appPermissionGroup.getDeclaringPackage().equals("android") || isModernPermissionGroup(appPermissionGroup.getName());
        }
        return false;
    }

    public static Drawable applyTint(Context context, Drawable drawable, int i) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(i, typedValue, true);
        Drawable mutate = drawable.mutate();
        mutate.setTint(context.getColor(typedValue.resourceId));
        return mutate;
    }

    public static Drawable applyTint(Context context, int i, int i2) {
        return applyTint(context, context.getDrawable(i), i2);
    }

    public static ArraySet<String> getLauncherPackages(Context context) {
        ArraySet<String> arraySet = new ArraySet<>();
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(LAUNCHER_INTENT, 786432)) {
            arraySet.add(resolveInfo.activityInfo.packageName);
        }
        return arraySet;
    }

    public static boolean isGroupOrBgGroupUserSensitive(AppPermissionGroup appPermissionGroup) {
        return appPermissionGroup.isUserSensitive() || (appPermissionGroup.getBackgroundPermissions() != null && appPermissionGroup.getBackgroundPermissions().isUserSensitive());
    }

    public static boolean areGroupPermissionsIndividuallyControlled(Context context, String str) {
        if (context.getPackageManager().arePermissionsIndividuallyControlled()) {
            return "android.permission-group.SMS".equals(str) || "android.permission-group.PHONE".equals(str) || "android.permission-group.CONTACTS".equals(str);
        }
        return false;
    }

    public static boolean isPermissionIndividuallyControlled(Context context, String str) {
        if (context.getPackageManager().arePermissionsIndividuallyControlled()) {
            return "android.permission.READ_CONTACTS".equals(str) || "android.permission.WRITE_CONTACTS".equals(str) || "android.permission.SEND_SMS".equals(str) || "android.permission.RECEIVE_SMS".equals(str) || "android.permission.READ_SMS".equals(str) || "android.permission.RECEIVE_MMS".equals(str) || "android.permission.CALL_PHONE".equals(str) || "android.permission.READ_CALL_LOG".equals(str) || "android.permission.WRITE_CALL_LOG".equals(str);
        }
        return false;
    }

    public static CharSequence getRequestMessage(CharSequence charSequence, AppPermissionGroup appPermissionGroup, Context context, int i) {
        if (appPermissionGroup.getName().equals("android.permission-group.STORAGE") && !appPermissionGroup.isNonIsolatedStorage()) {
            return Html.fromHtml(String.format(context.getResources().getConfiguration().getLocales().get(0), context.getString(R.string.permgrouprequest_storage_isolated), charSequence), 0);
        }
        if (i != 0) {
            try {
                return Html.fromHtml(context.getPackageManager().getResourcesForApplication(appPermissionGroup.getDeclaringPackage()).getString(i, charSequence), 0);
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return Html.fromHtml(context.getString(R.string.permission_warning_template, charSequence, appPermissionGroup.getDescription()), 0);
    }

    public static String getAbsoluteTimeString(Context context, long j) {
        if (j == 0) {
            return null;
        }
        if (isToday(j)) {
            return DateFormat.getTimeFormat(context).format(Long.valueOf(j));
        }
        return DateFormat.getMediumDateFormat(context).format(Long.valueOf(j));
    }

    private static boolean isToday(long j) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        Calendar calendar2 = Calendar.getInstance(Locale.getDefault());
        calendar2.setTimeInMillis(j);
        return !calendar2.before(calendar);
    }

    public static void prepareSearchMenuItem(Menu menu, final Context context) {
        final Intent intent = new Intent("android.settings.APP_SEARCH_SETTINGS");
        if (context.getPackageManager().resolveActivity(intent, 0) == null) {
            return;
        }
        MenuItem add = menu.add(0, 0, 0, R.string.search_menu);
        add.setIcon(R.drawable.ic_search_24dp);
        add.setShowAsAction(2);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() { // from class: com.android.packageinstaller.permission.utils.-$$Lambda$Utils$AlZca1McXFf_YE1Da-jLEsZIXSk
            @Override // android.view.MenuItem.OnMenuItemClickListener
            public final boolean onMenuItemClick(MenuItem menuItem) {
                return Utils.lambda$prepareSearchMenuItem$0(context, intent, menuItem);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$prepareSearchMenuItem$0(Context context, Intent intent, MenuItem menuItem) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.e("Utils", "Cannot start activity to search settings", e);
            return true;
        }
    }

    public static Drawable getBadgedIcon(Context context, ApplicationInfo applicationInfo) {
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(applicationInfo.uid);
        IconFactory obtain = IconFactory.obtain(context);
        try {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), obtain.createBadgedIconBitmap(applicationInfo.loadUnbadgedIcon(context.getPackageManager()), userHandleForUid, false).icon);
            if (obtain != null) {
                obtain.close();
            }
            return bitmapDrawable;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (obtain != null) {
                    try {
                        obtain.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static String getPermissionGroupDescriptionString(Context context, String str, CharSequence charSequence) {
        char c;
        switch (str.hashCode()) {
            case -1639857183:
                if (str.equals("android.permission-group.CONTACTS")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1410061184:
                if (str.equals("android.permission-group.PHONE")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1250730292:
                if (str.equals("android.permission-group.CALENDAR")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1243751087:
                if (str.equals("android.permission-group.CALL_LOG")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1140935117:
                if (str.equals("android.permission-group.CAMERA")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 225035509:
                if (str.equals("android.permission-group.ACTIVITY_RECOGNITION")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 421761675:
                if (str.equals("android.permission-group.SENSORS")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 828638019:
                if (str.equals("android.permission-group.LOCATION")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 852078861:
                if (str.equals("android.permission-group.STORAGE")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case 1581272376:
                if (str.equals("android.permission-group.MICROPHONE")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1795181803:
                if (str.equals("android.permission-group.SMS")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return context.getString(R.string.permission_description_summary_activity_recognition);
            case 1:
                return context.getString(R.string.permission_description_summary_calendar);
            case 2:
                return context.getString(R.string.permission_description_summary_call_log);
            case 3:
                return context.getString(R.string.permission_description_summary_camera);
            case 4:
                return context.getString(R.string.permission_description_summary_contacts);
            case 5:
                return context.getString(R.string.permission_description_summary_location);
            case 6:
                return context.getString(R.string.permission_description_summary_microphone);
            case 7:
                return context.getString(R.string.permission_description_summary_phone);
            case '\b':
                return context.getString(R.string.permission_description_summary_sensors);
            case '\t':
                return context.getString(R.string.permission_description_summary_sms);
            case '\n':
                return context.getString(R.string.permission_description_summary_storage);
            default:
                return context.getString(R.string.permission_description_summary_generic, charSequence);
        }
    }

    public static boolean isLocationAccessCheckEnabled() {
        return DeviceConfig.getBoolean("privacy", "location_access_check_enabled", true);
    }

    public static SharedPreferences getDeviceProtectedSharedPreferences(Context context) {
        if (!context.isDeviceProtectedStorage()) {
            context = context.createDeviceProtectedStorageContext();
        }
        return context.getSharedPreferences("preferences", 0);
    }

    public static void updateUserSensitive(Application application, UserHandle userHandle) {
        String str;
        int i;
        int i2;
        ArrayMap<String, Integer> arrayMap;
        String[] strArr;
        int i3;
        Context parentUserContext = getParentUserContext(application);
        PackageManager packageManager = parentUserContext.getPackageManager();
        int i4 = 0;
        SharedPreferences sharedPreferences = parentUserContext.getSharedPreferences("preferences", 0);
        boolean z = sharedPreferences.getBoolean("assistant_record_audio_is_user_sensitive_key", false);
        Set<String> stringSet = sharedPreferences.getStringSet("forced_user_sensitive_uids_key", Collections.emptySet());
        List roleHolders = ((RoleManager) parentUserContext.getSystemService(RoleManager.class)).getRoleHolders("android.app.role.ASSISTANT");
        if (roleHolders.isEmpty()) {
            str = null;
        } else {
            if (roleHolders.size() > 1) {
                Log.wtf("Utils", "Assistant role is not exclusive");
            }
            str = (String) roleHolders.get(0);
        }
        String str2 = str;
        SparseArray<ArrayMap<String, Integer>> loadValueInBackground = PerUserUidToSensitivityLiveData.get(userHandle, application).loadValueInBackground();
        int size = loadValueInBackground.size();
        int i5 = 0;
        while (i5 < size) {
            int keyAt = loadValueInBackground.keyAt(i5);
            String[] packagesForUid = packageManager.getPackagesForUid(keyAt);
            if (packagesForUid != null) {
                boolean contains = stringSet.contains(String.valueOf(keyAt));
                boolean contains2 = ArrayUtils.contains(packagesForUid, str2);
                ArrayMap<String, Integer> valueAt = loadValueInBackground.valueAt(i5);
                int size2 = valueAt.size();
                int i6 = i4;
                while (i6 < size2) {
                    String keyAt2 = valueAt.keyAt(i6);
                    int length = packagesForUid.length;
                    Set<String> set = stringSet;
                    int i7 = 0;
                    while (true) {
                        if (i7 >= length) {
                            i = i6;
                            i2 = size2;
                            arrayMap = valueAt;
                            strArr = packagesForUid;
                            i3 = i5;
                            break;
                        }
                        int i8 = length;
                        String str3 = packagesForUid[i7];
                        int intValue = contains ? 768 : valueAt.valueAt(i6).intValue();
                        if (contains2 && keyAt2.equals("android.permission.RECORD_AUDIO")) {
                            intValue = z ? 768 : 0;
                        }
                        String str4 = keyAt2;
                        i = i6;
                        i2 = size2;
                        arrayMap = valueAt;
                        strArr = packagesForUid;
                        int i9 = intValue;
                        i3 = i5;
                        try {
                            packageManager.updatePermissionFlags(str4, str3, 768, i9, userHandle);
                            break;
                        } catch (IllegalArgumentException e) {
                            Log.e("Utils", "Unexpected exception while updating flags for " + str3 + " permission " + str4, e);
                            i7++;
                            length = i8;
                            keyAt2 = str4;
                            i5 = i3;
                            i6 = i;
                            size2 = i2;
                            valueAt = arrayMap;
                            packagesForUid = strArr;
                        }
                    }
                    i6 = i + 1;
                    stringSet = set;
                    i5 = i3;
                    size2 = i2;
                    valueAt = arrayMap;
                    packagesForUid = strArr;
                }
            }
            i5++;
            stringSet = stringSet;
            i4 = 0;
        }
    }

    public static Context getParentUserContext(Context context) {
        UserHandle profileParent = ((UserManager) getSystemServiceSafe(context, UserManager.class)).getProfileParent(UserHandle.of(UserHandle.myUserId()));
        if (profileParent == null) {
            return context;
        }
        try {
            return context.createPackageContextAsUser(context.getPackageName(), 0, profileParent);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("Could not switch to parent user " + profileParent, e);
        }
    }
}
