package com.android.packageinstaller.role.ui.auto;

import android.content.Context;
import android.util.AttributeSet;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
/* loaded from: classes.dex */
public class AutoSettingsPreference extends TwoTargetPreference {
    @Override // com.android.packageinstaller.role.ui.TwoTargetPreference
    public void setOnSecondTargetClickListener(TwoTargetPreference.OnSecondTargetClickListener onSecondTargetClickListener) {
    }

    public AutoSettingsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AutoSettingsPreference(Context context) {
        super(context);
    }
}
