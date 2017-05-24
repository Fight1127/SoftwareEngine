package com.ysy.mindmap.utils;

import android.graphics.PointF;

public class MathHelper {

    public static double getDistance(PointF pointOne, PointF pointTwo) {
        return Math.sqrt(Math.pow(pointOne.x - pointTwo.x, 2.0) + Math.pow(pointOne.y - pointTwo.y, 2.0));
    }
}
