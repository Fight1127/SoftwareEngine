package com.ysy.mindmap.uis.mindmap.components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

import com.ysy.mindmap.models.MindMapItem;
import com.ysy.mindmap.utils.PointEvaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class Bullet implements Observer {

    //Other values Mapping.
    private List<Bullet> children;
    private Bullet parent;

    //Different rendering properties.
    private MindMapItem wrappedContent;
    private StyleScheme currentStyleScheme;
    private StyleScheme normalStyleScheme;
    private StyleScheme ghostStyleScheme;

    //Display data that is cached for efficiency.
    private Rect textBounds;
    private Rect itemBounds;
    private Point position;
    private Point layoutPosition;
    private Map<Bullet, Path> cachedPaths;

    //Bullet properties
    private boolean isSelected;
    private BulletRenderStyle childOrientation;
    private StyleSchemeType styleSchemeType;

    //Bullet Display Properties
    private boolean isHighlightLeft;
    private boolean isHighlightRight;
    private boolean isRunningMoveAnimation;

    private Bullet(MindMapItem contentToWrap) {
        cachedPaths = new HashMap<>();
        children = new ArrayList<>();
        normalStyleScheme = new StyleScheme();
        ghostStyleScheme = new StyleScheme();
        ghostStyleScheme.setColorAlpha(100);

        setChildOrientation(BulletRenderStyle.CENTER);
        setCurrentStyleScheme(normalStyleScheme);

        setContentToWrap(contentToWrap);
        setPosition(0, 0);
        setLayoutPosition(0, 0);
    }

    public static Bullet createBullet(MindMapItem contentToWrap) {
        return new Bullet(contentToWrap);
    }

    public void updateLayout() {
        updateLayoutWithChildren(BulletRenderStyle.CENTER);
    }

    public void updateLayoutWithChildren(BulletRenderStyle renderStyle) {
        switch (renderStyle) {
            case CENTER:
                updateLayoutChildrenCenter();
                break;
            case TO_THE_LEFT:
                updateLayoutChildrenToLeft(0, getChildren().size());
                break;
            case TO_THE_RIGHT:
                updateLayoutChildrenToRight(0, getChildren().size());
                break;
        }
    }

    public void renderChildConnections(Canvas canvasToRenderOn) {
        for (Bullet child : children) {
            renderChildConnection(child, canvasToRenderOn);
        }
    }

    public void updateLayoutChildrenCenter() {
        int itemsToTheRight = getOptimalNumberOfChildrenToRight();
        updateLayoutChildrenToRight(0, itemsToTheRight);
        updateLayoutChildrenToLeft(itemsToTheRight, getChildren().size());
    }

    public void updatePositionInstantlyRecursive() {
        setPosition(getLayoutPosition());
        for (Bullet child : getChildren()) {
            child.updatePositionInstantlyRecursive();
        }
    }

    public void populateMovementAnimators(List<ValueAnimator> allAnimators, int animationTime) {
        updatePositionPopulateAnimators(allAnimators, animationTime);
        for (Bullet child : getChildren()) {
            child.populateMovementAnimators(allAnimators, animationTime);
        }
    }

    public void updatePositionPopulateAnimators(List<ValueAnimator> allAnimators, int animationTime) {
        if (!isRunningMoveAnimation()
                && (getPosition().x != getLayoutPosition().x
                || getPosition().y != getLayoutPosition().y)) {
            ObjectAnimator animator = ObjectAnimator.ofObject(this, "position", new PointEvaluator(),
                    getPosition(), getLayoutPosition());
            animator.setDuration(animationTime);
            allAnimators.add(animator);

            animator.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                    setRunningMoveAnimation(true);
                }

                public void onAnimationEnd(Animator animation) {
                    setRunningMoveAnimation(false);
                }

                public void onAnimationCancel(Animator animation) {
                    setRunningMoveAnimation(false);
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
        }
    }

    private int getChildItemsSize(int childStartIndex, int childStopIndex) {
        int itemExternalPadding = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_EXTERNAL_PADDING_HEIGHT);
        int totalHeight = -itemExternalPadding;
        for (int i = childStartIndex; i < childStopIndex; i++) {
            totalHeight += getChildren().get(i).getDesiredHeightWithChildren();
            totalHeight += itemExternalPadding;
        }
        return totalHeight;
    }

    public PointF getRightConnectPoint() {
        Point position = getPosition();
        Rect itemBounds = getItemBounds();
        return new PointF(position.x + itemBounds.width(), position.y + itemBounds.height() / 2);
    }

    public PointF getLeftConnectPoint() {
        Point position = getPosition();
        Rect itemBounds = getItemBounds();
        return new PointF(position.x, position.y + itemBounds.height() / 2);
    }

    public void hoverItem(Bullet hoverBullet, PointF hoverPosition) {
        PointF relativePosition = getRelativePosition(hoverPosition);
        float highlightRelativePlace = relativePosition.x / (float) getItemBounds().width();
        boolean highlightLeft = highlightRelativePlace < 0.5f;
        setHighlightLeft(highlightLeft);
        setHighlightRight(!highlightLeft);
    }

    public DropItemAction getDropItemAction(Bullet hoverBullet, PointF hoverPosition) {
        PointF relativePosition = getRelativePosition(hoverPosition);
        float highlightRelativePlace = relativePosition.x / (float) getItemBounds().width();
        DropItemAction action = DropItemAction.UNDEFINED;
        switch (getChildOrientation()) {
            case CENTER:
                action = highlightRelativePlace < 0.25 || highlightRelativePlace > 0.75 ? DropItemAction.ADD_CHILD : DropItemAction.SWAP;
                break;
            case TO_THE_LEFT:
                action = highlightRelativePlace < 0.5 ? DropItemAction.ADD_CHILD : DropItemAction.SWAP;
                break;
            case TO_THE_RIGHT:
                action = highlightRelativePlace > 0.5 ? DropItemAction.ADD_CHILD : DropItemAction.SWAP;
                break;
        }
        return action;
    }

    public void hoverItemLeave(Bullet hoverBullet) {
        setHighlightLeft(false);
        setHighlightRight(false);
    }

    public void updateLayoutChildrenToLeft(int startIndex, int stopIndex) {
        int itemExternalPaddingWidth = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_EXTERNAL_PADDING_WIDTH);

        int childItemsSize = getChildItemsSize(startIndex, stopIndex);
        int endX = getLayoutPosition().x - itemExternalPaddingWidth;

        int itemTop = getLayoutPosition().y + getItemBounds().top;
        int itemExternalPaddingHeight = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_EXTERNAL_PADDING_HEIGHT);
        int nextItemTop = itemTop + childItemsSize / 2;

        //render items 
        for (int i = startIndex; i < stopIndex; i++) {
            Bullet currentBullet = getChildren().get(i);
            currentBullet.setChildOrientation(BulletRenderStyle.TO_THE_LEFT);
            int bulletDesiredHeight = currentBullet.getDesiredHeightWithChildren();
            int y = nextItemTop - bulletDesiredHeight / 2;
            currentBullet.setLayoutPosition(endX - currentBullet.getItemBounds().width(), y);
            currentBullet.updateLayoutWithChildren(BulletRenderStyle.TO_THE_LEFT);
            nextItemTop -= bulletDesiredHeight + itemExternalPaddingHeight;
        }
    }

    public void updateLayoutChildrenToRight(int startIndex, int stopIndex) {
        int itemRight = getLayoutPosition().x + getItemBounds().right;
        int itemExternalPaddingWidth = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_EXTERNAL_PADDING_WIDTH);
        int itemTop = getLayoutPosition().y + getItemBounds().top;
        int itemExternalPaddingHeight = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_EXTERNAL_PADDING_HEIGHT);

        int childItemsSize = getChildItemsSize(startIndex, stopIndex);

        int nextItemTop = itemTop + childItemsSize / 2;
        int x = itemRight + itemExternalPaddingWidth;

        //render items 
        for (int i = startIndex; i < stopIndex; i++) {
            Bullet currentBullet = getChildren().get(i);
            currentBullet.setChildOrientation(BulletRenderStyle.TO_THE_RIGHT);
            int bulletDesiredHeight = currentBullet.getDesiredHeightWithChildren();
            int y = nextItemTop - bulletDesiredHeight / 2;
            currentBullet.setLayoutPosition(x, y);
            currentBullet.updateLayoutWithChildren(BulletRenderStyle.TO_THE_RIGHT);
            nextItemTop -= bulletDesiredHeight + itemExternalPaddingHeight;
        }
    }

    public void renderThisItem(Canvas canvasToRenderOn) {
        renderBackground(canvasToRenderOn);
        renderBorder(canvasToRenderOn);
        renderText(canvasToRenderOn);
    }

    @Override
    public String toString() {
        return getWrappedContent().toString();
    }

    private void renderBackground(Canvas canvasToRenderOn) {
        Paint backgroundPaint = isSelected() ? getStyleScheme().getBackgroundHighlightedPaint() : getStyleScheme().getBackgroundPaint();
        Rect itemBounds = getItemBounds();
        Point position = getPosition();
        canvasToRenderOn.drawRect(position.x,
                position.y,
                position.x + itemBounds.width(),
                position.y + itemBounds.height(),
                backgroundPaint);
        if (!isSelected()) {
            Paint highlightPaint = getStyleScheme().getBackgroundHighlightedPaint();
            if (isHighlightLeft()) {
                canvasToRenderOn.drawRect(position.x,
                        position.y,
                        position.x + itemBounds.width() / 2,
                        position.y + itemBounds.height(),
                        highlightPaint);
            }
            if (isHighlightRight()) {
                canvasToRenderOn.drawRect(position.x + itemBounds.width() / 2,
                        position.y,
                        position.x + itemBounds.width(),
                        position.y + itemBounds.height(),
                        highlightPaint);
            }
        }
    }

    private void renderBorder(Canvas canvasToRenderOn) {
        Paint borderPaint = getStyleScheme().getBorderPaint();
        Rect itemBounds = getItemBounds();
        Point position = getPosition();

        canvasToRenderOn.drawRect(position.x,
                position.y,
                position.x + itemBounds.width(),
                position.y + itemBounds.height(),
                borderPaint);
    }

    private void renderText(Canvas canvasToRenderOn) {
        String textToRender = getContentText();
        Paint textPaint = getStyleScheme().getTextPaint();
        Point position = getPosition();
        int leftPadding = getLeftPadding();
        int bottomPadding = getBottomPadding();
        Rect itemBounds = getItemBounds();

        canvasToRenderOn.drawText(textToRender,
                position.x + leftPadding,
                position.y + bottomPadding + itemBounds.height() / 2,
                textPaint);
    }

    protected MindMapItem getContentToWrap() {
        return getWrappedContent();
    }

    protected void setContentToWrap(MindMapItem contentToWrap) {
        this.setWrappedContent(contentToWrap);
    }

    public int getDesiredHeightWithChildren() {
        int betweenChildrenPadding = getCurrentStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_EXTERNAL_PADDING_HEIGHT);

        int childrenSize = -betweenChildrenPadding;
        for (Bullet childBullet : getChildren()) {
            childrenSize += childBullet.getDesiredHeightWithChildren();
            childrenSize += betweenChildrenPadding;
        }

        int ownDesiredHeight = getItemDesiredHeight();

        return ownDesiredHeight > childrenSize ? ownDesiredHeight : childrenSize;
    }

    public int getItemDesiredHeight() {
        Rect itemBounds = getItemBounds();
        return itemBounds.height();
    }

    protected Rect getTextBounds() {
        if (textBounds == null) {
            textBounds = new Rect();
            String text = getContentText();
            getStyleScheme().getTextPaint().getTextBounds(text, 0, text.length(), textBounds);
        }
        return textBounds;
    }

    public Rect getItemBounds() {
        if (itemBounds == null) {
            itemBounds = new Rect();
            Rect textBounds = getTextBounds();
            int rightPadding = getRightPadding();
            int topPadding = getTopPadding();
            int leftPadding = getLeftPadding();
            int bottomPadding = getBottomPadding();

            itemBounds.set(0, 0,
                    textBounds.width() + leftPadding + rightPadding,
                    textBounds.height() + topPadding + bottomPadding);
        }
        return itemBounds;
    }

    private String getContentText() {
        MindMapItem item = getContentToWrap();
        if (item != null) {
            return item.getText();
        }
        return "";
    }

    public boolean containsPoint(PointF point) {
        Point position = getPosition();
        int relativeX = (int) point.x - position.x;
        int relativeY = (int) point.y - position.y;

        Rect itemBounds = getItemBounds();
        return itemBounds.contains(relativeX, relativeY);
    }

    protected Point getPosition() {
        return position;
    }

    protected Point getRelativePosition(Point absolutePosition) {
        int x = absolutePosition.x - getPosition().x;
        int y = absolutePosition.y - getPosition().y;
        return new Point(x, y);
    }

    public PointF getRelativePosition(PointF absolutePosition) {
        float x = absolutePosition.x - getPosition().x;
        float y = absolutePosition.y - getPosition().y;
        return new PointF(x, y);
    }

    public void setPosition(Point position) {
        if (this.position == null || this.position.x != position.x || this.position.y != position.y) {
            this.position = position;
            clearCachedPaths();
            if (parent != null) {
                parent.clearCachedPaths();
            }
        }
    }

    public void setLayoutPosition(int x, int y) {
        setLayoutPosition(new Point(x, y));
    }

    public void setPosition(int x, int y) {
        setPosition(new Point(x, y));
    }

    protected int getLeftPadding() {
        int leftPadding = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_PADDING_LEFT);
        return leftPadding;
    }

    protected int getRightPadding() {
        int rightPadding = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_PADDING_RIGHT);
        return rightPadding;
    }

    protected int getTopPadding() {
        int topPadding = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_PADDING_TOP);
        return topPadding;
    }

    protected int getBottomPadding() {
        int bottomPadding = getStyleScheme().getRenderAttribute(StyleScheme.ATTRIBUTE_PADDING_BOTTOM);
        return bottomPadding;
    }

    public Bullet getParent() {
        return parent;
    }

    public void setParent(Bullet parent) {
        this.parent = parent;
    }

    protected int getOptimalNumberOfChildrenToRight() {
        //1. We determine total height.
        int totalChildrenHeight = getDesiredHeightWithChildren();
        int desiredHeightOnEachSide = totalChildrenHeight / 2;
        //2. We determine how many items to updateLayout to the right.
        int accumulatedHeight = 0;
        int itemsToTheRight = 0;
        int closestMatch = Integer.MAX_VALUE;
        for (Bullet child : getChildren()) {
            accumulatedHeight += child.getDesiredHeightWithChildren();
            if (Math.abs(accumulatedHeight - desiredHeightOnEachSide) <= closestMatch) {
                itemsToTheRight++;
                closestMatch = Math.abs(accumulatedHeight - desiredHeightOnEachSide);
            } else {
                break;
            }
        }
        return itemsToTheRight;
    }

    public List<Bullet> getChildren() {
        return children;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    private void renderChildConnection(Bullet child, Canvas canvasToRenderOn) {

        Path path = calculateBezierPath(child);
//        Path path = getChildPath(child);
        renderConnectionPath(path, canvasToRenderOn);
    }

    private Path getChildPath(Bullet child) {
        if (!getCachedPaths().containsKey(child)) {
            Path calculatedPath = calculateBezierPath(child);
            getCachedPaths().put(child, calculatedPath);
        }
        return getCachedPaths().get(child);
    }

    private void renderConnectionPath(Path path, Canvas canvasToRenderOn) {
        Paint pathPaint = getStyleScheme().getPathPaint();
        canvasToRenderOn.drawPath(path, pathPaint);
    }

    private Path calculateBezierPath(PointF startPoint, PointF endPoint) {
        float offset = (startPoint.x - endPoint.x) / 2.0f;
        Path path = new Path();
        path.moveTo(startPoint.x, startPoint.y);
        float x2 = (endPoint.x + startPoint.x) / 2;
        float y2 = (endPoint.y + startPoint.y) / 2;
        path.quadTo(startPoint.x - offset, startPoint.y, x2, y2);
        path.quadTo(endPoint.x + offset, endPoint.y, endPoint.x, endPoint.y);
        return path;
    }

    private Path calculateBezierPath(Bullet child) {
        int xDiff = getPosition().x - child.getPosition().x;
        PointF endPoint;
        PointF startPoint;
        if (xDiff < 0) {
            startPoint = getRightConnectPoint();
            endPoint = child.getLeftConnectPoint();
        } else {
            startPoint = child.getRightConnectPoint();
            endPoint = getLeftConnectPoint();
        }
        Path path = calculateBezierPath(startPoint, endPoint);
        return path;
    }

    protected Map<Bullet, Path> getCachedPaths() {
        if (cachedPaths == null) {
            cachedPaths = new Hashtable<Bullet, Path>();
        }
        return cachedPaths;
    }

    public MindMapItem getWrappedContent() {
        return wrappedContent;
    }

    public void setWrappedContent(MindMapItem wrappedContent) {
        if (this.wrappedContent != null) {
            this.wrappedContent.deleteObserver(this);
        }
        wrappedContent.addObserver(this);
        this.wrappedContent = wrappedContent;
    }

    private void clearCachedPaths() {
        if (cachedPaths.size() > 0) {
            cachedPaths.clear();
        }
    }

    public void update(Observable observable, Object data) {
        MindMapItem updatedMindMapItem = (MindMapItem) observable;

        if (updatedMindMapItem == getWrappedContent()) {
            //We synchronize the children of the node.
            List<Bullet> bulletsToRemove = getBulletsToDelete();
            List<Bullet> bulletsToAdd = getBulletsToAdd();
            for (Bullet bulletToRemove : bulletsToRemove) {
                children.remove(bulletToRemove);
            }
            for (Bullet bulletToAdd : bulletsToAdd) {
                children.add(bulletToAdd);
            }
            sortBullets();
        }
        if (parent != null) {
            parent.clearCachedPaths();
        }
        clearCachedPaths();
        textBounds = null;
        itemBounds = null;

    }

    private void sortBullets() {
        List<Bullet> newBulletsList = new ArrayList<Bullet>();
        for (MindMapItem child : getWrappedContent().getChildren()) {
            boolean bulletFound = false;
            for (Bullet currentChildBullet : getChildren()) {
                if (currentChildBullet.getWrappedContent() == child) {
                    newBulletsList.add(currentChildBullet);
                    break;
                }
            }
        }
        setChildren(newBulletsList);
    }

    private List<Bullet> getBulletsToDelete() {
        List<Bullet> bulletsToDelete = new ArrayList<Bullet>();
        for (Bullet currentChildBullet : getChildren()) {
            boolean bulletFound = false;
            for (MindMapItem child : getWrappedContent().getChildren()) {
                if (currentChildBullet.getWrappedContent() == child) {
                    bulletFound = true;
                    break;
                }
            }
            if (!bulletFound) {
                bulletsToDelete.add(currentChildBullet);
            }
        }
        return bulletsToDelete;
    }

    private List<Bullet> getBulletsToAdd() {
        List<Bullet> bulletsToAdd = new ArrayList<Bullet>();
        for (MindMapItem child : getWrappedContent().getChildren()) {
            boolean bulletFound = false;
            for (Bullet currentChildBullet : getChildren()) {
                if (currentChildBullet.getWrappedContent() == child) {
                    bulletFound = true;
                    break;
                }
            }
            if (!bulletFound) {
                bulletsToAdd.add(Bullet.createBullet(child));
            }
        }
        return bulletsToAdd;
    }

    private void setChildren(List<Bullet> children) {
        this.children = children;
    }

    protected StyleScheme getStyleScheme() {
        return getCurrentStyleScheme();
    }

    protected void setStyleScheme(StyleScheme styleScheme) {
        this.setCurrentStyleScheme(styleScheme);
    }

    protected StyleScheme getCurrentStyleScheme() {
        return currentStyleScheme;
    }

    protected void setCurrentStyleScheme(StyleScheme currentStyleScheme) {
        this.currentStyleScheme = currentStyleScheme;
    }

    private BulletRenderStyle getChildOrientation() {
        return childOrientation;
    }

    private void setChildOrientation(BulletRenderStyle childOrientation) {
        this.childOrientation = childOrientation;
    }

    public boolean isHighlightLeft() {
        return isHighlightLeft;
    }

    public void setHighlightLeft(boolean highlightLeft) {
        this.isHighlightLeft = highlightLeft;
    }

    public boolean isHighlightRight() {
        return isHighlightRight;
    }

    public void setHighlightRight(boolean highlightRight) {
        this.isHighlightRight = highlightRight;
    }

    public StyleSchemeType getStyleSchemeType() {
        return styleSchemeType;
    }

    public void setStyleSchemeType(StyleSchemeType styleSchemeType) {
        this.styleSchemeType = styleSchemeType;
        switch (styleSchemeType) {
            case NORMAL:
                setStyleScheme(normalStyleScheme);
                break;
            case GHOST:
                setStyleScheme(ghostStyleScheme);
                break;
            default:
                break;
        }
    }

    public Point getLayoutPosition() {
        return layoutPosition;
    }

    public void setLayoutPosition(Point layoutPosition) {
        this.layoutPosition = layoutPosition;
    }

    public boolean isRunningMoveAnimation() {
        return isRunningMoveAnimation;
    }

    private void setRunningMoveAnimation(boolean runningMoveAnimation) {
        this.isRunningMoveAnimation = runningMoveAnimation;
    }
}
