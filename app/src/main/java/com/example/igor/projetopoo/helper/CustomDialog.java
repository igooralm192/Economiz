package com.example.igor.projetopoo.helper;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CustomDialog extends Dialog {

    public Activity activity;
    public View dialogView;
    public Button positiveButton, negativeButton;

    public CustomDialog(Activity act, int idYes, int idNo, int dial) {

        super(act);
        this.activity = act;
        this.dialogView = findViewById(dial);
        this.positiveButton = findViewById(idYes);
        this.negativeButton = findViewById(idNo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
    }

}