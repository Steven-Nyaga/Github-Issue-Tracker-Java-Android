package com.example.githubissuetracker.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.githubissuetracker.R;

public class DetailsDialog extends AppCompatDialogFragment {

    private EditText userName, userRepo;
    private DetailsDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_details, null);

        builder.setView(view)
                .setTitle("GitHub Details")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String user = userName.getText().toString();
                        String repo = userRepo.getText().toString();
                        listener.applyDetails(user,repo);
                    }
                });

        AlertDialog alert = builder.create();
        alert.setOnShowListener(arg0 -> {
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.dark_gray));
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
        });

        userName = view.findViewById(R.id.userName);
        userRepo = view.findViewById(R.id.userRepo);

        return alert;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DetailsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement CommentsDialogListener");
        }
    }

    public interface DetailsDialogListener{
        void applyDetails(String user, String repo);
    }
}
