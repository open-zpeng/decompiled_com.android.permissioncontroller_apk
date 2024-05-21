package com.android.car.ui.preference;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class CarUiPreference extends Preference {
    private Context mContext;
    private boolean mShowChevron;

    public CarUiPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet, i, i2);
    }

    public CarUiPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.Preference_CarUi_Preference);
    }

    public CarUiPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.carUiPreferenceStyle);
    }

    public CarUiPreference(Context context) {
        this(context, null);
    }

    public void init(Context context, AttributeSet attributeSet, int i, int i2) {
        this.mContext = context;
        this.mShowChevron = getContext().obtainStyledAttributes(attributeSet, R.styleable.CarUiPreference, i, i2).getBoolean(R.styleable.CarUiPreference_showChevron, true);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        if (this.mContext.getResources().getBoolean(R.bool.car_ui_preference_show_chevron) && this.mShowChevron) {
            if (getOnPreferenceClickListener() == null && getIntent() == null && getFragment() == null) {
                return;
            }
            setWidgetLayoutResource(R.layout.car_ui_preference_chevron);
        }
    }

    public void setShowChevron(boolean z) {
        this.mShowChevron = z;
    }
}
