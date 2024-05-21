package com.android.packageinstaller.permission.service;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.Log;
import androidx.core.util.Preconditions;
import com.android.car.ui.R;
import com.android.packageinstaller.PermissionControllerStatsLog;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.service.LocationAccessCheck;
import com.android.packageinstaller.permission.ui.AppPermissionActivity;
import com.android.packageinstaller.permission.utils.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
/* loaded from: classes.dex */
public class LocationAccessCheck {
    private static final String LOG_TAG = "LocationAccessCheck";
    private static final Object sLock = new Object();
    private final AppOpsManager mAppOpsManager;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final JobScheduler mJobScheduler;
    private final PackageManager mPackageManager;
    private final Random mRandom = new Random();
    private final SharedPreferences mSharedPrefs;
    private final BooleanSupplier mShouldCancel;
    private final UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: private */
    public long getPeriodicCheckIntervalMillis() {
        return Settings.Secure.getLong(this.mContentResolver, "location_access_check_interval_millis", TimeUnit.DAYS.toMillis(1L));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getFlexForPeriodicCheckMillis() {
        return getPeriodicCheckIntervalMillis() / 10;
    }

    private long getDelayMillis() {
        return Settings.Secure.getLong(this.mContentResolver, "location_access_check_delay_millis", TimeUnit.DAYS.toMillis(1L));
    }

    private long getInBetweenNotificationsMillis() {
        return getPeriodicCheckIntervalMillis() - ((long) (getFlexForPeriodicCheckMillis() * 2.1d));
    }

    private ArraySet<UserPackage> loadAlreadyNotifiedPackagesLocked() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.mContext.openFileInput("packages_already_notified_location_access")));
            ArraySet<UserPackage> arraySet = new ArraySet<>();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    String[] split = readLine.split(" ");
                    String str = split[0];
                    UserHandle userForSerialNumber = this.mUserManager.getUserForSerialNumber(Long.valueOf(split[1]).longValue());
                    if (userForSerialNumber != null) {
                        arraySet.add(new UserPackage(this.mContext, str, userForSerialNumber));
                    } else {
                        String str2 = LOG_TAG;
                        Log.i(str2, "Not restoring state \"" + readLine + "\" as user is unknown");
                    }
                } else {
                    $closeResource(null, bufferedReader);
                    return arraySet;
                }
            }
        } catch (FileNotFoundException unused) {
            return new ArraySet<>();
        } catch (Exception e) {
            Log.w(LOG_TAG, "Could not read packages_already_notified_location_access", e);
            return new ArraySet<>();
        }
    }

    private static /* synthetic */ void $closeResource(Throwable th, AutoCloseable autoCloseable) {
        if (th == null) {
            autoCloseable.close();
            return;
        }
        try {
            autoCloseable.close();
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    private void safeAlreadyNotifiedPackagesLocked(ArraySet<UserPackage> arraySet) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.mContext.openFileOutput("packages_already_notified_location_access", 0)));
            int size = arraySet.size();
            for (int i = 0; i < size; i++) {
                UserPackage valueAt = arraySet.valueAt(i);
                bufferedWriter.append((CharSequence) valueAt.pkg);
                bufferedWriter.append(' ');
                bufferedWriter.append((CharSequence) Long.valueOf(this.mUserManager.getSerialNumberForUser(valueAt.user)).toString());
                bufferedWriter.newLine();
            }
            $closeResource(null, bufferedWriter);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not write packages_already_notified_location_access", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markAsNotified(String str, UserHandle userHandle) {
        synchronized (sLock) {
            ArraySet<UserPackage> loadAlreadyNotifiedPackagesLocked = loadAlreadyNotifiedPackagesLocked();
            loadAlreadyNotifiedPackagesLocked.add(new UserPackage(this.mContext, str, userHandle));
            safeAlreadyNotifiedPackagesLocked(loadAlreadyNotifiedPackagesLocked);
        }
    }

    private void createPermissionReminderChannel(UserHandle userHandle) {
        ((NotificationManager) Utils.getSystemServiceSafe(this.mContext, NotificationManager.class, userHandle)).createNotificationChannel(new NotificationChannel("permission reminders", this.mContext.getString(R.string.permission_reminders), 2));
    }

    private void throwInterruptedExceptionIfTaskIsCanceled() throws InterruptedException {
        BooleanSupplier booleanSupplier = this.mShouldCancel;
        if (booleanSupplier != null && booleanSupplier.getAsBoolean()) {
            throw new InterruptedException();
        }
    }

    public LocationAccessCheck(Context context, BooleanSupplier booleanSupplier) {
        this.mContext = Utils.getParentUserContext(context);
        this.mJobScheduler = (JobScheduler) Utils.getSystemServiceSafe(this.mContext, JobScheduler.class);
        this.mAppOpsManager = (AppOpsManager) Utils.getSystemServiceSafe(this.mContext, AppOpsManager.class);
        this.mPackageManager = this.mContext.getPackageManager();
        this.mUserManager = (UserManager) Utils.getSystemServiceSafe(this.mContext, UserManager.class);
        this.mSharedPrefs = this.mContext.getSharedPreferences("preferences", 0);
        this.mContentResolver = this.mContext.getContentResolver();
        this.mShouldCancel = booleanSupplier;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addLocationNotificationIfNeeded(JobParameters jobParameters, LocationAccessCheckJobService locationAccessCheckJobService) {
        if (!checkLocationAccessCheckEnabledAndUpdateEnabledTime()) {
            locationAccessCheckJobService.jobFinished(jobParameters, false);
            return;
        }
        synchronized (sLock) {
            try {
            } catch (Exception e) {
                Log.e(LOG_TAG, "Could not check for location access", e);
                locationAccessCheckJobService.jobFinished(jobParameters, true);
                synchronized (sLock) {
                    locationAccessCheckJobService.mAddLocationNotificationIfNeededTask = null;
                }
            }
            if (System.currentTimeMillis() - this.mSharedPrefs.getLong("last_location_access_notification_shown", 0L) < getInBetweenNotificationsMillis()) {
                locationAccessCheckJobService.jobFinished(jobParameters, false);
                synchronized (sLock) {
                    locationAccessCheckJobService.mAddLocationNotificationIfNeededTask = null;
                }
            } else if (getCurrentlyShownNotificationLocked() != null) {
                locationAccessCheckJobService.jobFinished(jobParameters, false);
                synchronized (sLock) {
                    locationAccessCheckJobService.mAddLocationNotificationIfNeededTask = null;
                }
            } else {
                addLocationNotificationIfNeeded(this.mAppOpsManager.getPackagesForOps(new String[]{"android:fine_location"}));
                locationAccessCheckJobService.jobFinished(jobParameters, false);
                synchronized (sLock) {
                    locationAccessCheckJobService.mAddLocationNotificationIfNeededTask = null;
                }
            }
        }
    }

    private void addLocationNotificationIfNeeded(List<AppOpsManager.PackageOps> list) throws InterruptedException {
        UserPackage userPackage;
        synchronized (sLock) {
            List<UserPackage> locationUsersWithNoNotificationYetLocked = getLocationUsersWithNoNotificationYetLocked(list);
            PackageInfo packageInfo = null;
            while (packageInfo == null) {
                throwInterruptedExceptionIfTaskIsCanceled();
                if (locationUsersWithNoNotificationYetLocked.isEmpty()) {
                    return;
                }
                int size = locationUsersWithNoNotificationYetLocked.size();
                int i = 0;
                while (true) {
                    if (i >= size) {
                        userPackage = null;
                        break;
                    }
                    userPackage = locationUsersWithNoNotificationYetLocked.get(i);
                    LocationManager locationManager = (LocationManager) Utils.getSystemServiceSafe(this.mContext, LocationManager.class, userPackage.user);
                    if (locationManager.isExtraLocationControllerPackageEnabled() && userPackage.pkg.equals(locationManager.getExtraLocationControllerPackage())) {
                        break;
                    }
                    i++;
                }
                if (userPackage == null) {
                    userPackage = locationUsersWithNoNotificationYetLocked.get(this.mRandom.nextInt(locationUsersWithNoNotificationYetLocked.size()));
                }
                try {
                    packageInfo = userPackage.getPackageInfo();
                } catch (PackageManager.NameNotFoundException unused) {
                    locationUsersWithNoNotificationYetLocked.remove(userPackage);
                }
            }
            createPermissionReminderChannel(UserHandle.getUserHandleForUid(packageInfo.applicationInfo.uid));
            createNotificationForLocationUser(packageInfo);
        }
    }

    private List<UserPackage> getLocationUsersWithNoNotificationYetLocked(List<AppOpsManager.PackageOps> list) throws InterruptedException {
        UserPackage userPackage;
        AppPermissionGroup backgroundLocationGroup;
        ArrayList arrayList = new ArrayList();
        List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
        LocationManager locationManager = (LocationManager) this.mContext.getSystemService(LocationManager.class);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            AppOpsManager.PackageOps packageOps = list.get(i);
            String packageName = packageOps.getPackageName();
            if (!packageName.equals("android") && !locationManager.isProviderPackage(packageName)) {
                UserHandle userHandleForUid = UserHandle.getUserHandleForUid(packageOps.getUid());
                if (userProfiles.contains(userHandleForUid) && (backgroundLocationGroup = (userPackage = new UserPackage(this.mContext, packageName, userHandleForUid)).getBackgroundLocationGroup()) != null && backgroundLocationGroup.areRuntimePermissionsGranted() && backgroundLocationGroup.isUserSensitive() && (!userPackage.getLocationGroup().hasGrantedByDefaultPermission() || !backgroundLocationGroup.hasGrantedByDefaultPermission())) {
                    int size2 = packageOps.getOps().size();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size2) {
                            break;
                        }
                        AppOpsManager.OpEntry opEntry = (AppOpsManager.OpEntry) packageOps.getOps().get(i2);
                        String proxyPackageName = opEntry.getProxyPackageName();
                        if (proxyPackageName == null || proxyPackageName.equals("android") || locationManager.isProviderPackage(proxyPackageName)) {
                            long locationAccessCheckEnabledTime = getLocationAccessCheckEnabledTime();
                            if (locationAccessCheckEnabledTime >= 0 && opEntry.getLastAccessBackgroundTime(13) > locationAccessCheckEnabledTime) {
                                arrayList.add(userPackage);
                                break;
                            }
                        }
                        i2++;
                    }
                }
            }
        }
        ArraySet<UserPackage> loadAlreadyNotifiedPackagesLocked = loadAlreadyNotifiedPackagesLocked();
        throwInterruptedExceptionIfTaskIsCanceled();
        resetAlreadyNotifiedPackagesWithoutPermissionLocked(loadAlreadyNotifiedPackagesLocked);
        arrayList.removeAll(loadAlreadyNotifiedPackagesLocked);
        return arrayList;
    }

    private boolean checkLocationAccessCheckEnabledAndUpdateEnabledTime() {
        long locationAccessCheckEnabledTime = getLocationAccessCheckEnabledTime();
        if (Utils.isLocationAccessCheckEnabled()) {
            if (locationAccessCheckEnabledTime <= 0) {
                this.mSharedPrefs.edit().putLong("location_access_check_enabled_time", System.currentTimeMillis()).commit();
                return true;
            }
            return true;
        } else if (locationAccessCheckEnabledTime > 0) {
            this.mSharedPrefs.edit().remove("location_access_check_enabled_time").commit();
            return false;
        } else {
            return false;
        }
    }

    private long getLocationAccessCheckEnabledTime() {
        return this.mSharedPrefs.getLong("location_access_check_enabled_time", 0L);
    }

    private void createNotificationForLocationUser(PackageInfo packageInfo) {
        CharSequence applicationLabel = this.mPackageManager.getApplicationLabel(packageInfo.applicationInfo);
        Drawable applicationIcon = this.mPackageManager.getApplicationIcon(packageInfo.applicationInfo);
        Bitmap createBitmap = Bitmap.createBitmap(applicationIcon.getIntrinsicWidth(), applicationIcon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        applicationIcon.setBounds(0, 0, applicationIcon.getIntrinsicWidth(), applicationIcon.getIntrinsicHeight());
        applicationIcon.draw(canvas);
        String str = packageInfo.packageName;
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(packageInfo.applicationInfo.uid);
        NotificationManager notificationManager = (NotificationManager) Utils.getSystemServiceSafe(this.mContext, NotificationManager.class, userHandleForUid);
        long j = 0;
        while (j == 0) {
            j = new Random().nextLong();
        }
        Intent intent = new Intent(this.mContext, NotificationDeleteHandler.class);
        intent.putExtra("android.intent.extra.PACKAGE_NAME", str);
        intent.putExtra("com.android.packageinstaller.extra.SESSION_ID", j);
        intent.putExtra("android.intent.extra.UID", packageInfo.applicationInfo.uid);
        intent.putExtra("android.intent.extra.USER", userHandleForUid);
        intent.setFlags(268435456);
        Intent intent2 = new Intent(this.mContext, NotificationClickHandler.class);
        intent2.putExtra("android.intent.extra.PACKAGE_NAME", str);
        intent2.putExtra("com.android.packageinstaller.extra.SESSION_ID", j);
        intent2.putExtra("android.intent.extra.UID", packageInfo.applicationInfo.uid);
        intent2.putExtra("android.intent.extra.USER", userHandleForUid);
        intent2.setFlags(268435456);
        CharSequence notificationAppName = getNotificationAppName();
        Notification.Builder contentIntent = new Notification.Builder(this.mContext, "permission reminders").setContentTitle(this.mContext.getString(R.string.background_location_access_reminder_notification_title, applicationLabel)).setContentText(this.mContext.getString(R.string.background_location_access_reminder_notification_content)).setStyle(new Notification.BigTextStyle().bigText(this.mContext.getString(R.string.background_location_access_reminder_notification_content))).setSmallIcon(R.drawable.ic_pin_drop).setLargeIcon(createBitmap).setColor(this.mContext.getColor(17170460)).setAutoCancel(true).setDeleteIntent(PendingIntent.getBroadcast(this.mContext, 0, intent, 1207959552)).setContentIntent(PendingIntent.getBroadcast(this.mContext, 0, intent2, 1207959552));
        if (notificationAppName != null) {
            Bundle bundle = new Bundle();
            bundle.putString("android.substName", notificationAppName.toString());
            contentIntent.addExtras(bundle);
        }
        notificationManager.notify(str, 0, contentIntent.build());
        PermissionControllerStatsLog.write(214, j, packageInfo.applicationInfo.uid, str, 1);
        Log.v(LOG_TAG, "Location access check notification shown with sessionId=" + j + " uid=" + packageInfo.applicationInfo.uid + " pkgName=" + str);
        this.mSharedPrefs.edit().putLong("last_location_access_notification_shown", System.currentTimeMillis()).apply();
    }

    private CharSequence getNotificationAppName() {
        ResolveInfo resolveActivity = this.mPackageManager.resolveActivity(new Intent("android.settings.SETTINGS"), 0);
        if (resolveActivity == null) {
            return null;
        }
        return this.mPackageManager.getApplicationLabel(resolveActivity.activityInfo.applicationInfo);
    }

    private StatusBarNotification getCurrentlyShownNotificationLocked() {
        StatusBarNotification[] activeNotifications;
        List<UserHandle> userProfiles = this.mUserManager.getUserProfiles();
        int size = userProfiles.size();
        for (int i = 0; i < size; i++) {
            for (StatusBarNotification statusBarNotification : ((NotificationManager) Utils.getSystemServiceSafe(this.mContext, NotificationManager.class, userProfiles.get(i))).getActiveNotifications()) {
                if (statusBarNotification.getId() == 0) {
                    return statusBarNotification;
                }
            }
        }
        return null;
    }

    private void resetAlreadyNotifiedPackagesWithoutPermissionLocked(ArraySet<UserPackage> arraySet) throws InterruptedException {
        ArrayList arrayList = new ArrayList();
        Iterator<UserPackage> it = arraySet.iterator();
        while (it.hasNext()) {
            UserPackage next = it.next();
            throwInterruptedExceptionIfTaskIsCanceled();
            AppPermissionGroup backgroundLocationGroup = next.getBackgroundLocationGroup();
            if (backgroundLocationGroup == null || !backgroundLocationGroup.areRuntimePermissionsGranted()) {
                arrayList.add(next);
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        arraySet.removeAll(arrayList);
        safeAlreadyNotifiedPackagesLocked(arraySet);
        throwInterruptedExceptionIfTaskIsCanceled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forgetAboutPackage(String str, UserHandle userHandle) {
        synchronized (sLock) {
            StatusBarNotification currentlyShownNotificationLocked = getCurrentlyShownNotificationLocked();
            if (currentlyShownNotificationLocked != null && currentlyShownNotificationLocked.getUser().equals(userHandle) && currentlyShownNotificationLocked.getTag().equals(str)) {
                ((NotificationManager) Utils.getSystemServiceSafe(this.mContext, NotificationManager.class, userHandle)).cancel(str, 0);
            }
            ArraySet<UserPackage> loadAlreadyNotifiedPackagesLocked = loadAlreadyNotifiedPackagesLocked();
            loadAlreadyNotifiedPackagesLocked.remove(new UserPackage(this.mContext, str, userHandle));
            safeAlreadyNotifiedPackagesLocked(loadAlreadyNotifiedPackagesLocked);
        }
    }

    public void checkLocationAccessSoon() {
        int schedule = this.mJobScheduler.schedule(new JobInfo.Builder(1, new ComponentName(this.mContext, LocationAccessCheckJobService.class)).setMinimumLatency(getDelayMillis()).build());
        if (schedule != 1) {
            String str = LOG_TAG;
            Log.e(str, "Could not schedule location access check " + schedule);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isRunningInParentProfile() {
        UserHandle of = UserHandle.of(UserHandle.myUserId());
        UserHandle profileParent = this.mUserManager.getProfileParent(of);
        return profileParent == null || of.equals(profileParent);
    }

    /* loaded from: classes.dex */
    public static class SetupPeriodicBackgroundLocationAccessCheck extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int schedule;
            LocationAccessCheck locationAccessCheck = new LocationAccessCheck(context, null);
            JobScheduler jobScheduler = (JobScheduler) Utils.getSystemServiceSafe(context, JobScheduler.class);
            if (locationAccessCheck.isRunningInParentProfile() && jobScheduler.getPendingJob(0) == null && (schedule = jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(context, LocationAccessCheckJobService.class)).setPeriodic(locationAccessCheck.getPeriodicCheckIntervalMillis(), locationAccessCheck.getFlexForPeriodicCheckMillis()).build())) != 1) {
                String str = LocationAccessCheck.LOG_TAG;
                Log.e(str, "Could not schedule periodic location access check " + schedule);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class LocationAccessCheckJobService extends JobService {
        private AddLocationNotificationIfNeededTask mAddLocationNotificationIfNeededTask;
        private LocationAccessCheck mLocationAccessCheck;

        @Override // android.app.Service
        public void onCreate() {
            super.onCreate();
            this.mLocationAccessCheck = new LocationAccessCheck(this, new BooleanSupplier() { // from class: com.android.packageinstaller.permission.service.-$$Lambda$LocationAccessCheck$LocationAccessCheckJobService$4YZnwWzQ55U8Mg9l55GlUDxMaAw
                @Override // java.util.function.BooleanSupplier
                public final boolean getAsBoolean() {
                    return LocationAccessCheck.LocationAccessCheckJobService.this.lambda$onCreate$0$LocationAccessCheck$LocationAccessCheckJobService();
                }
            });
        }

        public /* synthetic */ boolean lambda$onCreate$0$LocationAccessCheck$LocationAccessCheckJobService() {
            boolean z;
            synchronized (LocationAccessCheck.sLock) {
                AddLocationNotificationIfNeededTask addLocationNotificationIfNeededTask = this.mAddLocationNotificationIfNeededTask;
                z = addLocationNotificationIfNeededTask != null && addLocationNotificationIfNeededTask.isCancelled();
            }
            return z;
        }

        @Override // android.app.job.JobService
        public boolean onStartJob(JobParameters jobParameters) {
            synchronized (LocationAccessCheck.sLock) {
                if (this.mAddLocationNotificationIfNeededTask != null) {
                    return false;
                }
                this.mAddLocationNotificationIfNeededTask = new AddLocationNotificationIfNeededTask();
                this.mAddLocationNotificationIfNeededTask.execute(jobParameters, this);
                return true;
            }
        }

        @Override // android.app.job.JobService
        public boolean onStopJob(JobParameters jobParameters) {
            synchronized (LocationAccessCheck.sLock) {
                if (this.mAddLocationNotificationIfNeededTask == null) {
                    return false;
                }
                AddLocationNotificationIfNeededTask addLocationNotificationIfNeededTask = this.mAddLocationNotificationIfNeededTask;
                addLocationNotificationIfNeededTask.cancel(false);
                try {
                    addLocationNotificationIfNeededTask.get();
                } catch (Exception e) {
                    String str = LocationAccessCheck.LOG_TAG;
                    Log.e(str, "While waiting for " + addLocationNotificationIfNeededTask + " to finish", e);
                }
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class AddLocationNotificationIfNeededTask extends AsyncTask<Object, Void, Void> {
            private AddLocationNotificationIfNeededTask() {
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public final Void doInBackground(Object... objArr) {
                LocationAccessCheckJobService.this.mLocationAccessCheck.addLocationNotificationIfNeeded((JobParameters) objArr[0], (LocationAccessCheckJobService) objArr[1]);
                return null;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class NotificationDeleteHandler extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String stringExtraSafe = Utils.getStringExtraSafe(intent, "android.intent.extra.PACKAGE_NAME");
            UserHandle userHandle = (UserHandle) Utils.getParcelableExtraSafe(intent, "android.intent.extra.USER");
            long longExtra = intent.getLongExtra("com.android.packageinstaller.extra.SESSION_ID", 0L);
            int intExtra = intent.getIntExtra("android.intent.extra.UID", 0);
            PermissionControllerStatsLog.write(214, longExtra, intExtra, stringExtraSafe, 2);
            String str = LocationAccessCheck.LOG_TAG;
            Log.v(str, "Location access check notification declined with sessionId=" + longExtra + " uid=" + intExtra + " pkgName=" + stringExtraSafe);
            new LocationAccessCheck(context, null).markAsNotified(stringExtraSafe, userHandle);
        }
    }

    /* loaded from: classes.dex */
    public static class NotificationClickHandler extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String stringExtraSafe = Utils.getStringExtraSafe(intent, "android.intent.extra.PACKAGE_NAME");
            UserHandle userHandle = (UserHandle) Utils.getParcelableExtraSafe(intent, "android.intent.extra.USER");
            int intExtra = intent.getIntExtra("android.intent.extra.UID", 0);
            long longExtra = intent.getLongExtra("com.android.packageinstaller.extra.SESSION_ID", 0L);
            new LocationAccessCheck(context, null).markAsNotified(stringExtraSafe, userHandle);
            PermissionControllerStatsLog.write(214, longExtra, intExtra, stringExtraSafe, 3);
            String str = LocationAccessCheck.LOG_TAG;
            Log.v(str, "Location access check notification clicked with sessionId=" + longExtra + " uid=" + intExtra + " pkgName=" + stringExtraSafe);
            Intent intent2 = new Intent(context, AppPermissionActivity.class);
            intent2.addFlags(402653184);
            intent2.putExtra("android.intent.extra.PERMISSION_NAME", "android.permission.ACCESS_FINE_LOCATION");
            intent2.putExtra("android.intent.extra.PACKAGE_NAME", stringExtraSafe);
            intent2.putExtra("android.intent.extra.USER", userHandle);
            intent2.putExtra("com.android.packageinstaller.extra.SESSION_ID", longExtra);
            context.startActivity(intent2);
        }
    }

    /* loaded from: classes.dex */
    public static class PackageResetHandler extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Objects.equals(action, "android.intent.action.PACKAGE_DATA_CLEARED") || Objects.equals(action, "android.intent.action.PACKAGE_FULLY_REMOVED")) {
                Uri data = intent.getData();
                Preconditions.checkNotNull(data);
                new LocationAccessCheck(context, null).forgetAboutPackage(data.getSchemeSpecificPart(), UserHandle.getUserHandleForUid(intent.getIntExtra("android.intent.extra.UID", 0)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class UserPackage {
        private final Context mContext;
        public final String pkg;
        public final UserHandle user;

        UserPackage(Context context, String str, UserHandle userHandle) {
            try {
                this.mContext = context.createPackageContextAsUser(context.getPackageName(), 0, userHandle);
                this.pkg = str;
                this.user = userHandle;
            } catch (PackageManager.NameNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }

        PackageInfo getPackageInfo() throws PackageManager.NameNotFoundException {
            return this.mContext.getPackageManager().getPackageInfo(this.pkg, 4096);
        }

        AppPermissionGroup getLocationGroup() {
            try {
                return AppPermissionGroup.create(this.mContext, getPackageInfo(), "android.permission.ACCESS_FINE_LOCATION", false);
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }

        AppPermissionGroup getBackgroundLocationGroup() {
            AppPermissionGroup locationGroup = getLocationGroup();
            if (locationGroup == null) {
                return null;
            }
            return locationGroup.getBackgroundPermissions();
        }

        public boolean equals(Object obj) {
            if (obj instanceof UserPackage) {
                UserPackage userPackage = (UserPackage) obj;
                return this.pkg.equals(userPackage.pkg) && this.user.equals(userPackage.user);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.pkg, this.user);
        }
    }
}
