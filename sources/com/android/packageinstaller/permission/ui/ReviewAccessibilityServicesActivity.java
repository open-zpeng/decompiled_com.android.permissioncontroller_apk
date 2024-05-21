package com.android.packageinstaller.permission.ui;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.text.BidiFormatter;
import androidx.fragment.app.FragmentActivity;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.utils.Utils;
import java.util.List;
/* loaded from: classes.dex */
public final class ReviewAccessibilityServicesActivity extends FragmentActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        final List<AccessibilityServiceInfo> enabledAccessibilityServiceList = ((AccessibilityManager) getSystemService(AccessibilityManager.class)).getEnabledAccessibilityServiceList(-1);
        new AlertDialog.Builder(this).setView(createDialogView(enabledAccessibilityServiceList)).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null).setNeutralButton(R.string.settings, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.-$$Lambda$ReviewAccessibilityServicesActivity$WPVgZWA6P5zCWAg18Rz7ojbtnVE
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ReviewAccessibilityServicesActivity.this.lambda$onCreate$0$ReviewAccessibilityServicesActivity(enabledAccessibilityServiceList, dialogInterface, i);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.packageinstaller.permission.ui.-$$Lambda$ReviewAccessibilityServicesActivity$9oyDA3imdzYx7dvsFmNRMZzEMfI
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                ReviewAccessibilityServicesActivity.this.lambda$onCreate$1$ReviewAccessibilityServicesActivity(dialogInterface);
            }
        }).show();
    }

    public /* synthetic */ void lambda$onCreate$0$ReviewAccessibilityServicesActivity(List list, DialogInterface dialogInterface, int i) {
        if (list.size() == 1) {
            startAccessibilityScreen(((AccessibilityServiceInfo) list.get(0)).getResolveInfo().serviceInfo);
        } else {
            startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
        }
    }

    public /* synthetic */ void lambda$onCreate$1$ReviewAccessibilityServicesActivity(DialogInterface dialogInterface) {
        finish();
    }

    private View createDialogView(List<AccessibilityServiceInfo> list) {
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(AppOpsManager.class);
        LayoutInflater from = LayoutInflater.from(this);
        View inflate = from.inflate(R.layout.accessibility_service_dialog, (ViewGroup) null);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ResolveInfo resolveInfo = list.get(i).getResolveInfo();
            final ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            ApplicationInfo applicationInfo = serviceInfo.applicationInfo;
            CharSequence label = getLabel(resolveInfo);
            long lastAccessTime = getLastAccessTime(applicationInfo, appOpsManager);
            if (size == 1) {
                ((TextView) inflate.requireViewById(R.id.title)).setText(getString(R.string.accessibility_service_dialog_title_single, new Object[]{label}));
                ((TextView) inflate.requireViewById(R.id.bottom_text)).setText(getString(R.string.accessibility_service_dialog_bottom_text_single, new Object[]{label}));
                ImageView imageView = (ImageView) inflate.requireViewById(R.id.header_icon);
                imageView.setImageDrawable(Utils.getBadgedIcon(this, applicationInfo));
                imageView.setVisibility(0);
                if (lastAccessTime != 0) {
                    TextView textView = (TextView) inflate.requireViewById(R.id.middle_text);
                    textView.setText(getString(R.string.app_permission_most_recent_summary, new Object[]{Utils.getAbsoluteTimeString(this, lastAccessTime)}));
                    textView.setVisibility(0);
                }
            } else {
                ((TextView) inflate.requireViewById(R.id.title)).setText(getString(R.string.accessibility_service_dialog_title_multiple, new Object[]{Integer.valueOf(list.size())}));
                ((TextView) inflate.requireViewById(R.id.bottom_text)).setText(getString(R.string.accessibility_service_dialog_bottom_text_multiple));
                ViewGroup viewGroup = (ViewGroup) inflate.requireViewById(R.id.items_container);
                View inflate2 = from.inflate(R.layout.accessibility_service_dialog_item, viewGroup, false);
                ((TextView) inflate2.requireViewById(R.id.title)).setText(label);
                ((ImageView) inflate2.requireViewById(R.id.icon)).setImageDrawable(Utils.getBadgedIcon(this, applicationInfo));
                if (lastAccessTime == 0) {
                    inflate2.requireViewById(R.id.summary).setVisibility(8);
                } else {
                    ((TextView) inflate2.requireViewById(R.id.summary)).setText(getString(R.string.app_permission_most_recent_summary, new Object[]{Utils.getAbsoluteTimeString(this, lastAccessTime)}));
                }
                inflate2.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.-$$Lambda$ReviewAccessibilityServicesActivity$6wtswzl_quUnCLpMKn49zOgn8Pc
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ReviewAccessibilityServicesActivity.this.lambda$createDialogView$2$ReviewAccessibilityServicesActivity(serviceInfo, view);
                    }
                });
                viewGroup.addView(inflate2);
            }
        }
        return inflate;
    }

    public /* synthetic */ void lambda$createDialogView$2$ReviewAccessibilityServicesActivity(ServiceInfo serviceInfo, View view) {
        startAccessibilityScreen(serviceInfo);
    }

    private void startAccessibilityScreen(ServiceInfo serviceInfo) {
        Intent intent = new Intent("android.settings.ACCESSIBILITY_DETAILS_SETTINGS");
        intent.putExtra("android.intent.extra.COMPONENT_NAME", new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToString());
        startActivity(intent);
    }

    private CharSequence getLabel(ResolveInfo resolveInfo) {
        return BidiFormatter.getInstance().unicodeWrap(TextUtils.makeSafeForPresentation(resolveInfo.loadLabel(getPackageManager()).toString(), 0, 0.0f, 5));
    }

    private static long getLastAccessTime(ApplicationInfo applicationInfo, AppOpsManager appOpsManager) {
        List opsForPackage = appOpsManager.getOpsForPackage(applicationInfo.uid, applicationInfo.packageName, new String[]{"android:access_accessibility"});
        int size = opsForPackage.size();
        long j = 0;
        int i = 0;
        while (i < size) {
            AppOpsManager.PackageOps packageOps = (AppOpsManager.PackageOps) opsForPackage.get(i);
            int size2 = packageOps.getOps().size();
            long j2 = j;
            for (int i2 = 0; i2 < size2; i2++) {
                j2 = Math.max(j2, ((AppOpsManager.OpEntry) packageOps.getOps().get(i2)).getLastAccessTime(13));
            }
            i++;
            j = j2;
        }
        return j;
    }
}
