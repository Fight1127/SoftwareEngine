package com.ysy.mindmap.models;

import com.ysy.mindmap.utils.SuspendableObservable;

import java.util.ArrayList;
import java.util.List;

public class MindMapItem extends SuspendableObservable {

    public static final int UPDATED_TEXT = 1;
    public static final int UPDATED_CHILDREN = 2;
    public static final int UPDATED_DELETED = 3;
    private MindMapItem parent;
    private String text;
    private List<MindMapItem> children;

    public MindMapItem(String title, MindMapItem... mindMapItems) {
        children = new ArrayList<>();

        setText(title);
        if (mindMapItems != null) {
            for (MindMapItem item : mindMapItems) {
                item.setParent(this);
                children.add(item);
            }
        }
    }

    public MindMapItem getParent() {
        return parent;
    }

    public int getChildIndex(MindMapItem mindMapItem) {
        return children.indexOf(mindMapItem);
    }

    public void removeChild(MindMapItem childToRemove) {
        children.remove(childToRemove);
        setChangeAndNotifyObservers(UPDATED_CHILDREN);
    }

    public void addChild(int index, MindMapItem childToAdd) {
        children.add(index, childToAdd);
        childToAdd.setParent(this);
        setChangeAndNotifyObservers(UPDATED_CHILDREN);
    }

    public void addChild(MindMapItem childToAdd) {
        childToAdd.setParent(this);
        if (!children.contains(childToAdd)) {
            children.add(childToAdd);
            setChangeAndNotifyObservers(UPDATED_CHILDREN);
        }
    }

    public void addChild(String childText) {
        MindMapItem childToAdd = new MindMapItem(childText);
        addChild(childToAdd);
    }

    public void addSibling(String childText) {
        if (parent != null) {
            MindMapItem childToAdd = new MindMapItem(childText);
            int currentIndex = parent.getChildIndex(this);
            parent.addChild(currentIndex + 1, childToAdd);
        }
    }

    public List<MindMapItem> getChildren() {
        return children;
    }

    public void setChildren(List<MindMapItem> childrenToSet) {
        children.clear();
        for (MindMapItem child : childrenToSet) {
            if (child != this) {
                child.setParent(this);
                children.add(child);
            }
        }
        setChangeAndNotifyObservers(UPDATED_CHILDREN);
    }

    public List<MindMapItem> copyChildren() {
        ArrayList<MindMapItem> childrenCopy = new ArrayList<MindMapItem>(children.size());
        for (MindMapItem child : children) {
            childrenCopy.add(child);
        }
        return childrenCopy;
    }

    private void setParent(MindMapItem parent) {
        if (this.parent != null && this.parent != parent) {
            this.parent.removeChild(this);
        }
        this.parent = parent;
    }

    public void clearParent() {
        setParent(null);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;

        setChangeAndNotifyObservers(UPDATED_TEXT);
    }

    public void notifyDelete() {
        setChangeAndNotifyObservers(UPDATED_DELETED);
    }

    public void notifyDeleteWithChildren() {
        for (MindMapItem child : getChildren()) {
            child.notifyDeleteWithChildren();
        }
        setChangeAndNotifyObservers(UPDATED_DELETED);
    }

    public void deleteOne() {
        if (parent != null) {
            parent.suspendBinding();
            for (MindMapItem child : children) {
                parent.addChild(0, child);
            }
            parent.removeChild(this);
            notifyDelete();
            parent.resumeBinding();
        }
    }

    public void deleteBranch() {
        if (parent != null) {
            parent.removeChild(this);
            notifyDeleteWithChildren();
        }
    }

    public static void SwapItems(MindMapItem itemOne, MindMapItem itemTwo) {
        MindMapItem itemOneParent = itemOne.getParent();
        MindMapItem itemTwoParent = itemTwo.getParent();
        itemOneParent = itemOneParent == itemTwo ? itemOne : itemOneParent;
        itemTwoParent = itemTwoParent == itemOne ? itemTwo : itemTwoParent;

        List<MindMapItem> itemOneChildren = itemOne.copyChildren();
        List<MindMapItem> itemTwoChildren = itemTwo.copyChildren();

        itemOne.suspendBinding();
        itemTwo.suspendBinding();
        itemOne.setChildren(itemTwoChildren);
        itemTwo.setChildren(itemOneChildren);
        if (itemTwoParent != null) {
            itemTwoParent.addChild(itemOne);
        } else {
            itemOne.clearParent();
        }
        if (itemOneParent != null) {
            itemOneParent.addChild(itemTwo);
        } else {
            itemTwo.clearParent();
        }
        itemOne.resumeBinding();
        itemTwo.resumeBinding();
    }

    @Override
    public String toString() {
        return getText() + " " + getChildren().size();
    }
}
