package com.cmpe295.grabz.domain;

/**
 * Created by shajithiyarathodi on 4/18/15.
 */
public class StoreItem {
    private ItemModel item;

    /**
     * Price of the item in perticular store.
     */
    private double price;
    private boolean onPromotion;
    private double promotionalPrice;
    private String promotionName;


    public StoreItem(){

    }
    public StoreItem(ItemModel item, double price, boolean onPromotion,
                     double promotionalPrice, String promotionName) {
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
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
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
    public double getPromotionalPrice() {
        return promotionalPrice;
    }

    /**
     * @param promotionalPrice the promotionalPrice to set
     */
    public void setPromotionalPrice(double promotionalPrice) {
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
