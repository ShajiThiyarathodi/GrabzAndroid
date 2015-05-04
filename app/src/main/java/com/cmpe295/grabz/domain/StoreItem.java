package com.cmpe295.grabz.domain;

/**
 * Created by shajithiyarathodi on 4/18/15.
 */
public class StoreItem {
    private ItemModel item;

    /**
     * Price of the item in particular store.
     */
    private float price;
    private boolean onPromotion;
    private float promotionalPrice;
    private String promotionName;


    public StoreItem(){

    }
    public StoreItem(ItemModel item, float price, boolean onPromotion,
                     float promotionalPrice, String promotionName) {
        super();
        this.item = item;
        this.price = price;
        this.onPromotion = onPromotion;
        this.promotionalPrice = promotionalPrice;
        this.promotionName = promotionName;
    }

    /**
     * @return the item
     */
    public ItemModel getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(ItemModel item) {
        this.item = item;
    }

    /**
     * @return the price
     */
    public float getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(float price) {
        this.price = price;
    }

    /**
     * @return the onPromotion
     */
    public boolean isOnPromotion() {
        return onPromotion;
    }

    /**
     * @param onPromotion the onPromotion to set
     */
    public void setOnPromotion(boolean onPromotion) {
        this.onPromotion = onPromotion;
    }

    /**
     * @return the promotionalPrice
     */
    public float getPromotionalPrice() {
        return promotionalPrice;
    }

    /**
     * @param promotionalPrice the promotionalPrice to set
     */
    public void setPromotionalPrice(float promotionalPrice) {
        this.promotionalPrice = promotionalPrice;
    }

    /**
     * @return the promotionName
     */
    public String getPromotionName() {
        return promotionName;
    }

    /**
     * @param promotionName the promotionName to set
     */
    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }
}
