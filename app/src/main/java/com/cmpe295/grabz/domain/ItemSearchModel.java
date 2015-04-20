package com.cmpe295.grabz.domain;

/**
 * Created by Sina on 4/15/2015.
 */
public class ItemSearchModel {
    private String itemId;
    private String name;

    public ItemSearchModel(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
