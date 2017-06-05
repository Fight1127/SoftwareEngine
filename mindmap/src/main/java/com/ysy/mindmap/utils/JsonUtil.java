package com.ysy.mindmap.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ysy.mindmap.models.MindMapItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/3.
 */

public class JsonUtil {

    private static final String TITLE = "title";
    private static final String IS_LOCKED = "is_locked";
    private static final String LOCKED_BY = "locked_by";
    private static final String DATA = "data";
    private static final String HISTORY = "history";

    public static MindMapItem jsonToMindMap(String json) {
        JsonParser parser = new JsonParser();
        JsonObject rootObj = (JsonObject) parser.parse(json);
        String rootTitle = "";
        String history = "";
        byte isLocked = 0;
        long lockedBy = -1;

        if (rootObj.get(TITLE) != null)
            rootTitle = rootObj.get(TITLE).getAsString();
        if (rootObj.get(IS_LOCKED) != null)
            isLocked = rootObj.get(IS_LOCKED).getAsByte();
        if (rootObj.get(LOCKED_BY) != null)
            lockedBy = rootObj.get(LOCKED_BY).getAsLong();
        if (rootObj.get(HISTORY) != null)
            history = rootObj.get(HISTORY).getAsString();
        JsonArray rootArr = rootObj.getAsJsonArray(DATA);

        return new MindMapItem(rootTitle, isLocked, lockedBy, history, createItemList(rootArr));
    }

    // 多叉树递归
    private static List<MindMapItem> createItemList(JsonArray array) {
        List<MindMapItem> itemList = new ArrayList<>();
        for (int j = 0; j < array.size(); j++) {
            JsonObject obj = (JsonObject) array.get(j);
            String title = "";
            String history = "";
            byte isLocked = 0;
            long lockedBy = -1;

            if (obj.get(TITLE) != null)
                title = obj.get(TITLE).getAsString();
            if (obj.get(IS_LOCKED) != null)
                isLocked = obj.get(IS_LOCKED).getAsByte();
            if (obj.get(LOCKED_BY) != null)
                lockedBy = obj.get(LOCKED_BY).getAsLong();
            if (obj.get(HISTORY) != null)
                history = obj.get(HISTORY).getAsString();
            JsonArray dataArr = obj.getAsJsonArray(DATA);

            if (dataArr != null && dataArr.size() > 0)
                itemList.add(new MindMapItem(title, isLocked, lockedBy, history, createItemList(dataArr)));
            else
                itemList.add(new MindMapItem(title, isLocked, lockedBy, history));
        }
        return itemList;
    }

    public static String mindMapToJson(MindMapItem mindMap) {
        JsonObject rootObj = new JsonObject();
        rootObj.addProperty(TITLE, mindMap.getText());
        rootObj.addProperty(IS_LOCKED, mindMap.getIsLocked());
        rootObj.addProperty(LOCKED_BY, mindMap.getLockedBy());
        rootObj.addProperty(HISTORY, mindMap.getHistory());
        rootObj.add(DATA, getRootArray(mindMap.getChildren()));
        return rootObj.toString();
    }

    // 多叉树递归
    private static JsonArray getRootArray(List<MindMapItem> children) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < children.size(); i++) {
            JsonObject obj = new JsonObject();
            obj.addProperty(TITLE, children.get(i).getText());
            obj.addProperty(IS_LOCKED, children.get(i).getIsLocked());
            obj.addProperty(LOCKED_BY, children.get(i).getLockedBy());
            obj.addProperty(HISTORY, children.get(i).getHistory());
            List<MindMapItem> subChildren = children.get(i).getChildren();
            if (subChildren != null && subChildren.size() > 0)
                obj.add(DATA, getRootArray(subChildren));
            array.add(obj);
        }
        return array;
    }
}
