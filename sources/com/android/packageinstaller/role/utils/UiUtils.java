package com.android.packageinstaller.role.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
/* loaded from: classes.dex */
public class UiUtils {
    public static float dpToPx(float f, Context context) {
        return TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }

    public static int dpToPxOffset(float f, Context context) {
        return (int) dpToPx(f, context);
    }

    public static void setViewShown(final View view, boolean z) {
        if (z && view.getVisibility() == 0 && view.getAlpha() == 1.0f) {
            view.animate().alpha(1.0f).setDuration(0L);
        } else if (!z && (view.getVisibility() != 0 || view.getAlpha() == 0.0f)) {
            view.animate().alpha(0.0f).setDuration(0L);
            view.setVisibility(4);
        } else {
            if (z && view.getVisibility() != 0) {
                view.setAlpha(0.0f);
                view.setVisibility(0);
            }
            view.animate().alpha(z ? 1.0f : 0.0f).setDuration(view.getResources().getInteger(17694721)).setInterpolator(AnimationUtils.loadInterpolator(view.getContext(), z ? 17563661 : 17563663)).setListener(z ? null : new AnimatorListenerAdapter() { // from class: com.android.packageinstaller.role.utils.UiUtils.1
                private boolean mCanceled = false;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    this.mCanceled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (this.mCanceled) {
                        return;
                    }
                    view.setVisibility(4);
                }
            });
        }
    }
}
