package com.android.packageinstaller.incident;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IncidentManager;
/* loaded from: classes.dex */
public class ApprovalReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            IncidentManager incidentManager = (IncidentManager) context.getSystemService(IncidentManager.class);
            if ("com.android.packageinstaller.incident.APPROVE".equals(intent.getAction())) {
                incidentManager.approveReport(data);
            } else if ("com.android.packageinstaller.incident.DENY".equals(intent.getAction())) {
                incidentManager.denyReport(data);
            }
        }
        PendingList.getInstance().updateState(context, 1);
    }
}
