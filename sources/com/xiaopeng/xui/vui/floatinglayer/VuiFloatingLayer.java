package com.xiaopeng.xui.vui.floatinglayer;

import android.content.Context;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import com.xiaopeng.xui.app.XDialogSystemType;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class VuiFloatingLayer implements IVuiFloatingLayer {
    private static final String TAG = "VuiFloatingLayer";
    private Context mContext;
    private WindowManager mWindowManager;
    private SparseArray<VuiFloatingView> mVuiFloatingViewMap = new SparseArray<>();
    private SparseArray<LayerInfo> mLayerInfoMap = new SparseArray<>();
    private SparseArray<AnimationCallback> mAnimationCallbackMap = new SparseArray<>();
    private Handler mHandler = new Handler();
    private SparseArray<RunnableTouch> mRunnableTouchMap = new SparseArray<>();
    private SparseArray<RunnableTimeOut> mRunnableTimeOutMap = new SparseArray<>();
    private WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();

    /* loaded from: classes.dex */
    public class LayerInfo {
        int[] location;
        int mCenterOffsetX;
        int mCenterOffsetY;
        int targetHeight;
        int targetWidth;

        public LayerInfo() {
        }

        public String toString() {
            return "LayerInfo{targetWidth=" + this.targetWidth + ", targetHeight=" + this.targetHeight + ", location=" + Arrays.toString(this.location) + ", mCenterOffsetX=" + this.mCenterOffsetX + ", mCenterOffsetY=" + this.mCenterOffsetY + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VuiFloatingLayer(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        WindowManager.LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.gravity = 8388659;
        layoutParams.type = XDialogSystemType.TYPE_SYSTEM_DIALOG;
        layoutParams.flags = 328488;
        layoutParams.format = -2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VuiFloatingView getVuiFloatingView(final int i) {
        VuiFloatingView vuiFloatingView = this.mVuiFloatingViewMap.get(i);
        if (vuiFloatingView == null) {
            VuiFloatingView vuiFloatingView2 = new VuiFloatingView(this.mContext, i);
            this.mVuiFloatingViewMap.put(i, vuiFloatingView2);
            vuiFloatingView2.setOnTouchListener(new VuiFloatingView.OnTouchListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.-$$Lambda$VuiFloatingLayer$UXxtbfMrTLOTVDQw-YOjhAcXvSA
                @Override // com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView.OnTouchListener
                public final void onTouch(int i2) {
                    VuiFloatingLayer.this.lambda$getVuiFloatingView$0$VuiFloatingLayer(i, i2);
                }
            });
            return vuiFloatingView2;
        }
        return vuiFloatingView;
    }

    public /* synthetic */ void lambda$getVuiFloatingView$0$VuiFloatingLayer(int i, int i2) {
        this.mHandler.postDelayed(getRunnableTouch(i), 150L);
    }

    private AnimationCallback getAnimationCallback(int i) {
        AnimationCallback animationCallback = this.mAnimationCallbackMap.get(i);
        if (animationCallback == null) {
            AnimationCallback animationCallback2 = new AnimationCallback(i);
            this.mAnimationCallbackMap.put(i, animationCallback2);
            return animationCallback2;
        }
        return animationCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RunnableTouch implements Runnable {
        private int type;

        RunnableTouch(int i) {
            this.type = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiFloatingLayer vuiFloatingLayer = VuiFloatingLayer.this;
            vuiFloatingLayer.log("touch RunnableTouch type " + this.type);
            VuiFloatingLayer.this.removeView(this.type);
        }
    }

    private RunnableTouch getRunnableTouch(int i) {
        RunnableTouch runnableTouch = this.mRunnableTouchMap.get(i);
        if (runnableTouch == null) {
            RunnableTouch runnableTouch2 = new RunnableTouch(i);
            this.mRunnableTouchMap.put(i, runnableTouch2);
            return runnableTouch2;
        }
        return runnableTouch;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RunnableTimeOut implements Runnable {
        private int type;

        RunnableTimeOut(int i) {
            this.type = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiFloatingLayer vuiFloatingLayer = VuiFloatingLayer.this;
            vuiFloatingLayer.log("RunnableTimeOut type " + this.type);
            VuiFloatingLayer.this.getVuiFloatingView(this.type).requestNeedReLoadDrawable();
            VuiFloatingLayer.this.removeView(this.type);
        }
    }

    private RunnableTimeOut getRunnableTimeOut(int i) {
        RunnableTimeOut runnableTimeOut = this.mRunnableTimeOutMap.get(i);
        if (runnableTimeOut == null) {
            RunnableTimeOut runnableTimeOut2 = new RunnableTimeOut(i);
            this.mRunnableTimeOutMap.put(i, runnableTimeOut2);
            return runnableTimeOut2;
        }
        return runnableTimeOut;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AnimationCallback implements VuiFloatingView.Callback {
        private int type;

        AnimationCallback(int i) {
            this.type = i;
        }

        @Override // com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView.Callback
        public void onAnimationStart(VuiFloatingView vuiFloatingView) {
            VuiFloatingLayer.this.log("onAnimationStart ");
        }

        @Override // com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView.Callback
        public void onAnimationEnd(VuiFloatingView vuiFloatingView) {
            VuiFloatingLayer.this.log("onAnimationEnd ");
            VuiFloatingLayer.this.removeView(this.type);
        }
    }

    private void setFloatingLocation(VuiFloatingView vuiFloatingView, LayerInfo layerInfo, int i) {
        if (layerInfo == null) {
            return;
        }
        int visibleWidth = vuiFloatingView.getVisibleWidth();
        int visibleHeight = vuiFloatingView.getVisibleHeight();
        WindowManager.LayoutParams layoutParams = this.mLayoutParams;
        layoutParams.width = visibleWidth;
        layoutParams.height = visibleHeight;
        int[] location = VuiFloatingLocationUtils.getLocation(i, layerInfo, visibleWidth, visibleHeight);
        WindowManager.LayoutParams layoutParams2 = this.mLayoutParams;
        layoutParams2.x = location[0];
        layoutParams2.y = location[1];
        vuiFloatingView.setLocation(layoutParams2.x, layoutParams2.y);
        addView(vuiFloatingView, this.mLayoutParams, i);
    }

    @Override // com.xiaopeng.xui.vui.floatinglayer.IVuiFloatingLayer
    public void showFloatingLayer(View view, final int i, int i2, int i3) {
        log_I("showFloatingLayer...type : " + i + " , view " + view);
        this.mHandler.removeCallbacks(getRunnableTouch(i));
        this.mHandler.removeCallbacks(getRunnableTimeOut(i));
        LayerInfo layerInfo = this.mLayerInfoMap.get(i);
        if (layerInfo == null) {
            layerInfo = new LayerInfo();
            this.mLayerInfoMap.put(i, layerInfo);
        }
        layerInfo.targetWidth = view.getMeasuredWidth();
        layerInfo.targetHeight = view.getMeasuredHeight();
        layerInfo.mCenterOffsetX = i2;
        layerInfo.mCenterOffsetY = i3;
        layerInfo.location = new int[2];
        view.getLocationOnScreen(layerInfo.location);
        log(layerInfo.toString());
        final VuiFloatingView vuiFloatingView = getVuiFloatingView(i);
        final AnimationCallback animationCallback = getAnimationCallback(i);
        vuiFloatingView.unRegisterAnimationCallback();
        vuiFloatingView.postDelayed(new Runnable() { // from class: com.xiaopeng.xui.vui.floatinglayer.-$$Lambda$VuiFloatingLayer$PZ_K1OqtUY7adEnl6_SElfFSV0c
            @Override // java.lang.Runnable
            public final void run() {
                VuiFloatingLayer.this.lambda$showFloatingLayer$1$VuiFloatingLayer(i, vuiFloatingView, animationCallback);
            }
        }, 500L);
        removeView(i);
        vuiFloatingView.prepare();
        setFloatingLocation(vuiFloatingView, layerInfo, i);
        startAnimation(vuiFloatingView);
    }

    public /* synthetic */ void lambda$showFloatingLayer$1$VuiFloatingLayer(int i, VuiFloatingView vuiFloatingView, AnimationCallback animationCallback) {
        log("registerAnimationCallback...type : " + i);
        vuiFloatingView.registerAnimationCallback(animationCallback);
    }

    @Override // com.xiaopeng.xui.vui.floatinglayer.IVuiFloatingLayer
    public void hideFloatingLayer(int i) {
        log("hideFloatingLayer...type : " + i);
        stopAnimation(getVuiFloatingView(i));
        removeView(i);
    }

    private void addView(View view, WindowManager.LayoutParams layoutParams, int i) {
        if (view.getParent() == null) {
            this.mWindowManager.addView(view, layoutParams);
            log_I("add to window x : " + layoutParams.x + ", y :" + layoutParams.y + " , w : " + layoutParams.width + " ,h : " + layoutParams.height);
        } else {
            this.mWindowManager.updateViewLayout(view, layoutParams);
            log_I("update to window x : " + layoutParams.x + ", y :" + layoutParams.y + " , w : " + layoutParams.width + " ,h : " + layoutParams.height);
        }
        this.mHandler.postDelayed(getRunnableTimeOut(i), VuiImageDecoderUtils.getAnimateTimeOut(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeView(int i) {
        VuiFloatingView vuiFloatingView = getVuiFloatingView(i);
        stopAnimation(vuiFloatingView);
        if (vuiFloatingView != null && vuiFloatingView.getParent() != null) {
            this.mWindowManager.removeView(vuiFloatingView);
            this.mHandler.removeCallbacks(getRunnableTimeOut(i));
            log_I("remove window..type : " + i);
            return;
        }
        log("view is null or view all in window");
    }

    private void startAnimation(VuiFloatingView vuiFloatingView) {
        log("startAnimation...");
        if (vuiFloatingView != null) {
            vuiFloatingView.start();
        } else {
            log("view is null");
        }
    }

    private void stopAnimation(VuiFloatingView vuiFloatingView) {
        if (vuiFloatingView != null) {
            vuiFloatingView.stop();
        } else {
            log("view is null");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        XLogUtils.d(TAG, str);
    }

    private void log_I(String str) {
        XLogUtils.i(TAG, str);
    }
}
