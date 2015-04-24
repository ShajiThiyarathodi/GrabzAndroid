package com.cmpe295.grabz.Dto;

import com.cmpe295.grabz.domain.BasketItemDetail;

/**
 * Created by Sina on 4/22/2015.
 */
public class BasketItemDetailDto extends LinksDto{

    BasketItemDetail basketItemDetail;

    public BasketItemDetailDto(){
        super();
    }

    /**
     * @param basketItemDetail
     */
    public BasketItemDetailDto(BasketItemDetail basketItemDetail) {
        super();
        this.basketItemDetail = basketItemDetail;
    }

    /**
     * @return the basketItemDetail
     */
    public BasketItemDetail getBasketItemDetail() {
        return basketItemDetail;
    }

    /**
     * @param basketItemDetail the basketItemDetail to set
     */
    public void setBasketItemDetail(BasketItemDetail basketItemDetail) {
        this.basketItemDetail = basketItemDetail;
    }
}
