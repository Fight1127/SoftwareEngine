package com.ysy.mindmap.uis.mindmap;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ysy.mindmap.models.MindMapItem;
import com.ysy.mindmap.uis.mindmap.components.Bullet;
import com.ysy.mindmap.uis.mindmap.components.BulletManager;
import com.ysy.mindmap.uis.mindmap.components.DropItemAction;
import com.ysy.mindmap.uis.mindmap.components.StyleSchemeType;
import com.ysy.mindmap.views.ZoomView;

import java.util.ArrayList;
import java.util.List;

public class MindMapView extends ZoomView {

    private MindMapItem mindMapRoot;
    private Bullet selectedBullet;
    private Bullet moveBullet;
    private PointF moveBulletInitPosition;
    private Bullet hoveredItem;

    private OnSelectedBulletChangeListener selectedBulletChangeListener;
    private MindMapItemActionRequestListener mindMapActionListener;
    private BulletManager bulletManager;
    private boolean isRootPositionInitialized;

    public void updateMindMap(MindMapItem rootItem) {
        mindMapRoot = rootItem;
        Bullet rootBullet = getBulletManager().getOrCreateBullet(mindMapRoot);
        setBulletRoot(rootBullet);
        this.invalidate();
    }

    public MindMapItem getMindMapRoot() {
        return mindMapRoot;
    }

    private void generateMap() {
        mindMapRoot = new MindMapItem("加载中…");
    }

    public MindMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        generateMap();
        Bullet rootBullet = getBulletManager().getOrCreateBullet(mindMapRoot);
        setBulletRoot(rootBullet);
    }

    public MindMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MindMapView(Context context) {
        super(context);
        setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (!isRootPositionInitialized) {
            initializeRootPosition();
            isRootPositionInitialized = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        prepareCanvasZoom(canvas);
        getBulletRoot().updateLayout();

        handleMovementAnimations(1000);
        renderItemWithChildren(canvas, getBulletRoot());

        Bullet moveBullet = getMoveBullet();
        if (moveBullet != null) {
            moveBullet.renderThisItem(canvas);
        }
        super.onDraw(canvas);
    }

    private void handleMovementAnimations(int animationTime) {
        List<ValueAnimator> allAnimators = new ArrayList<>();
        getBulletRoot().populateMovementAnimators(allAnimators, animationTime);
        if (allAnimators.size() > 0) {
            allAnimators.get(0).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    invalidate();
                }
            });

            AnimatorSet animatorSet = new AnimatorSet();
            for (ValueAnimator animator : allAnimators) {
                animatorSet.play(animator);
            }
            animatorSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    invalidate();
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
            animatorSet.start();
        }

    }

    private void renderItemWithChildren(Canvas canvasToRenderOn, Bullet itemToRender) {
        itemToRender.renderThisItem(canvasToRenderOn);
        itemToRender.renderChildConnections(canvasToRenderOn);
        for (Bullet child : itemToRender.getChildren()) {

            renderItemWithChildren(canvasToRenderOn, child);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClickEvent(MotionEvent event) {
        PointF transformedPoint = getAsAbsoluteCoordinate(event.getRawX(), event.getRawY());

        Bullet foundBullet = getBulletAtCoordinate(getBulletRoot(), transformedPoint);
        if (foundBullet != null
                && foundBullet.getWrappedContent() != getSelectedMindMapItem()) {
            setSelectedBullet(foundBullet);
        } else {
            setSelectedBullet(null);
        }
        super.onClickEvent(event);
    }

    /**
     * Initializes dragging of a map point.
     */
    @Override
    public void onLongClickEvent(PointF rawPoint) {
        //Initializes dragging of a map point.
        PointF transformedPoint = getAsAbsoluteCoordinate(rawPoint.x, rawPoint.y);
        Bullet foundBullet = getBulletAtCoordinate(getBulletRoot(), transformedPoint);
        if (foundBullet != null) {
            //This is where the dragging starts.
            setSelectedBullet(foundBullet);
            setActionMode(ZoomView.ACTION_MODE_PAN_IN_CORNERS);

            Bullet moveBullet = createMoveBullet(foundBullet);
            PointF moveBulletInitPosition = foundBullet.getRelativePosition(transformedPoint);
            setMoveBullet(moveBullet, moveBulletInitPosition);

            updateMoveBulletPosition(moveBullet, rawPoint);
        }
        super.onLongClickEvent(rawPoint);
    }

    private Bullet createMoveBullet(Bullet bulletToMove) {
        Bullet moveBullet = Bullet.createBullet(bulletToMove.getWrappedContent());
        moveBullet.setStyleSchemeType(StyleSchemeType.GHOST);
        return moveBullet;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Bullet moveBullet = getMoveBullet();
        PointF transformedPoint = getAsAbsoluteCoordinate(event.getRawX(), event.getRawY());
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                //See if we are dragging an item.
                if (moveBullet != null) {
                    updateMoveBulletPosition(moveBullet, new PointF(event.getRawX(), event.getRawY()));
                    handleMoveItemHoverMove(transformedPoint, moveBullet);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                //See if we should place an object.
                if (moveBullet != null) {

                    Bullet hoveredBullet = getHoveredItem();
                    if (hoveredBullet != null) {
                        hoveredBullet.hoverItemLeave(moveBullet);
                        DropItemAction releaseAction = hoveredBullet.getDropItemAction(moveBullet, transformedPoint);
                        if (handleDropItemAction(hoveredBullet.getWrappedContent(), moveBullet.getWrappedContent(), releaseAction)) {

                        }
                    }
                    setMoveBullet(null, null);
                    invalidate();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean handleDropItemAction(MindMapItem hoveredItem, MindMapItem droppedItem, DropItemAction action) {
        if (getMindMapActionListener() != null) {
            switch (action) {
                case SWAP:
                    return getMindMapActionListener().requestSwap(hoveredItem, droppedItem);
                case ADD_CHILD:
                    return getMindMapActionListener().requestBecomeChild(hoveredItem, droppedItem, 0);
            }
        }
        return false;
    }

    private Bullet getBulletAtCoordinate(Bullet searchBullet, PointF coordinate) {
        if (searchBullet != null) {
            if (searchBullet.containsPoint(coordinate)) {
                return searchBullet;
            } else {
                for (Bullet childBullet : searchBullet.getChildren()) {
                    Bullet foundBullet = getBulletAtCoordinate(childBullet, coordinate);
                    if (foundBullet != null) {
                        return foundBullet;
                    }
                }
            }
        }
        return null;
    }

    protected Bullet getBulletRoot() {
        return getBulletManager().getRootBullet();
    }

    protected void setBulletRoot(Bullet bulletRoot) {
        getBulletManager().setRootBullet(bulletRoot);
    }

    public MindMapItem getSelectedMindMapItem() {
        if (selectedBullet != null) {
            return selectedBullet.getWrappedContent();
        }
        return null;
    }

    public void setSelectedBullet(Bullet selectedBullet) {
        if (this.selectedBullet != selectedBullet) {
            if (this.selectedBullet != null) {
                this.selectedBullet.setSelected(false);
            }
            this.selectedBullet = selectedBullet;
            if (this.selectedBullet != null) {
                this.selectedBullet.setSelected(true);
            }
            invalidate();
            OnSelectedBulletChangeListener selectedBulletChanged = getOnSelectedBulletChangeListener();
            if (selectedBulletChanged != null) {
                selectedBulletChanged.onSelectedBulletChanged(this, this.selectedBullet);
            }
        }
    }

    public OnSelectedBulletChangeListener getOnSelectedBulletChangeListener() {
        return getSelectedBulletChangeListener();
    }

    public void setOnSelectedBulletChangeListener(OnSelectedBulletChangeListener selectedBulletChangeListener) {
        this.setSelectedBulletChangeListener(selectedBulletChangeListener);
    }

    private Bullet getMoveBullet() {
        return moveBullet;
    }

    private void setMoveBullet(Bullet bulletToMove, PointF moveBulletInitPosition) {
        this.moveBulletInitPosition = moveBulletInitPosition;
        this.moveBullet = bulletToMove;
    }

    private Bullet getHoveredItem() {
        return hoveredItem;
    }

    private void setHoveredItem(Bullet hoveredItem) {
        this.hoveredItem = hoveredItem;
    }

    private void handleMoveItemHoverMove(PointF transformedPoint, Bullet moveBullet) {
        Bullet foundBullet = getBulletAtCoordinate(getBulletRoot(), transformedPoint);
        boolean hasLeftItem = getHoveredItem() != null && foundBullet != getHoveredItem();
        if (hasLeftItem) {
            getHoveredItem().hoverItemLeave(moveBullet);
        }

        boolean isHoveringOtherItem = foundBullet != null
                && foundBullet.getWrappedContent() != moveBullet.getWrappedContent();
        if (isHoveringOtherItem) {
            setHoveredItem(foundBullet);
            foundBullet.hoverItem(moveBullet, transformedPoint);
        } else if (getHoveredItem() != null) {
            setHoveredItem(null);
        }

    }

    private void updateMoveBulletPosition(Bullet moveBullet, PointF rawPoint) {
        if (moveBullet != null) {
            PointF moveBulletInitPosition = getMoveBulletInitPosition();
            if (moveBulletInitPosition != null) {
                moveBullet.setPosition((int) (rawPoint.x - moveBulletInitPosition.x), (int) (rawPoint.y - moveBulletInitPosition.y));
            } else {
                moveBullet.setPosition((int) (rawPoint.x), (int) (rawPoint.y));
            }
        }
    }

    private PointF getMoveBulletInitPosition() {
        return moveBulletInitPosition;
    }

    public OnSelectedBulletChangeListener getSelectedBulletChangeListener() {
        return selectedBulletChangeListener;
    }

    public void setSelectedBulletChangeListener(OnSelectedBulletChangeListener selectedBulletChangeListener) {
        this.selectedBulletChangeListener = selectedBulletChangeListener;
    }

    public MindMapItemActionRequestListener getMindMapActionListener() {
        return mindMapActionListener;
    }

    public void setMindMapActionListener(MindMapItemActionRequestListener mindMapActionListener) {
        this.mindMapActionListener = mindMapActionListener;
    }

    private BulletManager getBulletManager() {
        if (bulletManager == null) {
            bulletManager = new BulletManager();
        }
        return bulletManager;
    }

    private void initializeRootPosition() {
        int width = getWidth();
        int height = getHeight();
        Rect itemBounds = getBulletRoot().getItemBounds();
        int x = (width - itemBounds.width()) / 2;
        int y = (height - itemBounds.height()) / 2;

        getBulletRoot().setLayoutPosition(x, y);
    }
}
