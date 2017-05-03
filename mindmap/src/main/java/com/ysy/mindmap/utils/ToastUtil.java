package com.ysy.mindmap.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by Sylvester on 17/4/18.
 */

public class ToastUtil {

    private boolean isShow = true;

    private Handler mHandler;
    private Context mContext;

    public ToastUtil(Context context) {
        isShow = true;
        mContext = context;
        mHandler = new Handler();
    }

    public void dismissToast() {
        isShow = false;
    }

    public void showToastShort(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void showToastLong(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void showToastDuration(final String msg, final int duration) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isShow) {
                    Toast.makeText(mContext, msg, duration).show();
                }
            }
        });
    }
}
