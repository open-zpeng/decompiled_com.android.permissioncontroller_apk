package com.android.car.ui.baselayout;

import java.util.Objects;
/* loaded from: classes.dex */
public final class Insets {
    private final int mBottom;
    private final int mLeft;
    private final int mRight;
    private final int mTop;

    public Insets() {
        this.mBottom = 0;
        this.mTop = 0;
        this.mRight = 0;
        this.mLeft = 0;
    }

    public Insets(int i, int i2, int i3, int i4) {
        this.mLeft = i;
        this.mRight = i3;
        this.mTop = i2;
        this.mBottom = i4;
    }

    public int getLeft() {
        return this.mLeft;
    }

    public int getRight() {
        return this.mRight;
    }

    public int getTop() {
        return this.mTop;
    }

    public int getBottom() {
        return this.mBottom;
    }

    public String toString() {
        return "{ left: " + this.mLeft + ", right: " + this.mRight + ", top: " + this.mTop + ", bottom: " + this.mBottom + " }";
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || Insets.class != obj.getClass()) {
            return false;
        }
        Insets insets = (Insets) obj;
        return this.mLeft == insets.mLeft && this.mRight == insets.mRight && this.mTop == insets.mTop && this.mBottom == insets.mBottom;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mLeft), Integer.valueOf(this.mRight), Integer.valueOf(this.mTop), Integer.valueOf(this.mBottom));
    }
}
