package com.android.car.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.car.ui.recyclerview.CarUiListItemAdapter;
import com.android.car.ui.recyclerview.CarUiRadioButtonListItemAdapter;
/* loaded from: classes.dex */
public class AlertDialogBuilder {
    private AlertDialog.Builder mBuilder;
    private Context mContext;
    private Drawable mIcon;
    private boolean mNegativeButtonSet;
    private boolean mNeutralButtonSet;
    private boolean mPositiveButtonSet;
    private CharSequence mSubtitle;
    private CharSequence mTitle;

    public AlertDialogBuilder(Context context) {
        this(context, 0);
    }

    public AlertDialogBuilder(Context context, int i) {
        this.mBuilder = new AlertDialog.Builder(context, i);
        this.mContext = context;
    }

    public Context getContext() {
        return this.mBuilder.getContext();
    }

    public AlertDialogBuilder setTitle(int i) {
        return setTitle(this.mContext.getText(i));
    }

    public AlertDialogBuilder setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        this.mBuilder.setTitle(charSequence);
        return this;
    }

    public AlertDialogBuilder setSubtitle(int i) {
        return setSubtitle(this.mContext.getString(i));
    }

    public AlertDialogBuilder setSubtitle(CharSequence charSequence) {
        this.mSubtitle = charSequence;
        return this;
    }

    public AlertDialogBuilder setMessage(int i) {
        this.mBuilder.setMessage(i);
        return this;
    }

    public AlertDialogBuilder setMessage(CharSequence charSequence) {
        this.mBuilder.setMessage(charSequence);
        return this;
    }

    public AlertDialogBuilder setIcon(int i) {
        return setIcon(this.mContext.getDrawable(i));
    }

    public AlertDialogBuilder setIcon(Drawable drawable) {
        this.mIcon = drawable;
        this.mBuilder.setIcon(drawable);
        return this;
    }

    public AlertDialogBuilder setIconAttribute(int i) {
        this.mBuilder.setIconAttribute(i);
        return this;
    }

    public AlertDialogBuilder setPositiveButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setPositiveButton(i, onClickListener);
        this.mPositiveButtonSet = true;
        return this;
    }

    public AlertDialogBuilder setPositiveButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setPositiveButton(charSequence, onClickListener);
        this.mPositiveButtonSet = true;
        return this;
    }

    public AlertDialogBuilder setNegativeButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setNegativeButton(i, onClickListener);
        this.mNegativeButtonSet = true;
        return this;
    }

    public AlertDialogBuilder setNegativeButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setNegativeButton(charSequence, onClickListener);
        this.mNegativeButtonSet = true;
        return this;
    }

    public AlertDialogBuilder setNeutralButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setNeutralButton(i, onClickListener);
        this.mNeutralButtonSet = true;
        return this;
    }

    public AlertDialogBuilder setNeutralButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setNeutralButton(charSequence, onClickListener);
        this.mNeutralButtonSet = true;
        return this;
    }

    public AlertDialogBuilder setCancelable(boolean z) {
        this.mBuilder.setCancelable(z);
        return this;
    }

    public AlertDialogBuilder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mBuilder.setOnCancelListener(onCancelListener);
        return this;
    }

    public AlertDialogBuilder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mBuilder.setOnDismissListener(onDismissListener);
        return this;
    }

    public AlertDialogBuilder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.mBuilder.setOnKeyListener(onKeyListener);
        return this;
    }

    public AlertDialogBuilder setItems(int i, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setItems(i, onClickListener);
        return this;
    }

    public AlertDialogBuilder setItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setItems(charSequenceArr, onClickListener);
        return this;
    }

    @Deprecated
    public AlertDialogBuilder setAdapter(ListAdapter listAdapter, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setAdapter(listAdapter, onClickListener);
        return this;
    }

    public AlertDialogBuilder setAdapter(CarUiListItemAdapter carUiListItemAdapter) {
        setCustomList(carUiListItemAdapter);
        return this;
    }

    private void setCustomList(CarUiListItemAdapter carUiListItemAdapter) {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.car_ui_alert_dialog_list, (ViewGroup) null);
        RecyclerView recyclerView = (RecyclerView) inflate.requireViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.mContext));
        recyclerView.setAdapter(carUiListItemAdapter);
        this.mBuilder.setView(inflate);
    }

    public AlertDialogBuilder setCursor(Cursor cursor, DialogInterface.OnClickListener onClickListener, String str) {
        this.mBuilder.setCursor(cursor, onClickListener, str);
        return this;
    }

    public AlertDialogBuilder setMultiChoiceItems(int i, boolean[] zArr, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.mBuilder.setMultiChoiceItems(i, zArr, onMultiChoiceClickListener);
        return this;
    }

    public AlertDialogBuilder setMultiChoiceItems(CharSequence[] charSequenceArr, boolean[] zArr, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.mBuilder.setMultiChoiceItems(charSequenceArr, zArr, onMultiChoiceClickListener);
        return this;
    }

    public AlertDialogBuilder setMultiChoiceItems(Cursor cursor, String str, String str2, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.mBuilder.setMultiChoiceItems(cursor, str, str2, onMultiChoiceClickListener);
        return this;
    }

    public AlertDialogBuilder setSingleChoiceItems(int i, int i2, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setSingleChoiceItems(i, i2, onClickListener);
        return this;
    }

    public AlertDialogBuilder setSingleChoiceItems(Cursor cursor, int i, String str, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setSingleChoiceItems(cursor, i, str, onClickListener);
        return this;
    }

    public AlertDialogBuilder setSingleChoiceItems(CharSequence[] charSequenceArr, int i, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setSingleChoiceItems(charSequenceArr, i, onClickListener);
        return this;
    }

    @Deprecated
    public AlertDialogBuilder setSingleChoiceItems(ListAdapter listAdapter, int i, DialogInterface.OnClickListener onClickListener) {
        this.mBuilder.setSingleChoiceItems(listAdapter, i, onClickListener);
        return this;
    }

    @Deprecated
    public AlertDialogBuilder setSingleChoiceItems(CarUiRadioButtonListItemAdapter carUiRadioButtonListItemAdapter, DialogInterface.OnClickListener onClickListener) {
        setCustomList(carUiRadioButtonListItemAdapter);
        return this;
    }

    public AlertDialogBuilder setSingleChoiceItems(CarUiRadioButtonListItemAdapter carUiRadioButtonListItemAdapter) {
        setCustomList(carUiRadioButtonListItemAdapter);
        return this;
    }

    public AlertDialogBuilder setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.mBuilder.setOnItemSelectedListener(onItemSelectedListener);
        return this;
    }

    public AlertDialogBuilder setEditBox(String str, TextWatcher textWatcher, InputFilter[] inputFilterArr, int i) {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.car_ui_alert_dialog_edit_text, (ViewGroup) null);
        EditText editText = (EditText) inflate.requireViewById(R.id.textbox);
        editText.setText(str);
        if (textWatcher != null) {
            editText.addTextChangedListener(textWatcher);
        }
        if (inputFilterArr != null) {
            editText.setFilters(inputFilterArr);
        }
        if (i != 0) {
            editText.setInputType(i);
        }
        this.mBuilder.setView(inflate);
        return this;
    }

    public AlertDialogBuilder setEditBox(String str, TextWatcher textWatcher, InputFilter[] inputFilterArr) {
        return setEditBox(str, textWatcher, inputFilterArr, 0);
    }

    private void prepareDialog() {
        if (this.mSubtitle != null) {
            View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.car_ui_alert_dialog_title_with_subtitle, (ViewGroup) null);
            ImageView imageView = (ImageView) inflate.requireViewById(R.id.icon);
            ((TextView) inflate.requireViewById(R.id.alertTitle)).setText(this.mTitle);
            ((TextView) inflate.requireViewById(R.id.alertSubtitle)).setText(this.mSubtitle);
            imageView.setImageDrawable(this.mIcon);
            imageView.setVisibility(this.mIcon != null ? 0 : 8);
            this.mBuilder.setCustomTitle(inflate);
        }
        if (this.mNeutralButtonSet || this.mNegativeButtonSet || this.mPositiveButtonSet) {
            return;
        }
        this.mBuilder.setNegativeButton(this.mContext.getString(R.string.car_ui_alert_dialog_default_button), new DialogInterface.OnClickListener() { // from class: com.android.car.ui.-$$Lambda$AlertDialogBuilder$ZoAyUi43EP675R0Los9Z7-Rvdzk
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }

    public AlertDialog create() {
        prepareDialog();
        return this.mBuilder.create();
    }

    public AlertDialog show() {
        prepareDialog();
        return this.mBuilder.show();
    }
}
