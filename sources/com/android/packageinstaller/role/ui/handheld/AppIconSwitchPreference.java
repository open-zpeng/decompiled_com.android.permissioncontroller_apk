package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.SwitchPreference;
/* loaded from: classes.dex */
class AppIconSwitchPreference extends SwitchPreference {
    private AppIconPreference$Mixin mMixin;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppIconSwitchPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.mMixin = new AppIconPreference$Mixin(getContext());
    }

    @Override // androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mMixin.onBindViewHolder(preferenceViewHolder);
    }
}
