package com.firda.Life;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class MyDialog extends AppCompatDialogFragment {

    private EditText title;
    private EditText length;
    private MyDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Add job")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String inputTitle = title.getText().toString();
                        String inputLength = length.getText().toString();
                        mListener.addJob(inputTitle, inputLength);
                    }
                });
        title = view.findViewById(R.id.dialogTitleEditText);
        length = view.findViewById(R.id.dialogLengthEditText);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (MyDialogListener) context;
    }

    public interface MyDialogListener {
        void addJob(String title, String length);
    }
}
