package com.cmpe295.grabz;

/**
 * Created by shajithiyarathodi on 4/18/15.
 */
public class StoreItem {
    private ItemDetail item;
    private float price;

    public ItemDetail getItem() {
        return item;
    }

    public StoreItem() {
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setItem(ItemDetail item) {
        this.item = item;
    }
}
