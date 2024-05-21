package com.android.packageinstaller.role.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
/* loaded from: classes.dex */
public class DefaultAppConfirmationDialogFragment extends DialogFragment {
    private CharSequence mMessage;
    private String mPackageName;

    /* loaded from: classes.dex */
    public interface Listener {
        void setDefaultApp(String str);
    }

    public static DefaultAppConfirmationDialogFragment newInstance(String str, CharSequence charSequence) {
        DefaultAppConfirmationDialogFragment defaultAppConfirmationDialogFragment = new DefaultAppConfirmationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("android.intent.extra.PACKAGE_NAME", str);
        bundle.putCharSequence("android.intent.extra.TEXT", charSequence);
        defaultAppConfirmationDialogFragment.setArguments(bundle);
        return defaultAppConfirmationDialogFragment;
    }

    public static void show(String str, CharSequence charSequence, Fragment fragment) {
        newInstance(str, charSequence).show(fragment.getChildFragmentManager(), (String) null);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mPackageName = arguments.getString("android.intent.extra.PACKAGE_NAME");
        this.mMessage = arguments.getCharSequence("android.intent.extra.TEXT");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(requireContext(), getTheme()).setMessage(this.mMessage).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.packageinstaller.role.ui.-$$Lambda$DefaultAppConfirmationDialogFragment$JruRZngxph6lkFb8TnsyEdFq41A
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                DefaultAppConfirmationDialogFragment.this.lambda$onCreateDialog$0$DefaultAppConfirmationDialogFragment(dialogInterface, i);
            }
        }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }

    public /* synthetic */ void lambda$onCreateDialog$0$DefaultAppConfirmationDialogFragment(DialogInterface dialogInterface, int i) {
        onOk();
    }

    private void onOk() {
        ((Listener) getParentFragment()).setDefaultApp(this.mPackageName);
    }
}
