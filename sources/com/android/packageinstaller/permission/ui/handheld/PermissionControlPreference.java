package com.android.packageinstaller.permission.ui.handheld;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.model.AppPermissionGroup;
import java.util.List;
/* loaded from: classes.dex */
public class PermissionControlPreference extends Preference {
    private final Context mContext;
    private boolean mEllipsizeEnd;
    private List<Integer> mSummaryIcons;
    private List<Integer> mTitleIcons;
    private boolean mUseSmallerIcon;
    private Drawable mWidgetIcon;

    public PermissionControlPreference(Context context, AppPermissionGroup appPermissionGroup, String str) {
        this(context, appPermissionGroup, str, 0L);
    }

    public PermissionControlPreference(final Context context, final AppPermissionGroup appPermissionGroup, final String str, final long j) {
        super(context);
        this.mContext = context;
        this.mWidgetIcon = null;
        this.mUseSmallerIcon = false;
        this.mEllipsizeEnd = false;
        this.mTitleIcons = null;
        this.mSummaryIcons = null;
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$PermissionControlPreference$s5jYJOT5Qe1JdfAtzRz5VXi-Kzw
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return PermissionControlPreference.lambda$new$0(AppPermissionGroup.this, str, j, context, preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$new$0(AppPermissionGroup appPermissionGroup, String str, long j, Context context, Preference preference) {
        Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSION");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", appPermissionGroup.getApp().packageName);
        intent.putExtra("android.intent.extra.PERMISSION_NAME", appPermissionGroup.getPermissions().get(0).getName());
        intent.putExtra("android.intent.extra.USER", appPermissionGroup.getUser());
        intent.putExtra("com.android.packageinstaller.extra.CALLER_NAME", str);
        intent.putExtra("com.android.packageinstaller.extra.SESSION_ID", j);
        context.startActivity(intent);
        return true;
    }

    public void useSmallerIcon() {
        this.mUseSmallerIcon = true;
    }

    public void setEllipsizeEnd() {
        this.mEllipsizeEnd = true;
    }

    public void setGroupSummary(AppPermissionGroup appPermissionGroup) {
        AppPermissionGroup backgroundPermissions;
        if (appPermissionGroup.hasPermissionWithBackgroundMode() && appPermissionGroup.areRuntimePermissionsGranted() && ((backgroundPermissions = appPermissionGroup.getBackgroundPermissions()) == null || !backgroundPermissions.areRuntimePermissionsGranted())) {
            setSummary(R.string.permission_subtitle_only_in_foreground);
        } else {
            setSummary("");
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        if (this.mUseSmallerIcon) {
            ImageView imageView = (ImageView) preferenceViewHolder.findViewById(16908294);
            imageView.setMaxWidth(this.mContext.getResources().getDimensionPixelSize(R.dimen.secondary_app_icon_size));
            imageView.setMaxHeight(this.mContext.getResources().getDimensionPixelSize(R.dimen.secondary_app_icon_size));
        }
        super.onBindViewHolder(preferenceViewHolder);
        if (this.mWidgetIcon != null) {
            ((ImageView) preferenceViewHolder.findViewById(16908312).findViewById(R.id.icon)).setImageDrawable(this.mWidgetIcon);
        }
        if (this.mEllipsizeEnd) {
            TextView textView = (TextView) preferenceViewHolder.findViewById(16908310);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        setIcons(preferenceViewHolder, this.mSummaryIcons, R.id.summary_widget_frame);
        setIcons(preferenceViewHolder, this.mTitleIcons, R.id.title_widget_frame);
    }

    private void setIcons(PreferenceViewHolder preferenceViewHolder, List<Integer> list, int i) {
        ViewGroup viewGroup = (ViewGroup) preferenceViewHolder.findViewById(i);
        if (list == null || list.isEmpty()) {
            if (viewGroup != null) {
                viewGroup.setVisibility(8);
                return;
            }
            return;
        }
        viewGroup.setVisibility(0);
        viewGroup.removeAllViews();
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            ViewGroup viewGroup2 = (ViewGroup) ((LayoutInflater) this.mContext.getSystemService(LayoutInflater.class)).inflate(R.layout.title_summary_image_view, (ViewGroup) null);
            ((ImageView) viewGroup2.requireViewById(R.id.icon)).setImageResource(list.get(i2).intValue());
            viewGroup.addView(viewGroup2);
        }
    }
}
