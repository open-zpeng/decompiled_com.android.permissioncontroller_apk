package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.car.ui.R;
import com.android.packageinstaller.role.utils.UiUtils;
/* loaded from: classes.dex */
class FooterPreference extends Preference {
    /* JADX INFO: Access modifiers changed from: package-private */
    public FooterPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setIcon(R.drawable.ic_info_outline);
        setSelectable(false);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedAbove(true);
        View findViewById = preferenceViewHolder.findViewById(R.id.icon_frame);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
        layoutParams.gravity = 48;
        findViewById.setLayoutParams(layoutParams);
        int dpToPxOffset = UiUtils.dpToPxOffset(16.0f, findViewById.getContext());
        findViewById.setPaddingRelative(findViewById.getPaddingStart(), dpToPxOffset, findViewById.getPaddingEnd(), dpToPxOffset);
    }
}
