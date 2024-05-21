package com.android.car.ui.preference;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.preference.EditTextPreference;
/* loaded from: classes.dex */
public class EditTextPreferenceDialogFragment extends PreferenceDialogFragment implements TextView.OnEditorActionListener {
    private static final String SAVE_STATE_TEXT = "EditTextPreferenceDialogFragment.text";
    private boolean mAllowEnterToSubmit = true;
    private EditText mEditText;
    private CharSequence mText;

    @Override // com.android.car.ui.preference.CarUiDialogFragment
    protected boolean needInputMethod() {
        return true;
    }

    public static EditTextPreferenceDialogFragment newInstance(String str) {
        EditTextPreferenceDialogFragment editTextPreferenceDialogFragment = new EditTextPreferenceDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", str);
        editTextPreferenceDialogFragment.setArguments(bundle);
        return editTextPreferenceDialogFragment;
    }

    @Override // com.android.car.ui.preference.PreferenceDialogFragment, com.android.car.ui.preference.CarUiDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            this.mText = getEditTextPreference().getText();
        } else {
            this.mText = bundle.getCharSequence(SAVE_STATE_TEXT);
        }
    }

    @Override // com.android.car.ui.preference.CarUiDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putCharSequence(SAVE_STATE_TEXT, this.mText);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.car.ui.preference.CarUiDialogFragment
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        this.mEditText = (EditText) view.findViewById(16908291);
        EditText editText = this.mEditText;
        if (editText == null) {
            throw new IllegalStateException("Dialog view must contain an EditText with id @android:id/edit");
        }
        editText.requestFocus();
        this.mEditText.setText(this.mText);
        this.mEditText.setInputType(1);
        this.mEditText.setImeOptions(6);
        this.mEditText.setOnEditorActionListener(this);
        EditText editText2 = this.mEditText;
        editText2.setSelection(editText2.getText().length());
    }

    private EditTextPreference getEditTextPreference() {
        return (EditTextPreference) getPreference();
    }

    @Override // com.android.car.ui.preference.CarUiDialogFragment
    protected void onDialogClosed(boolean z) {
        if (z) {
            String obj = this.mEditText.getText().toString();
            if (getEditTextPreference().callChangeListener(obj)) {
                getEditTextPreference().setText(obj);
            }
        }
    }

    public void setAllowEnterToSubmit(boolean z) {
        this.mAllowEnterToSubmit = z;
    }

    public boolean getAllowEnterToSubmit() {
        return this.mAllowEnterToSubmit;
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6 && this.mAllowEnterToSubmit) {
            getEditTextPreference().callChangeListener(textView.getText());
            dismiss();
            return true;
        }
        return false;
    }
}
