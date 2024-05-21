package com.android.packageinstaller.role.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
/* loaded from: classes.dex */
public abstract class TwoTargetPreference extends Preference {

    /* loaded from: classes.dex */
    public interface OnSecondTargetClickListener {
        void onSecondTargetClick(TwoTargetPreference twoTargetPreference);
    }

    public abstract void setOnSecondTargetClickListener(OnSecondTargetClickListener onSecondTargetClickListener);

    public TwoTargetPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TwoTargetPreference(Context context) {
        super(context);
    }
}
