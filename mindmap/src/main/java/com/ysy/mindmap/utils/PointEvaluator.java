package com.ysy.mindmap.utils;

import android.animation.TypeEvaluator;
import android.graphics.Point;

public class PointEvaluator implements TypeEvaluator {

    public Object evaluate(float fraction, Object startValue, Object endValue) {
        Point startPoint = (Point) startValue;
        Point endPoint = (Point) endValue;
        return new Point((int) (startPoint.x + fraction * (endPoint.x - startPoint.x)),
                (int) (startPoint.y + fraction * (endPoint.y - startPoint.y)));
    }
}
