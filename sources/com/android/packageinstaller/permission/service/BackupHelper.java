package com.android.packageinstaller.permission.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.UserHandle;
import android.permission.PermissionManager;
import android.util.Log;
import android.util.Xml;
import androidx.core.os.BuildCompat;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import com.android.packageinstaller.permission.model.AppPermissions;
import com.android.packageinstaller.permission.model.Permission;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class BackupHelper {
    private static final String LOG_TAG = "BackupHelper";
    private static final Object sLock = new Object();
    private final Context mContext;

    public BackupHelper(Context context, UserHandle userHandle) {
        try {
            this.mContext = context.createPackageContextAsUser(context.getPackageName(), 0, userHandle);
        } catch (PackageManager.NameNotFoundException unused) {
            throw new IllegalStateException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void skipToEndOfTag(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        int i = 1;
        while (i > 0) {
            int next = xmlPullParser.next();
            if (next == 2) {
                i++;
            } else if (next == 3) {
                i--;
            }
        }
    }

    private void skipToTag(XmlPullParser xmlPullParser, String str) throws IOException, XmlPullParserException {
        int next;
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals(str)) {
                    return;
                }
                skipToEndOfTag(xmlPullParser);
                return;
            }
        } while (next != 1);
    }

    private ArrayList<BackupPackageState> parseFromXml(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        int i;
        int next;
        ArrayList<BackupPackageState> arrayList = new ArrayList<>();
        skipToTag(xmlPullParser, "perm-grant-backup");
        try {
            i = Integer.parseInt(xmlPullParser.getAttributeValue(null, "version"));
        } catch (NumberFormatException unused) {
            i = 28;
        }
        skipToTag(xmlPullParser, "rt-grants");
        if (xmlPullParser.getEventType() == 2 || xmlPullParser.getName().equals("rt-grants")) {
            do {
                next = xmlPullParser.next();
                if (next == 2) {
                    String name = xmlPullParser.getName();
                    char c = 65535;
                    if (name.hashCode() == 98615580 && name.equals("grant")) {
                        c = 0;
                    }
                    if (c == 0) {
                        try {
                            arrayList.add(BackupPackageState.parseFromXml(xmlPullParser, this.mContext, i));
                        } catch (XmlPullParserException e) {
                            Log.e(LOG_TAG, "Could not parse permissions ", e);
                            skipToEndOfTag(xmlPullParser);
                        }
                    } else {
                        String str = LOG_TAG;
                        Log.w(str, "Found unexpected tag " + xmlPullParser.getName() + " during restore");
                        skipToEndOfTag(xmlPullParser);
                    }
                }
            } while (next != 1);
            return arrayList;
        }
        throw new XmlPullParserException("Could not find perm-grant-backup > rt-grants");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restoreState(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        ArrayList<BackupPackageState> parseFromXml = parseFromXml(xmlPullParser);
        ArrayList<BackupPackageState> arrayList = new ArrayList<>();
        int size = parseFromXml.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                BackupPackageState backupPackageState = parseFromXml.get(i);
                try {
                    backupPackageState.restore(this.mContext, this.mContext.getPackageManager().getPackageInfo(backupPackageState.mPackageName, 4096));
                } catch (PackageManager.NameNotFoundException unused) {
                    arrayList.add(backupPackageState);
                }
            }
        }
        synchronized (sLock) {
            writeDelayedStorePkgsLocked(arrayList);
        }
    }

    private static void writePkgsAsXml(XmlSerializer xmlSerializer, ArrayList<BackupPackageState> arrayList) throws IOException {
        xmlSerializer.startDocument(null, true);
        xmlSerializer.startTag(null, "perm-grant-backup");
        if (BuildCompat.isAtLeastQ()) {
            Integer num = 29;
            xmlSerializer.attribute(null, "version", num.toString());
        } else {
            xmlSerializer.attribute(null, "version", Integer.valueOf(Build.VERSION.SDK_INT).toString());
        }
        xmlSerializer.startTag(null, "rt-grants");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            BackupPackageState backupPackageState = arrayList.get(i);
            if (backupPackageState != null) {
                backupPackageState.writeAsXml(xmlSerializer);
            }
        }
        xmlSerializer.endTag(null, "rt-grants");
        xmlSerializer.endTag(null, "perm-grant-backup");
        xmlSerializer.endDocument();
    }

    private void writeDelayedStorePkgsLocked(ArrayList<BackupPackageState> arrayList) {
        try {
            FileOutputStream openFileOutput = this.mContext.openFileOutput("delayed_restore_permissions.xml", 0);
            XmlSerializer newSerializer = Xml.newSerializer();
            newSerializer.setOutput(openFileOutput, StandardCharsets.UTF_8.name());
            writePkgsAsXml(newSerializer, arrayList);
            newSerializer.flush();
            if (openFileOutput != null) {
                $closeResource(null, openFileOutput);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not remember which packages still need to be restored", e);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeState(XmlSerializer xmlSerializer) throws IOException {
        List<PackageInfo> installedPackages = this.mContext.getPackageManager().getInstalledPackages(4096);
        ArrayList arrayList = new ArrayList();
        int size = installedPackages.size();
        for (int i = 0; i < size; i++) {
            BackupPackageState fromAppPermissions = BackupPackageState.fromAppPermissions(this.mContext, installedPackages.get(i));
            if (fromAppPermissions != null) {
                arrayList.add(fromAppPermissions);
            }
        }
        writePkgsAsXml(xmlSerializer, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean restoreDelayedState(String str) {
        synchronized (sLock) {
            try {
                FileInputStream openFileInput = this.mContext.openFileInput("delayed_restore_permissions.xml");
                try {
                    XmlPullParser newPullParser = Xml.newPullParser();
                    newPullParser.setInput(openFileInput, StandardCharsets.UTF_8.name());
                    ArrayList<BackupPackageState> parseFromXml = parseFromXml(newPullParser);
                    PackageInfo packageInfo = null;
                    if (openFileInput != null) {
                        $closeResource(null, openFileInput);
                    }
                    try {
                        packageInfo = this.mContext.getPackageManager().getPackageInfo(str, 4096);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(LOG_TAG, "Could not restore delayed permissions for " + str, e);
                    }
                    if (packageInfo != null) {
                        int size = parseFromXml.size();
                        int i = 0;
                        while (true) {
                            if (i >= size) {
                                break;
                            }
                            BackupPackageState backupPackageState = parseFromXml.get(i);
                            if (backupPackageState.mPackageName.equals(str)) {
                                backupPackageState.restore(this.mContext, packageInfo);
                                parseFromXml.remove(i);
                                writeDelayedStorePkgsLocked(parseFromXml);
                                break;
                            }
                            i++;
                        }
                    }
                    return parseFromXml.size() > 0;
                } finally {
                }
            } catch (IOException | XmlPullParserException e2) {
                Log.e(LOG_TAG, "Could not parse delayed permissions", e2);
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BackupPermissionState {
        private final boolean mIsGranted;
        private final boolean mIsUserFixed;
        private final boolean mIsUserSet;
        private final String mPermissionName;
        private final boolean mWasReviewed;

        private BackupPermissionState(String str, boolean z, boolean z2, boolean z3, boolean z4) {
            this.mPermissionName = str;
            this.mIsGranted = z;
            this.mIsUserSet = z2;
            this.mIsUserFixed = z3;
            this.mWasReviewed = z4;
        }

        static List<BackupPermissionState> parseFromXml(XmlPullParser xmlPullParser, Context context, int i) throws XmlPullParserException {
            String attributeValue = xmlPullParser.getAttributeValue(null, "name");
            if (attributeValue == null) {
                throw new XmlPullParserException("Found perm without name");
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(attributeValue);
            List splitPermissions = ((PermissionManager) context.getSystemService(PermissionManager.class)).getSplitPermissions();
            int size = splitPermissions.size();
            for (int i2 = 0; i2 < size; i2++) {
                PermissionManager.SplitPermissionInfo splitPermissionInfo = (PermissionManager.SplitPermissionInfo) splitPermissions.get(i2);
                if (i < splitPermissionInfo.getTargetSdk() && attributeValue.equals(splitPermissionInfo.getSplitPermission())) {
                    arrayList.addAll(splitPermissionInfo.getNewPermissions());
                }
            }
            ArrayList arrayList2 = new ArrayList(arrayList.size());
            int size2 = arrayList.size();
            for (int i3 = 0; i3 < size2; i3++) {
                arrayList2.add(new BackupPermissionState((String) arrayList.get(i3), "true".equals(xmlPullParser.getAttributeValue(null, "g")), "true".equals(xmlPullParser.getAttributeValue(null, "set")), "true".equals(xmlPullParser.getAttributeValue(null, "fixed")), "true".equals(xmlPullParser.getAttributeValue(null, "was-reviewed"))));
            }
            return arrayList2;
        }

        private static boolean isPermGrantedIncludingAppOp(Permission permission) {
            return permission.isGranted() && (!permission.affectsAppOp() || permission.isAppOpAllowed());
        }

        private static BackupPermissionState fromPermission(Permission permission, boolean z) {
            boolean z2;
            boolean z3;
            if ((permission.getFlags() & 20) != 0) {
                return null;
            }
            if (permission.isUserSet() || !permission.isGrantedByDefault()) {
                if (z) {
                    z2 = isPermGrantedIncludingAppOp(permission);
                    z3 = false;
                } else {
                    z2 = !isPermGrantedIncludingAppOp(permission);
                    z3 = !permission.isReviewRequired();
                }
                boolean z4 = z3;
                if (z2 || permission.isUserSet() || permission.isUserFixed() || z4) {
                    return new BackupPermissionState(permission.getName(), isPermGrantedIncludingAppOp(permission), permission.isUserSet(), permission.isUserFixed(), z4);
                }
                return null;
            }
            return null;
        }

        static ArrayList<BackupPermissionState> fromPermissionGroup(AppPermissionGroup appPermissionGroup) {
            ArrayList<BackupPermissionState> arrayList = new ArrayList<>();
            ArrayList<Permission> permissions = appPermissionGroup.getPermissions();
            boolean z = appPermissionGroup.getApp().applicationInfo.targetSdkVersion >= 23;
            int size = permissions.size();
            for (int i = 0; i < size; i++) {
                BackupPermissionState fromPermission = fromPermission(permissions.get(i), z);
                if (fromPermission != null) {
                    arrayList.add(fromPermission);
                }
            }
            return arrayList;
        }

        void writeAsXml(XmlSerializer xmlSerializer) throws IOException {
            xmlSerializer.startTag(null, "perm");
            xmlSerializer.attribute(null, "name", this.mPermissionName);
            if (this.mIsGranted) {
                xmlSerializer.attribute(null, "g", "true");
            }
            if (this.mIsUserSet) {
                xmlSerializer.attribute(null, "set", "true");
            }
            if (this.mIsUserFixed) {
                xmlSerializer.attribute(null, "fixed", "true");
            }
            if (this.mWasReviewed) {
                xmlSerializer.attribute(null, "was-reviewed", "true");
            }
            xmlSerializer.endTag(null, "perm");
        }

        void restore(AppPermissions appPermissions, boolean z) {
            AppPermissionGroup groupForPermission = appPermissions.getGroupForPermission(this.mPermissionName);
            if (groupForPermission == null) {
                String str = BackupHelper.LOG_TAG;
                Log.w(str, "Could not find group for " + this.mPermissionName + " in " + appPermissions.getPackageInfo().packageName);
            } else if (z != groupForPermission.isBackgroundGroup()) {
            } else {
                Permission permission = groupForPermission.getPermission(this.mPermissionName);
                if (this.mWasReviewed) {
                    permission.unsetReviewRequired();
                }
                if (groupForPermission.isSystemFixed() || groupForPermission.isPolicyFixed() || permission.isUserSet()) {
                    return;
                }
                if (this.mIsGranted) {
                    groupForPermission.grantRuntimePermissions(this.mIsUserFixed, new String[]{this.mPermissionName});
                } else {
                    groupForPermission.revokeRuntimePermissions(this.mIsUserFixed, new String[]{this.mPermissionName});
                }
                permission.setUserSet(this.mIsUserSet);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class BackupPackageState {
        final String mPackageName;
        private final ArrayList<BackupPermissionState> mPermissionsToRestore;

        private BackupPackageState(String str, ArrayList<BackupPermissionState> arrayList) {
            this.mPackageName = str;
            this.mPermissionsToRestore = arrayList;
        }

        static BackupPackageState parseFromXml(XmlPullParser xmlPullParser, Context context, int i) throws IOException, XmlPullParserException {
            String attributeValue = xmlPullParser.getAttributeValue(null, "pkg");
            if (attributeValue == null) {
                throw new XmlPullParserException("Found grant without pkg");
            }
            ArrayList arrayList = new ArrayList();
            while (true) {
                int next = xmlPullParser.next();
                if (next == 1) {
                    throw new XmlPullParserException("Could not parse state for " + attributeValue);
                } else if (next == 2) {
                    String name = xmlPullParser.getName();
                    char c = 65535;
                    if (name.hashCode() == 3437296 && name.equals("perm")) {
                        c = 0;
                    }
                    if (c != 0) {
                        String str = BackupHelper.LOG_TAG;
                        Log.w(str, "Found unexpected tag " + xmlPullParser.getName() + " while restoring " + attributeValue);
                        BackupHelper.skipToEndOfTag(xmlPullParser);
                    } else {
                        try {
                            arrayList.addAll(BackupPermissionState.parseFromXml(xmlPullParser, context, i));
                        } catch (XmlPullParserException e) {
                            String str2 = BackupHelper.LOG_TAG;
                            Log.e(str2, "Could not parse permission for " + attributeValue, e);
                        }
                        BackupHelper.skipToEndOfTag(xmlPullParser);
                    }
                } else if (next == 3) {
                    return new BackupPackageState(attributeValue, arrayList);
                }
            }
        }

        static BackupPackageState fromAppPermissions(Context context, PackageInfo packageInfo) {
            AppPermissions appPermissions = new AppPermissions(context, packageInfo, false, null);
            ArrayList arrayList = new ArrayList();
            List<AppPermissionGroup> permissionGroups = appPermissions.getPermissionGroups();
            int size = permissionGroups.size();
            for (int i = 0; i < size; i++) {
                AppPermissionGroup appPermissionGroup = permissionGroups.get(i);
                arrayList.addAll(BackupPermissionState.fromPermissionGroup(appPermissionGroup));
                if (appPermissionGroup.getBackgroundPermissions() != null) {
                    arrayList.addAll(BackupPermissionState.fromPermissionGroup(appPermissionGroup.getBackgroundPermissions()));
                }
            }
            if (arrayList.size() == 0) {
                return null;
            }
            return new BackupPackageState(packageInfo.packageName, arrayList);
        }

        void writeAsXml(XmlSerializer xmlSerializer) throws IOException {
            if (this.mPermissionsToRestore.size() == 0) {
                return;
            }
            xmlSerializer.startTag(null, "grant");
            xmlSerializer.attribute(null, "pkg", this.mPackageName);
            int size = this.mPermissionsToRestore.size();
            for (int i = 0; i < size; i++) {
                this.mPermissionsToRestore.get(i).writeAsXml(xmlSerializer);
            }
            xmlSerializer.endTag(null, "grant");
        }

        void restore(Context context, PackageInfo packageInfo) {
            AppPermissions appPermissions = new AppPermissions(context, packageInfo, false, true, null);
            int size = this.mPermissionsToRestore.size();
            for (int i = 0; i < size; i++) {
                this.mPermissionsToRestore.get(i).restore(appPermissions, false);
            }
            for (int i2 = 0; i2 < size; i2++) {
                this.mPermissionsToRestore.get(i2).restore(appPermissions, true);
            }
            int size2 = appPermissions.getPermissionGroups().size();
            for (int i3 = 0; i3 < size2; i3++) {
                AppPermissionGroup appPermissionGroup = appPermissions.getPermissionGroups().get(i3);
                if (appPermissionGroup.areRuntimePermissionsGranted()) {
                    appPermissionGroup.setUserFixed(false);
                }
                AppPermissionGroup backgroundPermissions = appPermissionGroup.getBackgroundPermissions();
                if (backgroundPermissions != null && backgroundPermissions.areRuntimePermissionsGranted()) {
                    backgroundPermissions.setUserFixed(false);
                }
            }
            appPermissions.persistChanges(true);
        }
    }
}
