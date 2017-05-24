package com.ysy.mindmap.uis.mindmap;

import com.ysy.mindmap.models.MindMapItem;

public interface MindMapItemActionRequestListener {

    boolean requestBecomeChild(MindMapItem requestedParent, MindMapItem requestChild, int index);

    boolean requestSwap(MindMapItem firstItem, MindMapItem secondItem);
}
