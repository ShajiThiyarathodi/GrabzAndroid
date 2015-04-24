package com.cmpe295.grabz.domain;

/**
 * Created by Sina on 4/22/2015.
 */
public class BasketItemDetail {
    String itemId;

    String name;

    boolean collected;

    String aisleNum;

    public BasketItemDetail(){

    }

    /**
     * @param itemId
     * @param name
     * @param isCollected
     * @param aisleNum
     */
    public BasketItemDetail(String itemId, String name, boolean isCollected,
                            String aisleNum) {
        super();
        this.itemId = itemId;
        this.name = name;
        this.collected = isCollected;
        this.aisleNum = aisleNum;
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

/*	*//**
     * @return the isCollected
     *//*
	public boolean isCollected() {
		return isCollected;
	}
	*//**
     * @param isCollected the isCollected to set
     *//*
	public void setCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}*/

    /**
     * @return the aisleNum
     */
    public String getAisleNum() {
        return aisleNum;
    }

    /**
     * @param aisleNum the aisleNum to set
     */
    public void setAisleNum(String aisleNum) {
        this.aisleNum = aisleNum;
    }

    /**
     * @return the collected
     */
    public boolean getCollected() {
        return collected;
    }

    /**
     * @param collected the collected to set
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }
}
