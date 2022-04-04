package com.yawar.memo.language;

import android.app.Dialog;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AppCompatDialogFragment;

public class BottomSheetDialogFragment extends AppCompatDialogFragment {
    public BottomSheetDialogFragment() {
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(this.getActivity(), this.getTheme());
    }
}
