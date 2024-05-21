package com.xiaopeng.xui.vui;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiUpdateType;
import com.xiaopeng.vui.commons.model.VuiEvent;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import java.util.List;
/* loaded from: classes.dex */
public abstract class VuiViewScene implements IVuiViewScene, IVuiSceneListener {
    private View mRootView;
    private IVuiEngine mVuiEngine = null;
    private String mSceneId = "";
    private IVuiElementListener mVuiElementListener = null;
    private List<Integer> customViewIds = null;
    private Handler mHandler = new Handler();
    private View.OnAttachStateChangeListener mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() { // from class: com.xiaopeng.xui.vui.VuiViewScene.1
        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
            if (Xui.isVuiEnable()) {
                VuiViewScene.this.createVuiScene();
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            if (Xui.isVuiEnable()) {
                VuiViewScene.this.destroyVuiScene();
            }
        }
    };
    IVuiElementChangedListener mListener = new IVuiElementChangedListener() { // from class: com.xiaopeng.xui.vui.-$$Lambda$VuiViewScene$opFwW47al5q72QJCRazwTlW1jvk
        @Override // com.xiaopeng.vui.commons.IVuiElementChangedListener
        public final void onVuiElementChaned(View view, VuiUpdateType vuiUpdateType) {
            VuiViewScene.this.lambda$new$1$VuiViewScene(view, vuiUpdateType);
        }
    };
    private boolean isUseNewProtocol = false;

    protected abstract void onBuildScenePrepare();

    /* JADX INFO: Access modifiers changed from: protected */
    public void setVuiView(View view) {
        log("initVui");
        if (Xui.isVuiEnable()) {
            View view2 = this.mRootView;
            if (view2 != null) {
                view2.removeOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
            }
            this.mRootView = view;
            view.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onVuiEvent(View view, VuiEvent vuiEvent) {
        log("VuiViewScene onVuiEvent");
        if (view == null) {
            return;
        }
        IVuiElementListener iVuiElementListener = this.mVuiElementListener;
        if (iVuiElementListener != null) {
            iVuiElementListener.onVuiElementEvent(view, vuiEvent);
        } else {
            VuiFloatingLayerManager.show(view);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public boolean onInterceptVuiEvent(View view, VuiEvent vuiEvent) {
        log("onInterceptVuiEvent");
        if (view == null) {
            return false;
        }
        IVuiElementListener iVuiElementListener = this.mVuiElementListener;
        if (iVuiElementListener != null) {
            return iVuiElementListener.onVuiElementEvent(view, vuiEvent);
        }
        VuiFloatingLayerManager.show(view);
        return false;
    }

    @Override // com.xiaopeng.vui.commons.IVuiSceneListener
    public void onBuildScene() {
        this.mHandler.post(new Runnable() { // from class: com.xiaopeng.xui.vui.-$$Lambda$VuiViewScene$bhRg6IeHfpBu4ffKtyCCDOAoC1Q
            @Override // java.lang.Runnable
            public final void run() {
                VuiViewScene.this.lambda$onBuildScene$0$VuiViewScene();
            }
        });
    }

    public /* synthetic */ void lambda$onBuildScene$0$VuiViewScene() {
        if (this.mVuiEngine == null || this.mSceneId == null || this.mRootView == null) {
            return;
        }
        log("onBuildScene");
        onBuildScenePrepare();
        this.mVuiEngine.buildScene(this.mSceneId, this.mRootView, this.customViewIds, this.mVuiElementListener);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiSceneId(String str) {
        this.mSceneId = str;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiEngine(IVuiEngine iVuiEngine) {
        this.mVuiEngine = iVuiEngine;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiElementListener(IVuiElementListener iVuiElementListener) {
        this.mVuiElementListener = iVuiElementListener;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setCustomViewIdList(List<Integer> list) {
        this.customViewIds = list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createVuiScene() {
        if (this.mVuiEngine == null || this.mSceneId == null || this.mRootView == null) {
            return;
        }
        log("createVuiScene");
        try {
            if (this.isUseNewProtocol) {
                this.mVuiEngine.addVuiSceneListener(this.mSceneId, this.mRootView, this, this.mListener);
            } else {
                this.mVuiEngine.addVuiSceneListener(this.mSceneId, this.mRootView, this);
            }
        } catch (AbstractMethodError unused) {
            this.mVuiEngine.addVuiSceneListener(this.mSceneId, this.mRootView, this);
        }
        this.mVuiEngine.enterScene(this.mSceneId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroyVuiScene() {
        if (this.mVuiEngine != null && this.mSceneId != null) {
            log("destroyVuiScene");
            this.mVuiEngine.exitScene(this.mSceneId);
            this.mVuiEngine.removeVuiSceneListener(this.mSceneId);
            this.mVuiEngine = null;
        }
        if (this.mVuiElementListener != null) {
            this.mVuiElementListener = null;
        }
    }

    private void log(String str) {
        XLogUtils.d("VuiViewScene", " mSceneId " + this.mSceneId + "  " + str + " hashcode " + hashCode() + " name " + getClass().getSimpleName());
    }

    public /* synthetic */ void lambda$new$1$VuiViewScene(View view, VuiUpdateType vuiUpdateType) {
        if (this.mVuiEngine == null || TextUtils.isEmpty(this.mSceneId) || view == null) {
            return;
        }
        if (VuiUpdateType.UPDATE_VIEW.equals(vuiUpdateType)) {
            this.mVuiEngine.updateScene(this.mSceneId, view);
        } else {
            this.mVuiEngine.updateElementAttribute(this.mSceneId, view);
        }
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void initVuiScene(String str, IVuiEngine iVuiEngine) {
        this.mSceneId = str;
        this.mVuiEngine = iVuiEngine;
        this.isUseNewProtocol = true;
    }
}
