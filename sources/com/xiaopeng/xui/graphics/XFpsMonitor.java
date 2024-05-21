package com.xiaopeng.xui.graphics;

import com.xiaopeng.xui.utils.XLogUtils;
import java.util.Locale;
/* loaded from: classes.dex */
public class XFpsMonitor {
    private int currentFps;
    private String currentFpsInfo;
    private float currentTfp;
    private long lastLog;
    private final long logInterval;
    private long start;
    private int sumFrames;
    private long sumTime;
    private String tag;

    public XFpsMonitor() {
        this("XFpsMonitor");
    }

    public XFpsMonitor(String str) {
        this.tag = str;
        this.lastLog = -1L;
        this.logInterval = 1000L;
        this.start = -1L;
        this.currentFpsInfo = "";
    }

    public final void frameStart() {
        if (this.start != -1) {
            throw new RuntimeException("Do you forget to call frameEnd? ");
        }
        this.start = System.currentTimeMillis();
        if (this.lastLog == -1) {
            this.lastLog = this.start;
        }
    }

    public final String frameEnd() {
        if (this.start == -1) {
            throw new RuntimeException("Do you forget to call frameStart? ");
        }
        long currentTimeMillis = System.currentTimeMillis();
        this.sumTime += currentTimeMillis - this.start;
        this.sumFrames++;
        if (currentTimeMillis - this.lastLog > this.logInterval) {
            this.currentFps = this.sumFrames;
            StringBuilder sb = new StringBuilder();
            sb.append(this.sumFrames);
            sb.append(" FPS\t\t");
            this.currentTfp = (((float) this.sumTime) * 1.0f) / this.sumFrames;
            sb.append(String.format(Locale.getDefault(), "%.2f", Float.valueOf(this.currentTfp)));
            sb.append(" ms/f");
            this.currentFpsInfo = sb.toString();
            XLogUtils.d(this.tag, this.currentFpsInfo);
            this.sumTime = 0L;
            this.sumFrames = 0;
            this.lastLog = currentTimeMillis;
        }
        this.start = -1L;
        return this.currentFpsInfo;
    }

    public final String getCurrentFpsInfo() {
        return this.currentFpsInfo;
    }

    public final float getCurrentTfp() {
        return this.currentTfp;
    }

    public final int getCurrentFps() {
        return this.currentFps;
    }
}
