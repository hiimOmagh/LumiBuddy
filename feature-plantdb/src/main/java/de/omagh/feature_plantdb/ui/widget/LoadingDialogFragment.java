package de.omagh.feature_plantdb.ui.widget;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.widget.ProgressBar;
import android.widget.FrameLayout;

/**
 * DialogFragment with an indeterminate progress bar.
 */
public class LoadingDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        ProgressBar bar = new ProgressBar(requireContext());
        FrameLayout container = new FrameLayout(requireContext());
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);
        container.addView(bar);
        return new MaterialAlertDialogBuilder(requireContext())
                .setView(container)
                .setCancelable(false)
                .create();
    }
}
