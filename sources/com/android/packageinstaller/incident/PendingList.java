package com.android.packageinstaller.incident;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IncidentManager;
import android.util.ArraySet;
import android.util.Log;
import com.android.car.ui.R;
import com.android.packageinstaller.incident.PendingList;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class PendingList {
    private static final PendingList sInstance = new PendingList();
    private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Rec {
        public final String label;
        public final IncidentManager.PendingReport report;

        Rec(IncidentManager.PendingReport pendingReport, String str) {
            this.report = pendingReport;
            this.label = str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Updater {
        private Collator mCollator;
        private final Context mContext;
        private final int mFlags;
        private final Formatting mFormatting;
        private final NotificationManager mNm;

        Updater(Context context, int i) {
            this.mContext = context;
            this.mFlags = i;
            this.mNm = (NotificationManager) context.getSystemService(NotificationManager.class);
            this.mFormatting = new Formatting(context);
            this.mCollator = Collator.getInstance(context.getResources().getConfiguration().getLocales().get(0));
        }

        void updateState() {
            IncidentManager incidentManager = (IncidentManager) this.mContext.getSystemService(IncidentManager.class);
            List pendingReports = incidentManager.getPendingReports();
            SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("com.android.packageinstaller.incident.PendingList", 0);
            Set<String> stringSet = sharedPreferences.getStringSet("notifications", null);
            ArraySet arraySet = new ArraySet();
            if (stringSet != null) {
                for (String str : stringSet) {
                    arraySet.add(str);
                }
            }
            ArraySet arraySet2 = new ArraySet();
            ArrayList arrayList = new ArrayList();
            int size = pendingReports.size();
            for (int i = 0; i < size; i++) {
                IncidentManager.PendingReport pendingReport = (IncidentManager.PendingReport) pendingReports.get(i);
                String appLabel = this.mFormatting.getAppLabel(pendingReport.getRequestingPackage());
                if (appLabel == null) {
                    Log.w("PermissionController.incident", "Application (or its label) could not be found. Summarily  denying report: " + pendingReport.getRequestingPackage());
                    incidentManager.denyReport(pendingReport.getUri());
                } else {
                    arrayList.add(new Rec(pendingReport, appLabel));
                }
            }
            arrayList.sort(new Comparator() { // from class: com.android.packageinstaller.incident.-$$Lambda$PendingList$Updater$1Nth6kHCyVmn9O9SIfhSVq_1JGM
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return PendingList.Updater.this.lambda$updateState$0$PendingList$Updater((PendingList.Rec) obj, (PendingList.Rec) obj2);
                }
            });
            ArrayList arrayList2 = new ArrayList();
            int size2 = arrayList.size();
            Rec rec = null;
            for (int i2 = 0; i2 < size2; i2++) {
                Rec rec2 = (Rec) arrayList.get(i2);
                arrayList2.add(rec2);
                String uri = rec2.report.getUri().toString();
                arraySet.remove(uri);
                arraySet2.add(uri);
                if ((rec2.report.getFlags() & 1) != 0 && rec == null) {
                    rec = rec2;
                }
            }
            showNotifications(arrayList2);
            int size3 = arraySet.size();
            for (int i3 = 0; i3 < size3; i3++) {
                this.mNm.cancel((String) arraySet.valueAt(i3), 66900652);
            }
            if (rec != null) {
                if (!rec.report.getUri().equals(ConfirmationActivity.getCurrentUri()) && (this.mFlags & 1) == 0) {
                    this.mContext.startActivity(newDialogIntent(rec));
                }
            } else {
                ConfirmationActivity.finishCurrent();
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putStringSet("notifications", arraySet2);
            edit.apply();
        }

        public /* synthetic */ int lambda$updateState$0$PendingList$Updater(Rec rec, Rec rec2) {
            int i = ((rec.report.getTimestamp() - rec2.report.getTimestamp()) > 0L ? 1 : ((rec.report.getTimestamp() - rec2.report.getTimestamp()) == 0L ? 0 : -1));
            if (i == 0) {
                return this.mCollator.compare(rec.label, rec2.label);
            }
            return i < 0 ? -1 : 1;
        }

        private void showNotifications(List<Rec> list) {
            createNotificationChannel();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                Rec rec = list.get(i);
                this.mNm.notify(rec.report.getUri().toString(), 66900652, new Notification.Builder(this.mContext).setStyle(new Notification.BigTextStyle()).setContentTitle(this.mContext.getString(R.string.incident_report_notification_title)).setContentText(this.mContext.getString(R.string.incident_report_notification_text, rec.label)).setSmallIcon(R.drawable.ic_bug_report_black_24dp).setWhen(rec.report.getTimestamp()).setGroup("incident confirmation").setChannelId("incident_confirmation").setSortKey(getSortKey(rec.report.getTimestamp())).setContentIntent(PendingIntent.getActivity(this.mContext, 0, newDialogIntent(rec), 0)).setDeleteIntent(PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.packageinstaller.incident.DENY", rec.report.getUri(), this.mContext, ApprovalReceiver.class), 0)).setColor(this.mContext.getColor(17170460)).build());
            }
        }

        private void createNotificationChannel() {
            this.mNm.createNotificationChannel(new NotificationChannel("incident_confirmation", this.mContext.getString(R.string.incident_report_channel_name), 3));
        }

        private String getSortKey(long j) {
            return PendingList.sDateFormatter.format(new Date(j));
        }

        private Intent newDialogIntent(Rec rec) {
            Intent intent = new Intent("android.intent.action.MAIN", rec.report.getUri(), this.mContext, ConfirmationActivity.class);
            intent.setFlags(268468224);
            return intent;
        }
    }

    public static PendingList getInstance() {
        return sInstance;
    }

    private PendingList() {
    }

    public void updateState(Context context, int i) {
        new Updater(context, i).updateState();
    }
}
