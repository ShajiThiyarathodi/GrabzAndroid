package com.cmpe295.grabz.domain;

/**
 * Created by Sina on 4/22/2015.
 */
public class BasketItem {
    private String itemId;

    private String aisleNum = "?";

    private boolean isCollected = false;

    public BasketItem(){

    }
    public BasketItem(String itemId, String aisleNum, boolean isCollected) {
        super();
        this.itemId = itemId;
        this.aisleNum = aisleNum;
        this.isCollected = isCollected;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getAisleNum() {
        return aisleNum;
    }

    public void setAisleNum(String aisleNum) {
        this.aisleNum = aisleNum;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }
}
