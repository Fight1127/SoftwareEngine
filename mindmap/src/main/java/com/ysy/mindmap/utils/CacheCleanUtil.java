package com.ysy.mindmap.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.math.BigDecimal;

/**
 * 清理缓存工具类
 * Created by Sylvester on 17/4/13.
 */
public class CacheCleanUtil {

    public static String getTotalCacheSize(Context context) {
        long cacheSize = 0;
        try {
            // 缩短路径为/data/data/包名，以删除包下所有数据，不只是cache
            File appDataDir = new File(context.getCacheDir().getAbsolutePath().replace("cache/", ""));
            cacheSize = getFolderSize(appDataDir);
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (context.getExternalCacheDir() != null) {
                    appDataDir = new File(context.getExternalCacheDir().getAbsolutePath().replace("cache/", ""));
                    cacheSize += getFolderSize(appDataDir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getFormatSize(cacheSize);
    }

    public static void clearAllCache(Context context) {
        File appDataDir = new File(context.getCacheDir().getAbsolutePath().replace("cache/", ""));
        deleteDir(appDataDir);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (context.getExternalCacheDir() != null) {
                appDataDir = new File(context.getExternalCacheDir().getAbsolutePath().replace("cache/", ""));
                deleteDir(appDataDir);
            }
        }
        new AppDataUtil(context).clearAllDatas();
    }

    // Java删除文件目录的前提是目录内容为空，因此必须递归遍历删除文件
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int size;
            if (children != null) {
                size = children.length;
                for (int i = 0; i < size; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir == null || dir.delete();
    }

    private static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                // 如果下面还有文件
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    private static String getFormatSize(double size) {
        double kiloBytes = size / 1024;
        if (kiloBytes < 100) {
            // 由于清除缓存后会立即重新获取缓存大小，实际上是不会到0B的，考虑用户体验，低于100KB直接显示0B
            return "0B";
        }

        double megaBytes = kiloBytes / 1024;
        if (megaBytes < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloBytes));
            // 返回时带2位小数
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaBytes = megaBytes / 1024;
        if (gigaBytes < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaBytes));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaBytes / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaBytes));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }

        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}
