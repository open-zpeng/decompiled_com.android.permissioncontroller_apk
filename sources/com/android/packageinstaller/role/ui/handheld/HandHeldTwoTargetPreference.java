package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceViewHolder;
import com.android.car.ui.R;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
/* loaded from: classes.dex */
abstract class HandHeldTwoTargetPreference extends TwoTargetPreference {
    /* JADX INFO: Access modifiers changed from: package-private */
    public HandHeldTwoTargetPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayoutResource(R.layout.two_target_preference);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908312);
        ViewGroup viewGroup = (ViewGroup) findViewById.getParent();
        ViewGroup viewGroup2 = (ViewGroup) preferenceViewHolder.itemView;
        if (viewGroup != viewGroup2) {
            viewGroup.removeView(findViewById);
            viewGroup2.addView(findViewById);
        }
    }
}
