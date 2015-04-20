package com.cmpe295.grabz;

/**
 * Created by shajithiyarathodi on 4/18/15.
 */
public class StoreItemDto extends LinksDto {
    private StoreItem storeItem;

    public StoreItemDto() {
        super();
    }

    public StoreItemDto(StoreItem storeItem) {
        super();
        this.storeItem = storeItem;
    }

    public StoreItem getStoreItem() {
        return storeItem;
    }

    public void setStoreItem(StoreItem storeItem) {
        this.storeItem = storeItem;
    }
}
