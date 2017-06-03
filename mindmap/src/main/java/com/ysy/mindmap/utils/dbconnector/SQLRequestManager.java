package com.ysy.mindmap.utils.dbconnector;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sylvester on 17/4/18.
 */

class SQLRequestManager {

    ArrayList<SQLRequest> requests;

    SQLRequestManager() {
        requests = new ArrayList<>();
    }

    private void addRequest(SQLRequest request) {
        requests.add(request);
    }

    SQLRequest createStringRequest(SQLEntity<String> entity, SQLCallback<ResultSet> callback) {
        SQLRequest<String, ResultSet> request = new SQLRequest<>(this, entity, callback);
        addRequest(request);
        return request;
    }

    SQLRequest createListRequest(SQLEntity<List<String>> entity, SQLCallback<List<ResultSet>> callback) {
        SQLRequest<List<String>, List<ResultSet>> request = new SQLRequest<>(this, entity, callback);
        addRequest(request);
        return request;
    }

    /**
     * 取消所有的网络请求（包括正在执行的）
     */
    void cancelAllRequest() {
        BlockingQueue queue = SQLRequestThreadPool.getQueue();
        for (int i = requests.size() - 1; i >= 0; i--) {
            SQLRequest request = requests.get(i);
            if (queue.contains(request)) {
                queue.remove(request);
            } else {
                request.disConn();
            }
        }
        requests.clear();
    }

    /**
     * 取消未执行的网络请求
     */
    void cancelBlockingRequest() {
        // 取交集（即取出那些在线程池的阻塞队列中等待执行的请求）
        List<SQLRequest> intersection = (List<SQLRequest>) requests.clone();
        intersection.retainAll(SQLRequestThreadPool.getQueue());
        // 分别删除
        SQLRequestThreadPool.getQueue().removeAll(intersection);
        requests.removeAll(intersection);
    }

    /**
     * 取消指定的网络请求
     */
    void cancelDesignatedRequest(SQLRequest request) {
        if (!SQLRequestThreadPool.removeTaskFromQueue(request)) {
            request.disConn();
        }
    }
}
