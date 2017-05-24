package com.ysy.mindmap.views;

import android.graphics.Matrix;
import android.graphics.PointF;

public class CanvasCamera {

    private float scale;
    private PointF translation;
    private Matrix transformMatrix;
    private Matrix inverseMatrix;
    private boolean isInverseMatrixShouldBeRecalculated = true;

    public CanvasCamera() {
        setTransformMatrix(new Matrix());
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        updateTransformMatrix();
    }

    public PointF getAsUntransformedCoordinates(float x, float y) {
//        Matrix transformMatrix = getTransformMatrix();
//        float[] coordinateVector = {x, y};
//        Matrix inverseMatrix = getInverseMatrix();
//        inverseMatrix.mapPoints(coordinateVector);
//        return new PointF(coordinateVector[0], coordinateVector[1]);
        float transformedX = x / scale - translation.x;
        float transformedY = y / scale - translation.y;
        return new PointF(transformedX, transformedY);
    }

    public void setScaleAndRelativeTranslate(float scale, float relativeTranslateX, float relativeTranslateY) {
        this.scale = scale;
        translate(relativeTranslateX, relativeTranslateY);
        updateTransformMatrix();
    }

    private void translate(float relativeTranslateX, float relativeTranslateY) {
        PointF currentTranslation = getTranslation();
        setTranslation(currentTranslation.x + relativeTranslateX, currentTranslation.y + relativeTranslateY);
    }

    public PointF getTranslation() {
        if (translation == null) {
            translation = new PointF();
        }
        return translation;
    }

    public PointF copyTranslation() {
        PointF currentTranslation = getTranslation();
        return new PointF(currentTranslation.x, currentTranslation.y);
    }

    public void setTranslation(PointF translation) {
        this.translation = translation;

        updateTransformMatrix();
    }

    public void updateTransformMatrix() {
        Matrix transformMatrix = getTransformMatrix();
        transformMatrix.reset();

        PointF translation = getTranslation();
        transformMatrix.preTranslate(translation.x, translation.y);
        transformMatrix.postScale(scale, scale);
    }

    public void setTranslation(float x, float y) {
        if (translation == null) {
            translation = new PointF(x, y);
        } else {
            this.translation.set(x, y);
        }
        updateTransformMatrix();
    }

    public Matrix getTransformMatrix() {
        return transformMatrix;
    }

    public void setTransformMatrix(Matrix transformMatrix) {
        this.transformMatrix = transformMatrix;
    }

    protected Matrix getInverseMatrix() {
        if (inverseMatrix == null) {
            isInverseMatrixShouldBeRecalculated = true;
            inverseMatrix = new Matrix();
        }
        if (isInverseMatrixShouldBeRecalculated) {
            Matrix transformMatrix = getTransformMatrix();
            if (!transformMatrix.invert(inverseMatrix)) {
                inverseMatrix.reset();
            }
            isInverseMatrixShouldBeRecalculated = false;
        }
        return inverseMatrix;
    }
}
