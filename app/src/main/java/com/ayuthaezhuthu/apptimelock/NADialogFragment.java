package com.ayuthaezhuthu.apptimelock;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.ayuthaezhuthu.apptimelock.listeners.DialogActionListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class NADialogFragment extends DialogFragment {

        public static NADialogFragment newInstance(int msg) {
            NADialogFragment frag = new NADialogFragment();
            Bundle args = new Bundle();
            args.putInt("msg", msg);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("msg");

            return new AlertDialog.Builder(getActivity())
                    .setMessage(title)
                    .setPositiveButton(R.string.positive_btn_usage_permission,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((DialogActionListener)getActivity()).doPositiveClick();
                                }
                            }
                    )
                    .setNegativeButton(R.string.negative_btn_usage_permission,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((DialogActionListener)getActivity()).doNegativeClick();
                                }
                            }
                    ).setCancelable(false)
                    .create();
        }


}
