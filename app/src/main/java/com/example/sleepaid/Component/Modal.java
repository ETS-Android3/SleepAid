package com.example.sleepaid.Component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Modal {
    public Modal() {}

    public static void show(
            Context context,
            String message,
            String positiveButton,
            DialogInterface.OnClickListener positiveAction,
            String negativeButton,
            DialogInterface.OnClickListener negativeAction)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);

        if (positiveButton != null) {
            alert.setPositiveButton(positiveButton, positiveAction);
        }

        if (negativeButton != null) {
            alert.setNegativeButton(negativeButton, negativeAction);
        }

        AlertDialog alertDialog = alert.create();
        alertDialog.show();

        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView messageComponent = alertDialog.findViewById(android.R.id.message);
        messageComponent.setTextSize(18);

        Button negButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,20,0);
        negButton.setLayoutParams(params);
    }
}
