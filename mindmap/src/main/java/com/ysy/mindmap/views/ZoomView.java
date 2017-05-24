package com.ysy.mindmap.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.ysy.mindmap.utils.MathHelper;

import java.util.Timer;
import java.util.TimerTask;

public class ZoomView extends View {

    protected static int ACTION_MODE_NONE = 0;
    protected static int ACTION_MODE_DRAG = 1;
    protected static int ACTION_MODE_ZOOM = 2;
    protected static int ACTION_MODE_PAN_IN_CORNERS = 3;
    private static float MIN_ZOOM = 0.25f;
    private static float MAX_ZOOM = 4f;
    protected static int LONGPRESS_TIME = 500;

    private long pressStartTimeInMilliSeconds;
    private boolean isStartedDragging;
    private Timer longpressTimer = new Timer();
    private ScaleGestureDetector zoomDetector;
    private CanvasCamera canvasCamera;
    private int _actionMode;
    private PointF currentDragStart;
    private PointF cameraDragStartTranslation;
    Handler guiThreadHandler;

    public ZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeZoomComponents();
        guiThreadHandler = new Handler();
    }

    public ZoomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeZoomComponents();
    }

    public ZoomView(Context context) {
        super(context);
        initializeZoomComponents();

    }

    public float getDragDistanceFromStart(MotionEvent event) {
        PointF dragStart = getCurrentDragStart();
        if (dragStart != null) {
            return (float) MathHelper.getDistance(dragStart, new PointF(event.getX(), event.getY()));
        }
        return 0.0f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                handleTouchStart(event);
                setActionMode(ACTION_MODE_DRAG);
                break;
            case MotionEvent.ACTION_MOVE:
                if (getActionMode() == ACTION_MODE_DRAG) {
                    float distanceFromStart = getDragDistanceFromStart(event);
                    if (distanceFromStart > 10.0f) {
                        stopLongpressTimer();
                        setStartedDragging(true);
                    }
                    if (isStartedDragging()) {
                        updateCameraTranslation(event);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                setActionMode(ACTION_MODE_ZOOM);
                break;

            case MotionEvent.ACTION_UP:
                stopLongpressTimer();
                boolean clickIsFasterThanLongpress = System.currentTimeMillis() - getPressStartTimeInMilliSeconds()
                        < LONGPRESS_TIME;
                if (!isStartedDragging() && clickIsFasterThanLongpress) {
                    onClickEvent(event);
                }
                setActionMode(ACTION_MODE_NONE);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                setActionMode(ACTION_MODE_NONE);
                break;
        }

        zoomDetector.onTouchEvent(event);
        return true;
    }

    public void onClickEvent(MotionEvent event) {

    }

    private void internalOnLongClickEvent(final PointF rawPoint) {
        guiThreadHandler.post(new Runnable() {
            public void run() {
                onLongClickEvent(rawPoint);
            }
        });
    }

    public void onLongClickEvent(PointF rawPoint) {

    }

    protected void prepareCanvasZoom(Canvas canvasToPrepare) {
        Matrix zoomMatrix = getCanvasCamera().getTransformMatrix();
        canvasToPrepare.setMatrix(zoomMatrix);
    }

    private void initializeZoomComponents() {
        zoomDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        setCanvasCamera(new CanvasCamera());
        getCanvasCamera().setScale(1.f);
        setCurrentDragStart(new PointF());
    }

    protected int getActionMode() {
        return _actionMode;
    }

    protected void setActionMode(int actionMode) {
        this._actionMode = actionMode;
    }

    protected PointF getCurrentDragStart() {
        return currentDragStart;
    }

    public PointF getAsAbsoluteCoordinate(PointF rawPoint) {
        return getAsAbsoluteCoordinate(rawPoint.x, rawPoint.y);
    }

    public PointF getAsAbsoluteCoordinate(float x, float y) {
        CanvasCamera camera = getCanvasCamera();
        return camera.getAsUntransformedCoordinates(x, y);
    }

    protected void setCurrentDragStart(PointF currentDragStart) {
        this.currentDragStart = currentDragStart;
    }

    protected void setCurrentDragStart(float x, float y) {
        this.currentDragStart.set(x, y);
    }

    protected CanvasCamera getCanvasCamera() {
        return canvasCamera;
    }

    protected void setCanvasCamera(CanvasCamera canvasCamera) {
        this.canvasCamera = canvasCamera;
    }

    protected PointF getCameraDragStartTranslation() {
        return cameraDragStartTranslation;
    }

    protected void initializeCameraDragStartTranslation() {
        this.cameraDragStartTranslation = getCanvasCamera().copyTranslation();
    }

    private void updateCameraTranslation(MotionEvent event) {
        PointF dragTranslation = new PointF();
        dragTranslation.set(event.getX() - getCurrentDragStart().x,
                event.getY() - getCurrentDragStart().y);
        CanvasCamera camera = getCanvasCamera();
        dragTranslation.x /= camera.getScale();
        dragTranslation.y /= camera.getScale();
        PointF startTranslation = getCameraDragStartTranslation();
        camera.setTranslation(dragTranslation.x + startTranslation.x,
                dragTranslation.y + startTranslation.y);
    }

    private boolean isStartedDragging() {
        return isStartedDragging;
    }

    private void setStartedDragging(boolean startedDragging) {
        this.isStartedDragging = startedDragging;
    }

    protected Timer getLongpressTimer() {
        return longpressTimer;
    }

    protected void setLongpressTimer(Timer longpressTimer) {
        this.longpressTimer = longpressTimer;
    }

    private void startLongpressTimer(MotionEvent event) {
        stopLongpressTimer();
        Timer currentLongPressTimer = getLongpressTimer();
        final PointF location = new PointF(event.getRawX(), event.getRawY());
        currentLongPressTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                internalOnLongClickEvent(location);
            }
        }, LONGPRESS_TIME);
    }

    private void stopLongpressTimer() {
        Timer currentLongPressTimer = getLongpressTimer();
        currentLongPressTimer.cancel();
        setLongpressTimer(new Timer());
    }

    private void handleTouchStart(MotionEvent event) {
        setCurrentDragStart(event.getX(), event.getY());
        initializeCameraDragStartTranslation();
        setStartedDragging(false);
        setPressStartTimeInMilliSeconds(System.currentTimeMillis());
        startLongpressTimer(event);
    }

    protected long getPressStartTimeInMilliSeconds() {
        return pressStartTimeInMilliSeconds;
    }

    protected void setPressStartTimeInMilliSeconds(long pressStartTimeInMilliSeconds) {
        this.pressStartTimeInMilliSeconds = pressStartTimeInMilliSeconds;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float oldScaleFactor = getCanvasCamera().getScale();
            float scaleFactor = oldScaleFactor * detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            if (scaleFactor != oldScaleFactor) {
                float scaleDifference = scaleFactor / oldScaleFactor;
                float zoomTranslationX = (1 - scaleDifference) * detector.getFocusX() / scaleFactor;
                float zoomTranslationY = (1 - scaleDifference) * detector.getFocusY() / scaleFactor;
                getCanvasCamera().setScaleAndRelativeTranslate(scaleFactor,
                        zoomTranslationX,
                        zoomTranslationY);
                invalidate();
            }
            return true;
        }
    }
}
