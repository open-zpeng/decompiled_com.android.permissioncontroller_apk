package com.android.packageinstaller.permission.ui.handheld;

import android.content.Context;
import android.view.View;
import android.widget.Switch;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class MultiTargetSwitchPreference extends SwitchPreference {
    private View.OnClickListener mSwitchOnClickLister;

    public MultiTargetSwitchPreference(Context context) {
        super(context);
    }

    public void setCheckedOverride(boolean z) {
        super.setChecked(z);
    }

    @Override // androidx.preference.TwoStatePreference
    public void setChecked(boolean z) {
        if (this.mSwitchOnClickLister == null) {
            super.setChecked(z);
        }
    }

    public void setSwitchOnClickListener(View.OnClickListener onClickListener) {
        this.mSwitchOnClickLister = onClickListener;
    }

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Switch r0 = (Switch) preferenceViewHolder.itemView.findViewById(16908352);
        if (r0 != null) {
            r0.setOnClickListener(this.mSwitchOnClickLister);
            if (this.mSwitchOnClickLister != null) {
                int measuredHeight = (int) (((preferenceViewHolder.itemView.getMeasuredHeight() - r0.getMeasuredHeight()) / 2) + 0.5f);
                r0.setPaddingRelative(measuredHeight, measuredHeight, 0, measuredHeight);
            }
        }
    }
}
