package com.ysy.mindmap.bases;

import android.app.Application;

import com.ysy.mindmap.utils.dbconnector.SQLClient;
import com.ysy.mindmap.utils.dbconnector.SQLClientConfig;

/**
 * Created by Sylvester on 17/5/3.
 */

public class GlobalApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SQLClientConfig config = new SQLClientConfig(getApplicationContext());
        SQLClient.init(config);
    }
}
