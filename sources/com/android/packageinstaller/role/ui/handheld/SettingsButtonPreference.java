package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.car.ui.R;
import com.android.packageinstaller.role.ui.TwoTargetPreference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class SettingsButtonPreference extends HandHeldTwoTargetPreference {
    private TwoTargetPreference.OnSecondTargetClickListener mOnSecondTargetClickListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SettingsButtonPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.settings_button_preference_widget);
    }

    @Override // com.android.packageinstaller.role.ui.TwoTargetPreference
    public void setOnSecondTargetClickListener(TwoTargetPreference.OnSecondTargetClickListener onSecondTargetClickListener) {
        this.mOnSecondTargetClickListener = onSecondTargetClickListener;
        notifyChanged();
    }

    @Override // com.android.packageinstaller.role.ui.handheld.HandHeldTwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908312);
        findViewById.setPadding(0, 0, 0, 0);
        View findViewById2 = preferenceViewHolder.findViewById(R.id.settings_button);
        if (this.mOnSecondTargetClickListener != null) {
            findViewById.setVisibility(0);
            findViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.role.ui.handheld.-$$Lambda$SettingsButtonPreference$QgxI4zD-po_3BfkjvegwFv-RiD0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    SettingsButtonPreference.this.lambda$onBindViewHolder$0$SettingsButtonPreference(view);
                }
            });
        } else {
            findViewById.setVisibility(8);
            findViewById2.setOnClickListener(null);
        }
        findViewById2.setEnabled(true);
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$SettingsButtonPreference(View view) {
        this.mOnSecondTargetClickListener.onSecondTargetClick(this);
    }
}
