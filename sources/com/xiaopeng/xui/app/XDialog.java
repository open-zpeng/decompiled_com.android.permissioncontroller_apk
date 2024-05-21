package com.xiaopeng.xui.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.xpui.R$dimen;
import com.xiaopeng.xpui.R$drawable;
import com.xiaopeng.xpui.R$style;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XDialogInterface;
import com.xiaopeng.xui.utils.XDialogUtils;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.IVuiViewScene;
import com.xiaopeng.xui.widget.dialogview.XDialogView;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
import java.util.List;
/* loaded from: classes.dex */
public class XDialog implements IVuiViewScene {
    private static int sObjectSize;
    private Context mContext;
    private Dialog mDialog;
    private XDialogInterface.OnClickListener mNegativeListener;
    private XDialogViewInterface.OnClickListener mNegativeListenerProxy;
    private XDialogInterface.OnCloseListener mOnCloseListener;
    private XDialogViewInterface.OnCloseListener mOnCloseListenerProxy;
    private XDialogInterface.OnCountDownListener mOnCountDownListener;
    private XDialogViewInterface.OnCountDownListener mOnCountDownListenerProxy;
    private DialogInterface.OnKeyListener mOnKeyListener;
    private XDialogInterface.OnClickListener mPositiveListener;
    private XDialogViewInterface.OnClickListener mPositiveListenerProxy;
    private int mSystemDialogOffsetY;
    private int mWindowBackgroundId;
    private XDialogView mXDialogView;

    public XDialog(Context context) {
        this(context, 0);
    }

    public XDialog(Context context, Parameters parameters) {
        this(context, 0, parameters);
    }

    public XDialog(Context context, int i) {
        this(context, i, null);
    }

    public XDialog(Context context, int i, Parameters parameters) {
        this.mXDialogView = new XDialogView(context, i);
        this.mContext = context;
        parameters = parameters == null ? Parameters.Builder() : parameters;
        if (parameters.mTheme != 0) {
            this.mDialog = new Dialog(context, parameters.mTheme);
        } else {
            this.mDialog = new Dialog(context, R$style.XAppTheme_XDialog);
        }
        if (parameters.mFullScreen) {
            XDialogUtils.requestFullScreen(this.mDialog);
        }
        this.mDialog.setContentView(this.mXDialogView.getContentView());
        init();
        sObjectSize++;
    }

    private void init() {
        this.mSystemDialogOffsetY = (int) this.mContext.getResources().getDimension(R$dimen.x_dialog_system_offset_y);
        this.mXDialogView.setOnDismissListener(new XDialogViewInterface.OnDismissListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$9P4fB9VBMHmiGhl_mlGUJko3V2E
            @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnDismissListener
            public final void onDismiss(XDialogView xDialogView) {
                XDialog.this.lambda$init$0$XDialog(xDialogView);
            }
        });
        this.mXDialogView.setThemeCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$s3CmKHbTxXoHkg0e6JUBfY_y_-Q
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public final void onThemeChanged() {
                XDialog.this.lambda$init$1$XDialog();
            }
        });
        this.mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$WZ3Q9pXFsK8sg4PXWTmacfC8DAk
            @Override // android.content.DialogInterface.OnKeyListener
            public final boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return XDialog.this.lambda$init$2$XDialog(dialogInterface, i, keyEvent);
            }
        });
        TypedArray obtainStyledAttributes = this.mDialog.getContext().obtainStyledAttributes(new int[]{16842836});
        this.mWindowBackgroundId = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
    }

    public /* synthetic */ void lambda$init$0$XDialog(XDialogView xDialogView) {
        dismiss();
    }

    public /* synthetic */ void lambda$init$1$XDialog() {
        logs("onThemeChanged, mWindowBackgroundId " + this.mWindowBackgroundId);
        if (this.mWindowBackgroundId == 0) {
            this.mWindowBackgroundId = R$drawable.x_bg_dialog;
        }
        if (this.mDialog.getWindow() == null || this.mWindowBackgroundId <= 0) {
            return;
        }
        this.mDialog.getWindow().setBackgroundDrawableResource(this.mWindowBackgroundId);
    }

    public /* synthetic */ boolean lambda$init$2$XDialog(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        DialogInterface.OnKeyListener onKeyListener = this.mOnKeyListener;
        if (onKeyListener != null && onKeyListener.onKey(dialogInterface, i, keyEvent)) {
            logs("custom key listener return true  keyCode : " + i + ", event " + keyEvent.getAction());
            return true;
        }
        return this.mXDialogView.onKey(i, keyEvent);
    }

    private void initCloseListenerProxy() {
        if (this.mOnCloseListenerProxy == null) {
            this.mOnCloseListenerProxy = new XDialogViewInterface.OnCloseListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$l1LARSaNT8tDepVXLgxNqVdJkkc
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnCloseListener
                public final boolean onClose(XDialogView xDialogView) {
                    return XDialog.this.lambda$initCloseListenerProxy$3$XDialog(xDialogView);
                }
            };
            this.mXDialogView.setOnCloseListener(this.mOnCloseListenerProxy);
        }
    }

    public /* synthetic */ boolean lambda$initCloseListenerProxy$3$XDialog(XDialogView xDialogView) {
        XDialogInterface.OnCloseListener onCloseListener = this.mOnCloseListener;
        if (onCloseListener != null) {
            return onCloseListener.onClose(this);
        }
        return false;
    }

    private void initCountDownListenerProxy() {
        if (this.mOnCountDownListenerProxy == null) {
            this.mOnCountDownListenerProxy = new XDialogViewInterface.OnCountDownListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$fbj-1PFv3EpqSHcLX-8gv1vcQx4
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnCountDownListener
                public final boolean onCountDown(XDialogView xDialogView, int i) {
                    return XDialog.this.lambda$initCountDownListenerProxy$4$XDialog(xDialogView, i);
                }
            };
            this.mXDialogView.setOnCountDownListener(this.mOnCountDownListenerProxy);
        }
    }

    public /* synthetic */ boolean lambda$initCountDownListenerProxy$4$XDialog(XDialogView xDialogView, int i) {
        XDialogInterface.OnCountDownListener onCountDownListener = this.mOnCountDownListener;
        if (onCountDownListener != null) {
            return onCountDownListener.onCountDown(this, i);
        }
        return false;
    }

    private void initPositiveListenerProxy() {
        if (this.mPositiveListenerProxy == null) {
            this.mPositiveListenerProxy = new XDialogViewInterface.OnClickListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$8umUr7E18qQsZk8zQujzHQFIHPY
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnClickListener
                public final void onClick(XDialogView xDialogView, int i) {
                    XDialog.this.lambda$initPositiveListenerProxy$5$XDialog(xDialogView, i);
                }
            };
        }
    }

    public /* synthetic */ void lambda$initPositiveListenerProxy$5$XDialog(XDialogView xDialogView, int i) {
        XDialogInterface.OnClickListener onClickListener = this.mPositiveListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, i);
        }
    }

    private void initNegativeListenerProxy() {
        if (this.mNegativeListenerProxy == null) {
            this.mNegativeListenerProxy = new XDialogViewInterface.OnClickListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$FFTotuu-r5O5A4gt3f42vBsU9o4
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnClickListener
                public final void onClick(XDialogView xDialogView, int i) {
                    XDialog.this.lambda$initNegativeListenerProxy$6$XDialog(xDialogView, i);
                }
            };
        }
    }

    public /* synthetic */ void lambda$initNegativeListenerProxy$6$XDialog(XDialogView xDialogView, int i) {
        XDialogInterface.OnClickListener onClickListener = this.mNegativeListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, i);
        }
    }

    public ViewGroup getContentView() {
        return this.mXDialogView.getContentView();
    }

    public XDialog setPositiveButtonInterceptDismiss(boolean z) {
        this.mXDialogView.setPositiveButtonInterceptDismiss(z);
        return this;
    }

    public XDialog setNegativeButtonInterceptDismiss(boolean z) {
        this.mXDialogView.setNegativeButtonInterceptDismiss(z);
        return this;
    }

    public XDialog setTitle(CharSequence charSequence) {
        this.mXDialogView.setTitle(charSequence);
        return this;
    }

    public XDialog setTitle(int i) {
        this.mXDialogView.setTitle(i);
        return this;
    }

    public XDialog setIcon(int i) {
        this.mXDialogView.setIcon(i);
        return this;
    }

    public XDialog setIcon(Drawable drawable) {
        this.mXDialogView.setIcon(drawable);
        return this;
    }

    public XDialog setMessage(CharSequence charSequence) {
        this.mXDialogView.setMessage(charSequence);
        return this;
    }

    public XDialog setMessage(int i) {
        this.mXDialogView.setMessage(i);
        return this;
    }

    public XDialog setCustomView(View view) {
        this.mXDialogView.setCustomView(view);
        return this;
    }

    public XDialog setCustomView(View view, boolean z) {
        this.mXDialogView.setCustomView(view, z);
        return this;
    }

    public XDialog setCustomView(int i) {
        this.mXDialogView.setCustomView(i);
        return this;
    }

    public XDialog setCustomView(int i, boolean z) {
        this.mXDialogView.setCustomView(i, z);
        return this;
    }

    public XDialog setCloseVisibility(boolean z) {
        this.mXDialogView.setCloseVisibility(z);
        return this;
    }

    public void setThemeCallback(ThemeViewModel.OnCallback onCallback) {
        this.mXDialogView.setThemeCallback(onCallback);
    }

    public boolean isCloseShowing() {
        return this.mXDialogView.isCloseShowing();
    }

    @Deprecated
    public XDialog setTitleVisibility(boolean z) {
        setTitleBarVisibility(z);
        return this;
    }

    public XDialog setTitleBarVisibility(boolean z) {
        this.mXDialogView.setTitleBarVisibility(z);
        return this;
    }

    public XDialog setPositiveButton(int i) {
        this.mXDialogView.setPositiveButton(i);
        return this;
    }

    public XDialog setPositiveButton(CharSequence charSequence) {
        this.mXDialogView.setPositiveButton(charSequence);
        return this;
    }

    public XDialog setPositiveButtonListener(XDialogInterface.OnClickListener onClickListener) {
        this.mPositiveListener = onClickListener;
        if (onClickListener != null) {
            initPositiveListenerProxy();
        }
        this.mXDialogView.setPositiveButtonListener(this.mPositiveListenerProxy);
        return this;
    }

    public XDialog setPositiveButton(int i, XDialogInterface.OnClickListener onClickListener) {
        setPositiveButton(i);
        setPositiveButtonListener(onClickListener);
        return this;
    }

    public XDialog setPositiveButton(CharSequence charSequence, XDialogInterface.OnClickListener onClickListener) {
        setPositiveButton(charSequence);
        setPositiveButtonListener(onClickListener);
        return this;
    }

    public XDialog setNegativeButton(int i) {
        this.mXDialogView.setNegativeButton(i);
        return this;
    }

    public XDialog setNegativeButton(CharSequence charSequence) {
        this.mXDialogView.setNegativeButton(charSequence);
        return this;
    }

    public XDialog setNegativeButtonListener(XDialogInterface.OnClickListener onClickListener) {
        this.mNegativeListener = onClickListener;
        if (onClickListener != null) {
            initNegativeListenerProxy();
        }
        this.mXDialogView.setNegativeButtonListener(this.mNegativeListenerProxy);
        return this;
    }

    public XDialog setNegativeButton(int i, XDialogInterface.OnClickListener onClickListener) {
        setNegativeButton(i);
        setNegativeButtonListener(onClickListener);
        return this;
    }

    public XDialog setNegativeButton(CharSequence charSequence, XDialogInterface.OnClickListener onClickListener) {
        setNegativeButton(charSequence);
        setNegativeButtonListener(onClickListener);
        return this;
    }

    public XDialog setPositiveButtonEnable(boolean z) {
        this.mXDialogView.setPositiveButtonEnable(z);
        return this;
    }

    public XDialog setNegativeButtonEnable(boolean z) {
        this.mXDialogView.setNegativeButtonEnable(z);
        return this;
    }

    public boolean isPositiveButtonEnable() {
        return this.mXDialogView.isPositiveButtonEnable();
    }

    public boolean isNegativeButtonEnable() {
        return this.mXDialogView.isNegativeButtonEnable();
    }

    public boolean isPositiveButtonShowing() {
        return this.mXDialogView.isPositiveButtonShowing();
    }

    public boolean isNegativeButtonShowing() {
        return this.mXDialogView.isNegativeButtonShowing();
    }

    public void show() {
        show(0, 0);
    }

    public void show(int i, int i2) {
        logs("show");
        if (i > 0 && i2 == 0) {
            this.mXDialogView.startPositiveButtonCountDown(i);
        }
        if (i2 > 0 && i == 0) {
            this.mXDialogView.startNegativeButtonCountDown(i2);
        }
        if (this.mDialog.getWindow() != null) {
            WindowManager.LayoutParams attributes = this.mDialog.getWindow().getAttributes();
            attributes.gravity = 17;
            attributes.y = attributes.type != 9 ? this.mSystemDialogOffsetY : 0;
            this.mDialog.getWindow().setAttributes(attributes);
        }
        this.mDialog.show();
    }

    public void dismiss() {
        logs("dismiss");
        this.mDialog.dismiss();
    }

    public void cancel() {
        logs("cancel");
        this.mDialog.cancel();
    }

    public boolean isShowing() {
        return this.mDialog.isShowing();
    }

    public XDialog setOnCloseListener(XDialogInterface.OnCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
        if (onCloseListener != null) {
            initCloseListenerProxy();
        }
        return this;
    }

    public XDialog setOnCountDownListener(XDialogInterface.OnCountDownListener onCountDownListener) {
        this.mOnCountDownListener = onCountDownListener;
        if (onCountDownListener != null) {
            initCountDownListenerProxy();
        }
        return this;
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public XDialog setSystemDialog(int i) {
        if (this.mDialog.getWindow() != null) {
            this.mDialog.getWindow().setType(i);
        }
        return this;
    }

    public XDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mDialog.setOnDismissListener(onDismissListener);
        return this;
    }

    public XDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mDialog.setOnCancelListener(onCancelListener);
        return this;
    }

    public XDialog setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.mDialog.setOnShowListener(onShowListener);
        return this;
    }

    public XDialog setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.mOnKeyListener = onKeyListener;
        return this;
    }

    public XDialog setCancelable(boolean z) {
        this.mDialog.setCancelable(z);
        return this;
    }

    public XDialog setCanceledOnTouchOutside(boolean z) {
        this.mDialog.setCanceledOnTouchOutside(z);
        return this;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiSceneId(String str) {
        this.mXDialogView.setVuiSceneId(str);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiEngine(IVuiEngine iVuiEngine) {
        this.mXDialogView.setVuiEngine(iVuiEngine);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiElementListener(IVuiElementListener iVuiElementListener) {
        this.mXDialogView.setVuiElementListener(iVuiElementListener);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setCustomViewIdList(List<Integer> list) {
        this.mXDialogView.setCustomViewIdList(list);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void initVuiScene(String str, IVuiEngine iVuiEngine) {
        this.mXDialogView.initVuiScene(str, iVuiEngine);
    }

    protected void finalize() throws Throwable {
        super.finalize();
        sObjectSize--;
        logs(" finalize object size " + sObjectSize);
    }

    /* loaded from: classes.dex */
    public static class Parameters {
        private boolean mFullScreen = Xui.isDialogFullScreen();
        private int mTheme;

        public static Parameters Builder() {
            return new Parameters();
        }

        private Parameters() {
        }

        public Parameters setTheme(int i) {
            this.mTheme = i;
            return this;
        }

        public Parameters setFullScreen(boolean z) {
            this.mFullScreen = z;
            return this;
        }
    }

    private void logs(String str) {
        XLogUtils.i("XDialog", str + "--hashcode " + hashCode());
    }
}
