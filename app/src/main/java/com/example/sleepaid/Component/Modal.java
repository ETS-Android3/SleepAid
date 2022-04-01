package com.example.sleepaid.Component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

public class Modal {
    public Modal() {}

    public static void show(
            Context context,
            String title,
            String positiveButton,
            DialogInterface.OnClickListener positiveAction,
            String negativeButton,
            DialogInterface.OnClickListener negativeAction)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);

        //TODO fix the styling for the buttons in dark mode
        if (positiveButton != null) {
            alert.setPositiveButton(positiveButton, positiveAction);
        }

        if (negativeButton != null) {
            alert.setNegativeButton(negativeButton, negativeAction);
        }

        AlertDialog alertDialog = alert.create();

        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}
