package com.example.natis.hagana.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;

public class MyProgressBar {
    private ProgressDialog progressDialog;
    private Context context;

    public MyProgressBar(Context context) {
        this.context=context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
    }

    public void showProgressDialog() {
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
