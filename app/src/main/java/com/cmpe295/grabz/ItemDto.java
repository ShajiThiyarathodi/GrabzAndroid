package com.cmpe295.grabz;

/**
 * Created by Sina on 4/15/2015.
 */
public class ItemDto extends LinksDto{

    private ItemSearchModel item;

    public ItemDto(){

    }
    /**
     * @param item
     */
    public ItemDto(ItemSearchModel item) {
        super();
        this.item = item;
    }

    /**
     * @return the item
     */
    public ItemSearchModel getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(ItemSearchModel item) {
        this.item = item;
    }
}
