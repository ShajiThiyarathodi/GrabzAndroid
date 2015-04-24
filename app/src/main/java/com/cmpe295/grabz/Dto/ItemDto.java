package com.cmpe295.grabz.Dto;

import com.cmpe295.grabz.domain.ItemModel;

/**
 * Created by Sina on 4/15/2015.
 */
public class ItemDto extends LinksDto {

    private ItemModel item;

    public ItemDto(){

    }
    /**
     * @param item
     */
    public ItemDto(ItemModel item) {
        super();
        this.item = item;
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
}
