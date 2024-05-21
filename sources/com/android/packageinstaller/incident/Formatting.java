package com.android.packageinstaller.incident;

import android.content.Context;
import android.content.pm.PackageManager;
import com.android.packageinstaller.permission.utils.Utils;
import java.text.DateFormat;
import java.util.Date;
/* loaded from: classes.dex */
public class Formatting {
    private final Context mContext;
    private final DateFormat mDateFormat;
    private final PackageManager mPm;
    private final DateFormat mTimeFormat;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Formatting(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mDateFormat = android.text.format.DateFormat.getDateFormat(context);
        this.mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getAppLabel(String str) {
        try {
            return Utils.getAppLabel(this.mPm.getApplicationInfo(str, 0), this.mContext);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getDate(long j) {
        return this.mDateFormat.format(new Date(j));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getTime(long j) {
        return this.mTimeFormat.format(new Date(j));
    }
}
