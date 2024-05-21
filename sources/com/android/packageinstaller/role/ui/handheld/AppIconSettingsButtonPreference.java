package com.android.packageinstaller.role.ui.handheld;

import android.content.Context;
import androidx.preference.PreferenceViewHolder;
/* loaded from: classes.dex */
class AppIconSettingsButtonPreference extends SettingsButtonPreference {
    private AppIconPreference$Mixin mMixin;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppIconSettingsButtonPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.mMixin = new AppIconPreference$Mixin(getContext());
    }

    @Override // com.android.packageinstaller.role.ui.handheld.SettingsButtonPreference, com.android.packageinstaller.role.ui.handheld.HandHeldTwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mMixin.onBindViewHolder(preferenceViewHolder);
    }
}
