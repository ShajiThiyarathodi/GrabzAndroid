package com.cmpe295.grabz;

/**
 * Created by Sina on 4/9/2015.
 */
public class AisleItemDto extends LinksDto {

    private AisleItem aisleItem;

    /**
     *
     */
    public AisleItemDto() {
        super();
        // TODO Auto-generated constructor stub
    }

    public AisleItem getAisleItem() {
        return aisleItem;
    }

    public void setAisleItem(AisleItem aisleItem) {
        this.aisleItem = aisleItem;
    }

    public AisleItemDto (AisleItem aisleItem){
        super();
        this.aisleItem = aisleItem;
    }
}
