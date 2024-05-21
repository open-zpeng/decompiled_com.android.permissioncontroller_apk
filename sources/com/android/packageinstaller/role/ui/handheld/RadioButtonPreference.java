package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.TwoStatePreference;
import com.android.car.ui.R;
/* loaded from: classes.dex */
class RadioButtonPreference extends TwoStatePreference {
    private final OnCheckedChangeListener mOnCheckedChangeListener;

    RadioButtonPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mOnCheckedChangeListener = new OnCheckedChangeListener();
        setWidgetLayoutResource(R.layout.radio_button_preference_widget);
    }

    RadioButtonPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    RadioButtonPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle, 16842894));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RadioButtonPreference(Context context) {
        this(context, null);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ViewGroup viewGroup = (ViewGroup) preferenceViewHolder.itemView;
        View findViewById = preferenceViewHolder.findViewById(16908312);
        if (viewGroup.indexOfChild(findViewById) != 0) {
            findViewById.setPaddingRelative(findViewById.getPaddingEnd(), findViewById.getPaddingTop(), findViewById.getPaddingStart(), findViewById.getPaddingBottom());
            viewGroup.removeView(findViewById);
            viewGroup.addView(findViewById, 0);
            viewGroup.setPaddingRelative(0, viewGroup.getPaddingTop(), viewGroup.getPaddingEnd(), viewGroup.getPaddingBottom());
        }
        RadioButton radioButton = (RadioButton) preferenceViewHolder.findViewById(R.id.radio_button);
        radioButton.setOnCheckedChangeListener(null);
        radioButton.setChecked(this.mChecked);
        radioButton.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
    }

    /* loaded from: classes.dex */
    private class OnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        OnCheckedChangeListener() {
        }

        @Override // android.widget.CompoundButton.OnCheckedChangeListener
        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (!RadioButtonPreference.this.callChangeListener(Boolean.valueOf(z))) {
                compoundButton.setChecked(!z);
            } else {
                RadioButtonPreference.this.setChecked(z);
            }
        }
    }
}
