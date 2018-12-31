package com.example.ericgrevillius.p4pathfinder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FingerprintQuestionFragment extends DialogFragment {
    private static final String TAG = "FingerprintDialogFragment";
    private OnDialogListener onDialogListener;
    private boolean setFingerprintLogin = false;

    public static FingerprintQuestionFragment newInstance() {
        Bundle args = new Bundle();
        FingerprintQuestionFragment fragment = new FingerprintQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Question");
        alertDialogBuilder.setMessage("Enable Fingerprint login for this user?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setFingerprintLogin = true;
                onDialogListener.setFingerprintLogin(setFingerprintLogin);
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onDialogListener.setFingerprintLogin(setFingerprintLogin);
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.create().show();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setOnDialogListener(OnDialogListener onDialogListener) {
        this.onDialogListener = onDialogListener;
    }

    public interface OnDialogListener {
        void setFingerprintLogin(boolean hasFingerprint);
    }
}
