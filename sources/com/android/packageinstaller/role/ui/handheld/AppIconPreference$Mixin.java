package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceViewHolder;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class AppIconPreference$Mixin {
    private int mIconSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppIconPreference$Mixin(Context context) {
        this.mIconSize = context.getResources().getDimensionPixelSize(R.dimen.secondary_app_icon_size);
    }

    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        boolean z;
        View findViewById = preferenceViewHolder.findViewById(16908294);
        ViewGroup.LayoutParams layoutParams = findViewById.getLayoutParams();
        int i = layoutParams.width;
        int i2 = this.mIconSize;
        if (i != i2) {
            layoutParams.width = i2;
            z = true;
        } else {
            z = false;
        }
        int i3 = layoutParams.height;
        int i4 = this.mIconSize;
        if (i3 != i4) {
            layoutParams.height = i4;
            z = true;
        }
        if (z) {
            findViewById.requestLayout();
        }
    }
}
