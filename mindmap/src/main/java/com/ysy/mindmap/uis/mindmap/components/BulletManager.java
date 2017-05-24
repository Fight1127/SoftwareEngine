package com.ysy.mindmap.uis.mindmap.components;

import com.ysy.mindmap.models.MindMapItem;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class BulletManager {

    private Map<MindMapItem, Bullet> bulletStore;
    private Bullet rootBullet;

    public BulletManager() {
        bulletStore = new HashMap<>();
    }

    public Bullet getOrCreateBullet(MindMapItem mindMapItem) {
        if (!bulletStore.containsKey(mindMapItem)) {
            mindMapItem.addObserver(new Observer() {
                public void update(Observable observable, Object data) {
                    BitSet updateMap = (BitSet) data;
                    if (updateMap != null) {
                        if (updateMap.get(MindMapItem.UPDATED_CHILDREN)) {
                            reconstructBulletTree();
                        }
                        if (updateMap.get(MindMapItem.UPDATED_DELETED)) {
                            bulletStore.remove(observable);
                        }
                    }
                }
            });
            Bullet newBullet = Bullet.createBullet(mindMapItem);
            for (MindMapItem itemChild : mindMapItem.getChildren()) {
                Bullet childBullet = getOrCreateBullet(itemChild);
                newBullet.getChildren().add(childBullet);
            }
            bulletStore.put(mindMapItem, newBullet);
        }
        return bulletStore.get(mindMapItem);
    }

    public void reconstructBulletTree() {
        reconstructBulletTree(getRootBullet());
    }

    public void reconstructBulletTree(Bullet bulletToReconstruct) {
        if (bulletToReconstruct != null) {
            bulletToReconstruct.getChildren().clear();
            for (MindMapItem itemChild : bulletToReconstruct.getWrappedContent().getChildren()) {
                Bullet childBullet = getOrCreateBullet(itemChild);
                bulletToReconstruct.getChildren().add(childBullet);
                reconstructBulletTree(childBullet);
            }
        }
    }

    public Bullet createMoveBullet(MindMapItem contentToWrap) {
        return Bullet.createBullet(contentToWrap);
    }

    public Bullet getRootBullet() {
        return rootBullet;
    }

    public void setRootBullet(Bullet rootBullet) {
        this.rootBullet = rootBullet;
    }
}
