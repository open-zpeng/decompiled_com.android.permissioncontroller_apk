package com.android.packageinstaller.role.ui.auto;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.TwoStatePreference;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class AutoDefaultAppPreference extends TwoStatePreference {
    public AutoDefaultAppPreference(Context context) {
        super(context, null, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle, 16842894));
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908304);
        if (textView == null) {
            return;
        }
        if (isChecked()) {
            CharSequence summary = getSummary();
            String string = getContext().getString(R.string.car_default_app_selected);
            if (!TextUtils.isEmpty(summary)) {
                string = getContext().getString(R.string.car_default_app_selected_with_info, summary);
            }
            textView.setText(string);
            textView.setVisibility(0);
            return;
        }
        textView.setVisibility(8);
    }
}
