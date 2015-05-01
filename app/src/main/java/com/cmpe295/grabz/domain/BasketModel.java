package com.cmpe295.grabz.domain;

import java.util.ArrayList;
import java.util.List;

public class BasketModel {

    /**
     * Name of the basket.
     * **/
    private String name;

    boolean updateRequired = false;

    /**
     * List of itemIds this basket contains.
     * **/
    //private List<String> itemIds = new ArrayList<String>();

    private List<BasketItem> basketItems = new ArrayList<BasketItem>();

    public BasketModel(){
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

    /**
     * @return the updateRequired
     */
    public boolean getUpdateRequired() {
        return updateRequired;
    }

    /**
     * @param updateRequired the updateRequired to set
     */
    public void setUpdateRequired(boolean updateRequired) {
        this.updateRequired = updateRequired;
    }

    /**
     * @return the basketItems
     */
    public List<BasketItem> getBasketItems() {
        return basketItems;
    }

    /**
     * @param basketItems the basketItems to set
     */
    public void setBasketItems(List<BasketItem> basketItems) {
        this.basketItems = basketItems;
    }

}
