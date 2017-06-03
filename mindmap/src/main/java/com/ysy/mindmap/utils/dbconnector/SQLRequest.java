package com.ysy.mindmap.utils.dbconnector;

import android.os.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sylvester on 17/4/18.
 */

public class SQLRequest<S, R> implements Runnable {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "";
    private static final String DB_USER = "";
    private static final String DB_PW = "";
    private static final int TIMEOUT_LIMIT = 8;

    private Connection getConn() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        int waitTime = TIMEOUT_LIMIT;
        while (waitTime > 0) {
            if (conn != null)
                break;
            else {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            waitTime--;
        }
        return conn;
    }

    void disConn() {
        isInterrupted = true;
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            conn = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stmt = null;
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            rs = null;
        }
    }

    public enum RequestType {
        SELECT, UPDATE, INSERT, DELETE, MULTI_QUERY, MULTI_UPDATE
    }

    // 宿主Manager
    private SQLRequestManager hostManager;
    // 请求回调
    private SQLCallback<R> callback;
    // Handler对象，用于在回调时切换回主线程进行相应操作
    private Handler handler;
    // 请求中断标志位
    private Boolean isInterrupted = false;

    private SQLEntity<S> sqlEntity;

    SQLRequest(SQLRequestManager requestManager, SQLEntity<S> entity,
               SQLCallback<R> callback) {
        this.hostManager = requestManager;
        this.sqlEntity = entity;
        this.callback = callback;
        this.handler = new Handler();
    }

    @Override
    public void run() {
        if (getConn() == null) {
            handleError(SQLErrorConstant.ERROR_TIMEOUT);
        } else {
            if (!isInterrupted) {
                S sql = sqlEntity.getSql();
                switch (sqlEntity.getType()) {
                    case SELECT:
                        select(sql);
                        break;
                    case UPDATE:
                        update(sql);
                        break;
                    case INSERT:
                        insert(sql);
                        break;
                    case DELETE:
                        delete(sql);
                        break;
                    case MULTI_QUERY:
                        multiQuery(sql);
                        break;
                    case MULTI_UPDATE:
                        multiUpdate(sql);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void select(S sql) {
        if (sql instanceof String) {
            String exeSQL = (String) sql;
            try {
                stmt = conn.createStatement();
                stmt.setQueryTimeout(TIMEOUT_LIMIT);
                rs = stmt.executeQuery(exeSQL);
                if (rs != null && callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess((R) rs);
                        }
                    });
                } else
                    handleError(SQLErrorConstant.ERROR_NETWORK);
            } catch (SQLException e) {
                handleError(SQLErrorConstant.ERROR_SYSTEM);
                e.printStackTrace();
            } finally {
                hostManager.requests.remove(this);
            }
        }
    }

    private void update(S sql) {
        if (sql instanceof String) {
            String exeSQL = (String) sql;
            try {
                stmt = conn.createStatement();
                stmt.setQueryTimeout(TIMEOUT_LIMIT);
                int res = stmt.executeUpdate(exeSQL);
                if (res == 1 && callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(null);
                        }
                    });
                } else
                    handleError(SQLErrorConstant.ERROR_NETWORK);
            } catch (SQLException e) {
                handleError(SQLErrorConstant.ERROR_SYSTEM);
                e.printStackTrace();
            } finally {
                hostManager.requests.remove(this);
            }
        } else if (sql instanceof List) {
            List<String> exeSQList = (List<String>) sql;
            int res = 0;
            try {
                conn.setAutoCommit(false);
                for (int i = 0; i < exeSQList.size(); i++) {
                    stmt = conn.createStatement();
                    stmt.setQueryTimeout(TIMEOUT_LIMIT / exeSQList.size());
                    res = res + stmt.executeUpdate(exeSQList.get(i));
                }
                conn.commit();
                conn.setAutoCommit(true);
                if (res == exeSQList.size() && callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(null);
                        }
                    });
                } else
                    handleError(SQLErrorConstant.ERROR_NETWORK);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                handleError(SQLErrorConstant.ERROR_SYSTEM);
                e.printStackTrace();
            } finally {
                hostManager.requests.remove(this);
            }
        }
    }

    private void insert(S sql) {
        if (sql instanceof String) {
            String exeSQL = (String) sql;
            try {
                stmt = conn.createStatement();
                stmt.setQueryTimeout(TIMEOUT_LIMIT);
                int res = stmt.executeUpdate(exeSQL);
                if (res == 1 && callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(null);
                        }
                    });
                } else
                    handleError(SQLErrorConstant.ERROR_NETWORK);
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate"))
                    handleError(SQLErrorConstant.ERROR_DUPLICATE);
                else
                    handleError(SQLErrorConstant.ERROR_SYSTEM);
                e.printStackTrace();
            } finally {
                hostManager.requests.remove(this);
            }
        }
    }

    private void delete(S sql) {
        if (sql instanceof String) {
            String exeSQL = (String) sql;
            try {
                stmt = conn.createStatement();
                stmt.setQueryTimeout(TIMEOUT_LIMIT);
                int res = stmt.executeUpdate(exeSQL);
                if (res == 1 && callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(null);
                        }
                    });
                } else
                    handleError(SQLErrorConstant.ERROR_NETWORK);
            } catch (SQLException e) {
                handleError(SQLErrorConstant.ERROR_SYSTEM);
                e.printStackTrace();
            } finally {
                hostManager.requests.remove(this);
            }
        }
    }

    private void multiQuery(S sql) {
        if (sql instanceof List) {
            List<String> exeSQList = (List<String>) sql;
            final List<ResultSet> resultSetList = new ArrayList<>();
            try {
                for (int i = 0; i < exeSQList.size(); i++) {
                    stmt = conn.createStatement();
                    stmt.setQueryTimeout(TIMEOUT_LIMIT / exeSQList.size());
                    rs = stmt.executeQuery(exeSQList.get(i));
                    resultSetList.add(rs);
                }
                if (resultSetList.size() == exeSQList.size() && callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess((R) resultSetList);
                        }
                    });
                } else
                    handleError(SQLErrorConstant.ERROR_NETWORK);
            } catch (SQLException e) {
                handleError(SQLErrorConstant.ERROR_SYSTEM);
                e.printStackTrace();
            } finally {
                hostManager.requests.remove(this);
            }
        }
    }

    private void multiUpdate(S sql) {
        if (sql instanceof List) {
            List<String> exeSQList = (List<String>) sql;
            int res = 0;
            try {
                conn.setAutoCommit(false);
                for (int i = 0; i < exeSQList.size(); i++) {
                    stmt = conn.createStatement();
                    stmt.setQueryTimeout(TIMEOUT_LIMIT / exeSQList.size());
                    res = res + stmt.executeUpdate(exeSQList.get(i));
                }
                conn.commit();
                conn.setAutoCommit(true);
                if (res == exeSQList.size() && callback != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(null);
                        }
                    });
                } else
                    handleError(SQLErrorConstant.ERROR_NETWORK);
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                if (e.getMessage().contains("Duplicate"))
                    handleError(SQLErrorConstant.ERROR_DUPLICATE);
                else
                    handleError(SQLErrorConstant.ERROR_SYSTEM);
                e.printStackTrace();
            } finally {
                hostManager.requests.remove(this);
            }
        }
    }

    private void handleError(final int errorCode) {
        if (callback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFail(errorCode);
                }
            });
        }
    }
}
