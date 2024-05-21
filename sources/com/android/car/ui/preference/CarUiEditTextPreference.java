package com.android.car.ui.preference;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.EditTextPreference;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class CarUiEditTextPreference extends EditTextPreference {
    private final Context mContext;
    private boolean mShowChevron;

    public CarUiEditTextPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mShowChevron = true;
        this.mContext = context;
    }

    public CarUiEditTextPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mShowChevron = true;
        this.mContext = context;
    }

    public CarUiEditTextPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowChevron = true;
        this.mContext = context;
    }

    public CarUiEditTextPreference(Context context) {
        super(context);
        this.mShowChevron = true;
        this.mContext = context;
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        if (this.mContext.getResources().getBoolean(R.bool.car_ui_preference_show_chevron) && this.mShowChevron) {
            setWidgetLayoutResource(R.layout.car_ui_preference_chevron);
        }
    }

    public void setShowChevron(boolean z) {
        this.mShowChevron = z;
    }
}
