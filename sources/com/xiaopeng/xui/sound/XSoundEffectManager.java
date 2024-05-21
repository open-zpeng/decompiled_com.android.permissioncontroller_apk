package com.xiaopeng.xui.sound;

import com.xiaopeng.xui.utils.XLogUtils;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes.dex */
public class XSoundEffectManager {
    private static final String TAG = "xpui-XSoundManager";
    private ExecutorService mExecutorService;
    private boolean mIsDestroy;
    private ConcurrentHashMap<Integer, XSoundEffect> mPoolHashMap;
    private ConcurrentHashMap<Integer, Boolean> mReleaseMap;

    /* loaded from: classes.dex */
    private static class SingletonHolder {
        private static final XSoundEffectManager INSTANCE = new XSoundEffectManager();

        private SingletonHolder() {
        }
    }

    private XSoundEffectManager() {
        this.mPoolHashMap = new ConcurrentHashMap<>();
        this.mExecutorService = Executors.newSingleThreadExecutor();
        this.mReleaseMap = new ConcurrentHashMap<>();
    }

    public static XSoundEffectManager get() {
        return SingletonHolder.INSTANCE;
    }

    public synchronized void play(final int i) {
        this.mIsDestroy = false;
        this.mReleaseMap.put(Integer.valueOf(i), false);
        this.mExecutorService.execute(new Runnable() { // from class: com.xiaopeng.xui.sound.-$$Lambda$XSoundEffectManager$SlLZzyLmP_q9Jgw7EoPYHagmYdY
            @Override // java.lang.Runnable
            public final void run() {
                XSoundEffectManager.this.lambda$play$0$XSoundEffectManager(i);
            }
        });
    }

    public /* synthetic */ void lambda$play$0$XSoundEffectManager(int i) {
        if (this.mIsDestroy) {
            log(String.format("play-not for destroy resource:%s", Integer.valueOf(i)));
            return;
        }
        Boolean bool = this.mReleaseMap.get(Integer.valueOf(i));
        if (bool != null && bool.booleanValue()) {
            log(String.format("play-not for release resource:%s", Integer.valueOf(i)));
            return;
        }
        System.currentTimeMillis();
        XSoundEffect xSoundEffect = this.mPoolHashMap.get(Integer.valueOf(i));
        if (xSoundEffect == null) {
            xSoundEffect = SoundEffectFactory.create(i);
            this.mPoolHashMap.put(Integer.valueOf(i), xSoundEffect);
        }
        xSoundEffect.play();
    }

    public synchronized void release(final int i) {
        this.mReleaseMap.put(Integer.valueOf(i), true);
        this.mExecutorService.execute(new Runnable() { // from class: com.xiaopeng.xui.sound.-$$Lambda$XSoundEffectManager$wIXGXmGDQzq-cwWNmTR5CyWKfoQ
            @Override // java.lang.Runnable
            public final void run() {
                XSoundEffectManager.this.lambda$release$1$XSoundEffectManager(i);
            }
        });
    }

    public /* synthetic */ void lambda$release$1$XSoundEffectManager(int i) {
        XSoundEffect xSoundEffect = this.mPoolHashMap.get(Integer.valueOf(i));
        if (xSoundEffect != null) {
            xSoundEffect.release();
        }
        log(String.format("release resource:%s", Integer.valueOf(i)));
    }

    public synchronized void destroy() {
        this.mIsDestroy = true;
        this.mExecutorService.execute(new Runnable() { // from class: com.xiaopeng.xui.sound.-$$Lambda$XSoundEffectManager$gAnWfvgV_N-93OUZ2dykrfal-MM
            @Override // java.lang.Runnable
            public final void run() {
                XSoundEffectManager.this.lambda$destroy$2$XSoundEffectManager();
            }
        });
    }

    public /* synthetic */ void lambda$destroy$2$XSoundEffectManager() {
        long currentTimeMillis = System.currentTimeMillis();
        for (XSoundEffect xSoundEffect : this.mPoolHashMap.values()) {
            if (xSoundEffect != null) {
                xSoundEffect.release();
            }
        }
        this.mPoolHashMap.clear();
        this.mReleaseMap.clear();
        log("destroy time : " + (System.currentTimeMillis() - currentTimeMillis));
    }

    public synchronized void resetResource(int i, String str, int i2) {
        resetResource(i, str, i2, 5);
    }

    public synchronized void resetResource(final int i, final String str, final int i2, final int i3) {
        this.mExecutorService.execute(new Runnable() { // from class: com.xiaopeng.xui.sound.-$$Lambda$XSoundEffectManager$zBcAqtI6-Ez0rykCABVb4QC0jtQ
            @Override // java.lang.Runnable
            public final void run() {
                XSoundEffectManager.this.lambda$resetResource$3$XSoundEffectManager(i, str, i2, i3);
            }
        });
    }

    public /* synthetic */ void lambda$resetResource$3$XSoundEffectManager(int i, String str, int i2, int i3) {
        SoundEffectFactory.resetResource(i, str, i2, i3);
        XSoundEffect xSoundEffect = this.mPoolHashMap.get(Integer.valueOf(i));
        if (xSoundEffect != null) {
            xSoundEffect.release();
            this.mPoolHashMap.remove(Integer.valueOf(i));
        }
        log(String.format("resetResource--resource:%s,path:%s,location:%s,streamType:%s", Integer.valueOf(i), str, Integer.valueOf(i2), Integer.valueOf(i3)));
    }

    public synchronized void restoreResource(final int i) {
        this.mExecutorService.execute(new Runnable() { // from class: com.xiaopeng.xui.sound.-$$Lambda$XSoundEffectManager$xVTBBr68UkEjCh3o-q9_G_PQBcY
            @Override // java.lang.Runnable
            public final void run() {
                XSoundEffectManager.this.lambda$restoreResource$4$XSoundEffectManager(i);
            }
        });
    }

    public /* synthetic */ void lambda$restoreResource$4$XSoundEffectManager(int i) {
        SoundEffectFactory.restoreResource(i);
        XSoundEffect xSoundEffect = this.mPoolHashMap.get(Integer.valueOf(i));
        if (xSoundEffect != null) {
            xSoundEffect.release();
            this.mPoolHashMap.remove(Integer.valueOf(i));
        }
        log(String.format("restoreResource resource:%s", Integer.valueOf(i)));
    }

    private void log(String str) {
        XLogUtils.d(TAG, str);
    }
}
