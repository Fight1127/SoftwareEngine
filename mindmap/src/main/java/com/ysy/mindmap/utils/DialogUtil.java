package com.ysy.mindmap.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.ysy.mindmap.views.WaitDialog;

/**
 * Created by Sylvester on 17/4/18.
 */

public class DialogUtil {

    private Dialog waitDialog;
    private Context context;

    public DialogUtil(Context context) {
        this.context = context;
    }

    public Dialog showWaitDialog(String content) {
        waitDialog = new WaitDialog(context).show(content);
        return waitDialog;
    }

    public Dialog showWaitDialog() {
        waitDialog = new WaitDialog(context).show("");
        return waitDialog;
    }

    public void showTipDialog(String title, String msg, String posText, String negText,
                              DialogInterface.OnClickListener posClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setPositiveButton(posText, posClickListener)
                .setNegativeButton(negText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        builder.show();
    }
}
