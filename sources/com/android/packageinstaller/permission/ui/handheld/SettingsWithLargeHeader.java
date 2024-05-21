package com.android.packageinstaller.permission.ui.handheld;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.car.ui.R;
import com.android.packageinstaller.DeviceUtils;
/* loaded from: classes.dex */
public abstract class SettingsWithLargeHeader extends PermissionsFrameFragment {
    private View mHeader;
    protected Drawable mIcon;
    protected Intent mInfoIntent;
    protected CharSequence mLabel;
    protected boolean mSmallIcon;
    protected UserHandle mUserHandle;

    @Override // com.android.packageinstaller.permission.ui.handheld.PermissionsFrameFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) super.onCreateView(layoutInflater, viewGroup, bundle);
        if (!DeviceUtils.isTelevision(getContext())) {
            View view = this.mHeader;
            if (view == null) {
                this.mHeader = layoutInflater.inflate(R.layout.header_large, viewGroup2, false);
                getPreferencesContainer().addView(this.mHeader, 0);
            } else if (view.getVisibility() == 0) {
                ((ViewGroup) this.mHeader.getParent()).removeView(this.mHeader);
                getPreferencesContainer().addView(this.mHeader, 0);
                updateHeader(this.mHeader);
                this.mHeader.requireViewById(R.id.header_link).setVisibility(0);
            }
        }
        return viewGroup2;
    }

    public void setHeader(Drawable drawable, CharSequence charSequence, Intent intent, UserHandle userHandle, boolean z) {
        this.mIcon = drawable;
        this.mLabel = charSequence;
        this.mInfoIntent = intent;
        this.mUserHandle = userHandle;
        this.mSmallIcon = z;
        updateHeader(this.mHeader);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateHeader(View view) {
        if (view != null) {
            view.setVisibility(0);
            ImageView imageView = (ImageView) view.requireViewById(R.id.entity_header_icon);
            imageView.setImageDrawable(this.mIcon);
            if (this.mSmallIcon) {
                int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.permission_icon_header_size);
                imageView.getLayoutParams().width = dimensionPixelSize;
                imageView.getLayoutParams().height = dimensionPixelSize;
            }
            if (this.mInfoIntent != null) {
                imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.-$$Lambda$SettingsWithLargeHeader$PUINXx2oPXk8pY5ntRmyCAdF5Ms
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        SettingsWithLargeHeader.this.lambda$updateHeader$0$SettingsWithLargeHeader(view2);
                    }
                });
                imageView.setContentDescription(this.mLabel);
            }
            ((TextView) view.requireViewById(R.id.entity_header_title)).setText(this.mLabel);
            view.requireViewById(R.id.entity_header_summary).setVisibility(8);
            view.requireViewById(R.id.entity_header_second_summary).setVisibility(8);
            view.requireViewById(R.id.header_link).setVisibility(8);
        }
    }

    public /* synthetic */ void lambda$updateHeader$0$SettingsWithLargeHeader(View view) {
        getActivity().startActivityAsUser(this.mInfoIntent, this.mUserHandle);
    }

    public void setSummary(CharSequence charSequence, View.OnClickListener onClickListener) {
        TextView textView = (TextView) this.mHeader.requireViewById(R.id.header_text);
        TextView textView2 = (TextView) this.mHeader.requireViewById(R.id.header_link);
        if (onClickListener != null) {
            textView2.setOnClickListener(onClickListener);
            textView2.setVisibility(0);
            textView2.setText(charSequence);
            textView.setVisibility(8);
            return;
        }
        textView.setVisibility(0);
        textView.setText(charSequence);
        textView2.setVisibility(8);
    }
}
