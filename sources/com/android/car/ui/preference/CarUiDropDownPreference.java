package com.android.car.ui.preference;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.DropDownPreference;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class CarUiDropDownPreference extends DropDownPreference {
    private final Context mContext;

    public CarUiDropDownPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public CarUiDropDownPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public CarUiDropDownPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public CarUiDropDownPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.DropDownPreference, androidx.preference.DialogPreference, androidx.preference.Preference
    public void onClick() {
        getPreferenceManager().showDialog(this);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        if (this.mContext.getResources().getBoolean(R.bool.car_ui_preference_show_chevron)) {
            setWidgetLayoutResource(R.layout.car_ui_preference_chevron);
        }
    }
}
