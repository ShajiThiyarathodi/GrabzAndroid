/**
 *
 */
package com.cmpe295.grabz.Dto;

import com.cmpe295.grabz.domain.BasketModel;

/**
 * @author Sina Nikkhah, Amit Dikkar, Shaji Thiyarathodi, Priyanka Deo
 *
 */
public class BasketDto  extends LinksDto {

    /**
     *
     */
    public BasketDto() {
        super();
        // TODO Auto-generated constructor stub
    }

    private BasketModel basket;

    public BasketDto (BasketModel basketItem){
        super();
        this.basket = basketItem;
    }

    public BasketModel getBasket() {
        return basket;
    }

    public void setBasketItem(BasketModel basketItem) {
        this.basket = basketItem;
    }
}