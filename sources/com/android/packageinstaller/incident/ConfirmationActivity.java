package com.android.packageinstaller.incident;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IncidentManager;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.car.ui.R;
import com.android.packageinstaller.incident.ReportDetails;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class ConfirmationActivity extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private static ConfirmationActivity sCurrentActivity;
    private static Uri sCurrentUri;

    public static void finishCurrent() {
        ConfirmationActivity confirmationActivity = sCurrentActivity;
        if (confirmationActivity != null) {
            confirmationActivity.finish();
        }
    }

    public static Uri getCurrentUri() {
        return sCurrentUri;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Formatting formatting = new Formatting(this);
        Uri data = getIntent().getData();
        Log.d("ConfirmationActivity", "uri=" + data);
        if (data == null) {
            Log.w("ConfirmationActivity", "No uri in intent: " + getIntent());
            finish();
            return;
        }
        IncidentManager.PendingReport pendingReport = new IncidentManager.PendingReport(data);
        String appLabel = formatting.getAppLabel(pendingReport.getRequestingPackage());
        Resources resources = getResources();
        try {
            ReportDetails parseIncidentReport = ReportDetails.parseIncidentReport(this, data);
            View inflate = getLayoutInflater().inflate(R.layout.incident_confirmation, (ViewGroup) null);
            ArrayList<String> reasons = parseIncidentReport.getReasons();
            int size = reasons.size();
            if (size > 0) {
                inflate.findViewById(R.id.reasonIntro).setVisibility(0);
                TextView textView = (TextView) inflate.findViewById(R.id.reasons);
                textView.setVisibility(0);
                int dimension = (int) (resources.getDimension(R.dimen.incident_reason_bullet_size) + 0.5f);
                int dimension2 = (int) (resources.getDimension(R.dimen.incident_reason_bullet_indent) + 0.5f);
                int color = getColor(R.color.incident_reason_bullet_color);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    sb.append(reasons.get(i));
                    if (i != size - 1) {
                        sb.append("\n");
                    }
                }
                SpannableString spannableString = new SpannableString(sb.toString());
                int i2 = 0;
                int i3 = 0;
                while (i2 < size) {
                    int length = reasons.get(i2).length();
                    spannableString.setSpan(new BulletSpan(dimension2, color, dimension), i3, i3 + length, 33);
                    i3 += length + 1;
                    i2++;
                    reasons = reasons;
                    size = size;
                    dimension = dimension;
                }
                textView.setText(spannableString);
            }
            ((TextView) inflate.findViewById(R.id.message)).setText(getString(R.string.incident_report_dialog_text, new Object[]{appLabel, formatting.getDate(pendingReport.getTimestamp()), formatting.getTime(pendingReport.getTimestamp()), appLabel}));
            ArrayList<Drawable> images = parseIncidentReport.getImages();
            int size2 = images.size();
            if (size2 > 0) {
                inflate.findViewById(R.id.imageScrollView).setVisibility(0);
                LinearLayout linearLayout = (LinearLayout) inflate.findViewById(R.id.imageList);
                int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.incident_image_width);
                int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.incident_image_height);
                for (int i4 = 0; i4 < size2; i4++) {
                    images.get(i4);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageDrawable(images.get(i4));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    linearLayout.addView(imageView, new LinearLayout.LayoutParams(dimensionPixelSize, dimensionPixelSize2));
                }
            }
            new AlertDialog.Builder(this).setTitle(R.string.incident_report_dialog_title).setPositiveButton(R.string.incident_report_dialog_allow_label, this).setNegativeButton(R.string.incident_report_dialog_deny_label, this).setOnDismissListener(this).setView(inflate).show();
        } catch (ReportDetails.ParseException e) {
            Log.w("Rejecting report because it couldn't be parsed", e);
            ((IncidentManager) getSystemService(IncidentManager.class)).denyReport(getIntent().getData());
            new AlertDialog.Builder(this).setTitle(R.string.incident_report_dialog_title).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.incident.ConfirmationActivity.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i5) {
                    ConfirmationActivity.this.finish();
                }
            }).setMessage(getString(R.string.incident_report_error_dialog_text, new Object[]{appLabel})).setOnDismissListener(this).show();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        sCurrentActivity = this;
        sCurrentUri = getIntent().getData();
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        sCurrentActivity = null;
        sCurrentUri = null;
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        IncidentManager incidentManager = (IncidentManager) getSystemService(IncidentManager.class);
        if (i == -2) {
            incidentManager.denyReport(getIntent().getData());
            PendingList.getInstance().updateState(this, 0);
        } else if (i == -1) {
            incidentManager.approveReport(getIntent().getData());
            PendingList.getInstance().updateState(this, 0);
        }
        finish();
    }
}
