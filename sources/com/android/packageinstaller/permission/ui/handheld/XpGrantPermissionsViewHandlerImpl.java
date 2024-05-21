package com.android.packageinstaller.permission.ui.handheld;

import android.app.Activity;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.UserHandle;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.android.car.ui.R;
import com.android.packageinstaller.permission.ui.GrantPermissionsViewHandler;
import com.xiaopeng.xui.widget.XCheckBox;
/* loaded from: classes.dex */
public class XpGrantPermissionsViewHandlerImpl implements GrantPermissionsViewHandler {
    private final Activity mActivity;
    private final String mAppPackageName;
    private View mBtnAllowView;
    private CharSequence[] mButtonLabels;
    private CharSequence mDetailMessage;
    private int mGroupCount;
    private Icon mGroupIcon;
    private int mGroupIndex;
    private CharSequence mGroupMessage;
    private String mGroupName;
    private TextView mMessageView;
    private XCheckBox mNotAskCheckBox;
    private GrantPermissionsViewHandler.ResultListener mResultListener;
    private ViewGroup mRootView;
    private TextView mTitleView;
    private final UserHandle mUserHandle;

    private void updateDetailDescription() {
    }

    @Override // com.android.packageinstaller.permission.ui.GrantPermissionsViewHandler
    public void updateWindowAttributes(WindowManager.LayoutParams layoutParams) {
    }

    public XpGrantPermissionsViewHandlerImpl(Activity activity, String str, UserHandle userHandle) {
        this.mActivity = activity;
        this.mAppPackageName = str;
        this.mUserHandle = userHandle;
    }

    public XpGrantPermissionsViewHandlerImpl setResultListener(GrantPermissionsViewHandler.ResultListener resultListener) {
        this.mResultListener = resultListener;
        return this;
    }

    @Override // com.android.packageinstaller.permission.ui.GrantPermissionsViewHandler
    public void saveInstanceState(Bundle bundle) {
        bundle.putString("ARG_GROUP_NAME", this.mGroupName);
        bundle.putInt("ARG_GROUP_COUNT", this.mGroupCount);
        bundle.putInt("ARG_GROUP_INDEX", this.mGroupIndex);
        bundle.putParcelable("ARG_GROUP_ICON", this.mGroupIcon);
        bundle.putCharSequence("ARG_GROUP_MESSAGE", this.mGroupMessage);
        bundle.putCharSequence("ARG_GROUP_DETAIL_MESSAGE", this.mDetailMessage);
        bundle.putCharSequenceArray("ARG_DIALOG_BUTTON_LABELS", this.mButtonLabels);
    }

    @Override // com.android.packageinstaller.permission.ui.GrantPermissionsViewHandler
    public void loadInstanceState(Bundle bundle) {
        this.mGroupName = bundle.getString("ARG_GROUP_NAME");
        this.mGroupMessage = bundle.getCharSequence("ARG_GROUP_MESSAGE");
        this.mGroupIcon = (Icon) bundle.getParcelable("ARG_GROUP_ICON");
        this.mGroupCount = bundle.getInt("ARG_GROUP_COUNT");
        this.mGroupIndex = bundle.getInt("ARG_GROUP_INDEX");
        this.mDetailMessage = bundle.getCharSequence("ARG_GROUP_DETAIL_MESSAGE");
        this.mButtonLabels = bundle.getCharSequenceArray("ARG_DIALOG_BUTTON_LABELS");
        Log.i("GrantPermis", String.format("loadInstanceState--mGroupName:%s  mGroupMessage:%s  mDetailMessage:%s", this.mGroupName, this.mGroupMessage, this.mDetailMessage));
        updateAll();
    }

    @Override // com.android.packageinstaller.permission.ui.GrantPermissionsViewHandler
    public void updateUi(String str, int i, int i2, Icon icon, CharSequence charSequence, CharSequence charSequence2, CharSequence[] charSequenceArr) {
        int i3 = this.mGroupIndex;
        this.mGroupName = str;
        this.mGroupCount = i;
        this.mGroupIndex = i2;
        this.mGroupIcon = icon;
        this.mGroupMessage = charSequence;
        this.mDetailMessage = charSequence2;
        this.mButtonLabels = charSequenceArr;
        Log.i("GrantPermis", String.format("updateUi--mGroupName:%s  mGroupMessage:%s  mDetailMessage:%s   groupCount:%s  groupIndex:%s", this.mGroupName, this.mGroupMessage, this.mDetailMessage, Integer.valueOf(i), Integer.valueOf(i2)));
        updateAll();
    }

    private void updateAll() {
        updateGroup();
        updateDescription();
        updateDetailDescription();
        this.mNotAskCheckBox.setChecked(false);
        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(200L);
        changeBounds.setInterpolator(AnimationUtils.loadInterpolator(this.mActivity, 17563661));
        TransitionManager.beginDelayedTransition(this.mRootView, changeBounds);
    }

    private void updateGroup() {
        if (this.mGroupCount > 1) {
            this.mTitleView.setText(this.mActivity.getString(R.string.current_permission_template, new Object[]{Integer.valueOf(this.mGroupIndex + 1), Integer.valueOf(this.mGroupCount)}));
        } else {
            this.mTitleView.setText(R.string.permission_request_title);
        }
    }

    @Override // com.android.packageinstaller.permission.ui.GrantPermissionsViewHandler
    public View createView() {
        this.mRootView = (ViewGroup) LayoutInflater.from(this.mActivity).inflate(R.layout.xp_grant_permissions, (ViewGroup) null);
        this.mTitleView = (TextView) this.mRootView.findViewById(R.id.x_dialog_title);
        this.mMessageView = (TextView) this.mRootView.findViewById(R.id.x_dialog_message);
        this.mRootView.findViewById(R.id.x_dialog_close).setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.XpGrantPermissionsViewHandlerImpl.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (XpGrantPermissionsViewHandlerImpl.this.mResultListener != null) {
                    XpGrantPermissionsViewHandlerImpl.this.mResultListener.onPermissionGrantResult(XpGrantPermissionsViewHandlerImpl.this.mGroupName, 2);
                }
            }
        });
        this.mBtnAllowView = this.mRootView.findViewById(R.id.x_dialog_button1);
        this.mBtnAllowView.setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.XpGrantPermissionsViewHandlerImpl.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (XpGrantPermissionsViewHandlerImpl.this.mResultListener != null) {
                    XpGrantPermissionsViewHandlerImpl.this.mResultListener.onPermissionGrantResult(XpGrantPermissionsViewHandlerImpl.this.mGroupName, 0);
                }
            }
        });
        this.mRootView.findViewById(R.id.x_dialog_button2).setOnClickListener(new View.OnClickListener() { // from class: com.android.packageinstaller.permission.ui.handheld.XpGrantPermissionsViewHandlerImpl.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (XpGrantPermissionsViewHandlerImpl.this.mResultListener != null) {
                    if (XpGrantPermissionsViewHandlerImpl.this.mNotAskCheckBox.isChecked()) {
                        XpGrantPermissionsViewHandlerImpl.this.mResultListener.onPermissionGrantResult(XpGrantPermissionsViewHandlerImpl.this.mGroupName, 3);
                    } else {
                        XpGrantPermissionsViewHandlerImpl.this.mResultListener.onPermissionGrantResult(XpGrantPermissionsViewHandlerImpl.this.mGroupName, 2);
                    }
                }
            }
        });
        this.mNotAskCheckBox = (XCheckBox) this.mRootView.findViewById(R.id.do_not_ask_checkbox);
        this.mNotAskCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.packageinstaller.permission.ui.handheld.XpGrantPermissionsViewHandlerImpl.4
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                XpGrantPermissionsViewHandlerImpl.this.mBtnAllowView.setEnabled(!z);
            }
        });
        String str = this.mGroupName;
        if (str != null) {
            Log.i("GrantPermis", String.format("createView--mGroupName:%s  mGroupMessage:%s  mDetailMessage:%s", str, this.mGroupMessage, this.mDetailMessage));
            updateAll();
        }
        return this.mRootView;
    }

    private void updateDescription() {
        this.mMessageView.setText(this.mGroupMessage);
        checkLines();
    }

    private void checkLines() {
        if (this.mMessageView.getWidth() > 0) {
            _checkLines();
        } else {
            this.mMessageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.packageinstaller.permission.ui.handheld.XpGrantPermissionsViewHandlerImpl.5
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    XpGrantPermissionsViewHandlerImpl.this.mMessageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    XpGrantPermissionsViewHandlerImpl.this._checkLines();
                    return true;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _checkLines() {
        if (this.mMessageView.getLineCount() < 2) {
            this.mMessageView.setGravity(1);
        } else {
            this.mMessageView.setGravity(8388611);
        }
        this.mMessageView.setVisibility(0);
    }
}
