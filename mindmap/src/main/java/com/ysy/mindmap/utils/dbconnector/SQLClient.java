package com.ysy.mindmap.utils.dbconnector;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sylvester on 17/4/18.
 */

public class SQLClient {

    @SuppressLint("StaticFieldLeak")
    static SQLClientConfig config;

    // 存放每个Activity对应的RequestManager
    private static Map<Activity, SQLRequestManager> managerMap;

    /**
     * 初始化
     *
     * @param config 全局配置信息
     */
    public static void init(SQLClientConfig config) {
        SQLClient.config = config;
        managerMap = new HashMap<>();
        // 初始化线程池
        SQLRequestThreadPool.init();
    }

    /**
     * 执行SQL请求
     *
     * @param activity 发起SQL请求的Activity
     * @param callBack SQL请求执行完毕后的回调接口
     */
    public static SQLRequest invokeStringRequest(Activity activity,
                                                 SQLEntity<String> entity, SQLCallback<ResultSet> callBack) {
        // 获取该activity对应的RequestManager对象，并创建SQLRequest对象
        SQLRequestManager manager = checkRequestManager(activity, true);
        SQLRequest request = manager.createStringRequest(entity, callBack);
        // 执行请求
        SQLRequestThreadPool.execute(request);
        return request;
    }

    public static SQLRequest invokeListRequest(Activity activity,
                                               SQLEntity<List<String>> entity, SQLCallback<List<ResultSet>> callBack) {
        SQLRequestManager manager = checkRequestManager(activity, true);
        SQLRequest request = manager.createListRequest(entity, callBack);
        SQLRequestThreadPool.execute(request);
        return request;
    }

    /**
     * 取消指定Activity中发起的所有HTTP请求
     */
    public static void cancelAllRequest(Activity activity) {
        SQLRequestManager requestManager = checkRequestManager(activity, false);
        if (requestManager != null)
            requestManager.cancelAllRequest();
    }

    /**
     * 取消线程池中整个阻塞队列所有HTTP请求
     */
    public static void cancelAllRequest() {
        SQLRequestThreadPool.removeAllTask();
    }

    /**
     * 取消指定Activity中未执行的请求
     */
    public static void cancelBlockingRequest(Activity activity) {
        SQLRequestManager requestManager = checkRequestManager(activity, false);
        if (requestManager != null)
            requestManager.cancelBlockingRequest();
    }

    /**
     * 取消指定请求
     */
    public static void cancelDesignatedRequest(Activity activity, SQLRequest request) {
        checkRequestManager(activity, false).cancelDesignatedRequest(request);
    }

    /**
     * 访问activity对应的RequestManager对象
     *
     * @param createNew 当RequestManager对象为null时是否创建新的RequestManager对象
     */
    private static SQLRequestManager checkRequestManager(Activity activity, boolean createNew) {
        SQLRequestManager manager;
        if ((manager = managerMap.get(activity)) == null) {
            if (createNew) {
                manager = new SQLRequestManager();
                managerMap.put(activity, manager);
            } else {
//                throw new NullPointerException(activity.getClass().getSimpleName() + "'s RequestManager is null!");
            }
        }
        return manager;
    }

    /**
     * 关闭线程池，并等待任务执行完成，不接受新任务
     */
    public static void shutdown() {
        SQLRequestThreadPool.shutdown();
    }

    /**
     * 关闭，立即关闭，并挂起所有正在执行的线程，不接受新任务
     */
    public static void shutdownRightnow() {
        SQLRequestThreadPool.shutdownRightNow();
    }
}
