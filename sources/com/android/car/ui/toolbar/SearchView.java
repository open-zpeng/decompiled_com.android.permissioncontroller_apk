package com.android.car.ui.toolbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.android.car.ui.R;
import com.android.car.ui.toolbar.Toolbar;
import com.android.car.ui.utils.CarUiUtils;
import java.util.Collections;
import java.util.Set;
/* loaded from: classes.dex */
public class SearchView extends ConstraintLayout {
    private final View mCloseIcon;
    private final int mEndPadding;
    private final ImageView mIcon;
    private final InputMethodManager mInputMethodManager;
    private boolean mIsPlainText;
    private Set<Toolbar.OnSearchCompletedListener> mSearchCompletedListeners;
    private Set<Toolbar.OnSearchListener> mSearchListeners;
    private final EditText mSearchText;
    private final int mStartPadding;
    private final int mStartPaddingWithoutIcon;
    private final TextWatcher mTextWatcher;
    private boolean mWasShown;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SearchView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSearchListeners = Collections.emptySet();
        this.mSearchCompletedListeners = Collections.emptySet();
        this.mTextWatcher = new TextWatcher() { // from class: com.android.car.ui.toolbar.SearchView.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                SearchView.this.onSearch(editable.toString());
            }
        };
        this.mIsPlainText = false;
        this.mWasShown = false;
        this.mInputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
        LayoutInflater.from(context).inflate(R.layout.car_ui_toolbar_search_view, (ViewGroup) this, true);
        this.mSearchText = (EditText) CarUiUtils.requireViewByRefId(this, R.id.car_ui_toolbar_search_bar);
        this.mIcon = (ImageView) CarUiUtils.requireViewByRefId(this, R.id.car_ui_toolbar_search_icon);
        this.mCloseIcon = CarUiUtils.requireViewByRefId(this, R.id.car_ui_toolbar_search_close);
        this.mCloseIcon.setOnClickListener(new View.OnClickListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$SearchView$X_FGZTNuIRv-v80Y2K7F5FfOzQE
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SearchView.this.lambda$new$0$SearchView(view);
            }
        });
        this.mCloseIcon.setVisibility(8);
        this.mStartPaddingWithoutIcon = this.mSearchText.getPaddingStart();
        this.mStartPadding = context.getResources().getDimensionPixelSize(R.dimen.car_ui_toolbar_search_search_icon_container_width);
        this.mEndPadding = context.getResources().getDimensionPixelSize(R.dimen.car_ui_toolbar_search_close_icon_container_width);
        this.mSearchText.setSaveEnabled(false);
        this.mSearchText.setPaddingRelative(this.mStartPadding, 0, this.mEndPadding, 0);
        this.mSearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$SearchView$X7fsaGo_U9eNegbbmVXAWpIsYEw
            @Override // android.view.View.OnFocusChangeListener
            public final void onFocusChange(View view, boolean z) {
                SearchView.this.lambda$new$1$SearchView(view, z);
            }
        });
        this.mSearchText.addTextChangedListener(this.mTextWatcher);
        this.mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.android.car.ui.toolbar.-$$Lambda$SearchView$FG6VZSqwor2f82_F3UfmTiH0ZHc
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                return SearchView.this.lambda$new$2$SearchView(textView, i2, keyEvent);
            }
        });
    }

    public /* synthetic */ void lambda$new$0$SearchView(View view) {
        this.mSearchText.getText().clear();
    }

    public /* synthetic */ void lambda$new$1$SearchView(View view, boolean z) {
        if (z) {
            this.mInputMethodManager.showSoftInput(view, 0);
        } else {
            this.mInputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public /* synthetic */ boolean lambda$new$2$SearchView(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6 || i == 3) {
            this.mSearchText.clearFocus();
            for (Toolbar.OnSearchCompletedListener onSearchCompletedListener : this.mSearchCompletedListeners) {
                onSearchCompletedListener.onSearchCompleted();
            }
            return false;
        }
        return false;
    }

    @Override // android.view.View
    public void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        boolean isShown = isShown();
        if (isShown && !this.mWasShown) {
            this.mCloseIcon.setVisibility(this.mSearchText.getText().length() > 0 ? 0 : 8);
            this.mSearchText.requestFocus();
        }
        this.mWasShown = isShown;
    }

    public void setSearchListeners(Set<Toolbar.OnSearchListener> set) {
        this.mSearchListeners = set;
    }

    public void setSearchCompletedListeners(Set<Toolbar.OnSearchCompletedListener> set) {
        this.mSearchCompletedListeners = set;
    }

    public void setHint(int i) {
        this.mSearchText.setHint(i);
    }

    public void setHint(CharSequence charSequence) {
        this.mSearchText.setHint(charSequence);
    }

    public CharSequence getHint() {
        return this.mSearchText.getHint();
    }

    public void setIcon(Drawable drawable) {
        if (drawable == null) {
            this.mIcon.setImageResource(R.drawable.car_ui_icon_search);
        } else {
            this.mIcon.setImageDrawable(drawable);
        }
    }

    public void setIcon(int i) {
        if (i == 0) {
            this.mIcon.setImageResource(R.drawable.car_ui_icon_search);
        } else {
            this.mIcon.setImageResource(i);
        }
    }

    public void setPlainText(boolean z) {
        if (z != this.mIsPlainText) {
            if (z) {
                this.mSearchText.setPaddingRelative(this.mStartPaddingWithoutIcon, 0, this.mEndPadding, 0);
                this.mSearchText.setImeOptions(6);
                this.mIcon.setVisibility(8);
            } else {
                this.mSearchText.setPaddingRelative(this.mStartPadding, 0, this.mEndPadding, 0);
                this.mSearchText.setImeOptions(3);
                this.mIcon.setVisibility(0);
            }
            this.mIsPlainText = z;
            this.mInputMethodManager.restartInput(this.mSearchText);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSearch(String str) {
        this.mCloseIcon.setVisibility(TextUtils.isEmpty(str) ? 8 : 0);
        for (Toolbar.OnSearchListener onSearchListener : this.mSearchListeners) {
            onSearchListener.onSearch(str);
        }
    }

    public void setSearchQuery(String str) {
        this.mSearchText.setText(str);
        EditText editText = this.mSearchText;
        editText.setSelection(editText.getText().length());
    }
}
