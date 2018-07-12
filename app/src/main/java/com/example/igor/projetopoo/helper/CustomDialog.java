package com.example.igor.projetopoo.helper;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

public class CustomDialog extends Dialog {

    public Activity activity;
    public int dialogView;

    public CustomDialog(Activity act, int dial) {

        super(act);
        this.activity = act;
        this.dialogView = dial;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(dialogView);
    }

}