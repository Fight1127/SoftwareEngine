package com.ysy.mindmap.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.ysy.mindmap.R;

/**
 * Created by Sylvester on 17/5/2.
 */

public class WaitDialog extends Dialog {

    private TextView mContentTv;

    public WaitDialog(Context context) {
        super(context, R.style.WaitDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait_dialog_layout);
        mContentTv = (TextView) findViewById(R.id.wait_dialog_tv);
    }

    public Dialog show(String content) {
        super.show();
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        if (!TextUtils.isEmpty(content)) {
            mContentTv.setText(content);
        }
        return this;
    }

    public void dismiss() {
        super.dismiss();
    }
}
