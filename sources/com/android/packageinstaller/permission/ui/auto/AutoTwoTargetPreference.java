package com.android.packageinstaller.permission.ui.auto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.car.ui.R;
/* loaded from: classes.dex */
public class AutoTwoTargetPreference extends Preference {
    private boolean mIsDividerVisible;
    private OnSecondTargetClickListener mListener;

    /* loaded from: classes.dex */
    public interface OnSecondTargetClickListener {
        void onSecondTargetClick(AutoTwoTargetPreference autoTwoTargetPreference);
    }

    public AutoTwoTargetPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsDividerVisible = true;
        init();
    }

    public AutoTwoTargetPreference(Context context) {
        super(context);
        this.mIsDividerVisible = true;
        init();
    }

    private void init() {
        setLayoutResource(R.layout.car_two_target_preference);
    }

    public void setOnSecondTargetClickListener(OnSecondTargetClickListener onSecondTargetClickListener) {
        this.mListener = onSecondTargetClickListener;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(R.id.action_widget_container);
        View findViewById2 = preferenceViewHolder.findViewById(R.id.two_target_divider);
        FrameLayout frameLayout = (FrameLayout) preferenceViewHolder.findViewById(16908312);
        if (this.mListener != null) {
            findViewById.setVisibility(0);
            findViewById2.setVisibility(this.mIsDividerVisible ? 0 : 8);
            frameLayout.setVisibility(0);
            frameLayout.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.auto.-$$Lambda$AutoTwoTargetPreference$BT7bcMp8A-ExOjey55Q7ZMaUnCY
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AutoTwoTargetPreference.this.lambda$onBindViewHolder$0$AutoTwoTargetPreference(view);
                }
            });
            return;
        }
        findViewById.setVisibility(8);
    }

    public /* synthetic */ void lambda$onBindViewHolder$0$AutoTwoTargetPreference(View view) {
        this.mListener.onSecondTargetClick(this);
    }
}
