package com.example.sleepaid.Component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.view.WindowManager;
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

        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}
