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
    private static final String DATA = "data";

    public static MindMapItem jsonToMindMap(String json) {
        JsonParser parser = new JsonParser();
        JsonObject rootObj = (JsonObject) parser.parse(json);
        String rootTitle = rootObj.get(TITLE).getAsString();
        JsonArray rootArr = rootObj.getAsJsonArray(DATA);
        return new MindMapItem(rootTitle, createItemList(rootArr));
    }

    private static List<MindMapItem> createItemList(JsonArray array) {
        List<MindMapItem> itemList = new ArrayList<>();
        for (int j = 0; j < array.size(); j++) {
            JsonObject obj = (JsonObject) array.get(j);
            String title = obj.get(TITLE).getAsString();
            JsonArray dataArr = obj.getAsJsonArray(DATA);
            if (dataArr != null && dataArr.size() > 0)
                itemList.add(new MindMapItem(title, createItemList(dataArr)));
            else
                itemList.add(new MindMapItem(title));
        }
        return itemList;
    }

    public static String mindMapToJson(MindMapItem mindMap) {
        JsonObject rootObj = new JsonObject();
        rootObj.addProperty("title", mindMap.getText());
        rootObj.add("data", getRootArray(mindMap.getChildren()));
        return rootObj.toString();
    }

    private static JsonArray getRootArray(List<MindMapItem> children) {
        JsonArray array = new JsonArray();
        for (int i = 0; i < children.size(); i++) {
            JsonObject obj = new JsonObject();
            obj.addProperty("title", children.get(i).getText());
            List<MindMapItem> subChildren = children.get(i).getChildren();
            if (subChildren != null && subChildren.size() > 0)
                obj.add("data", getRootArray(subChildren));
            array.add(obj);
        }
        return array;
    }
}
