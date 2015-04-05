package com.cmpe295.grabz;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sina Nikkhah, Amit Dikkar, Shaji Thiyarathodi, Priyanka Deo
 *
 */
public class BasketsDto{

	private List<BasketDto> baskets = new ArrayList<BasketDto>();
	
	/**
	 * 
	 */
	public BasketsDto() {
        baskets = new ArrayList<BasketDto>();
	}

	public BasketsDto (List<BasketDto> basketItems) {
		this.baskets = basketItems;
	}
	
	public List<BasketDto> getBasketItems() {
		return baskets;
	}

	public void setBasketItems(List<BasketDto> basketItems) {
		this.baskets = basketItems;
	}

}
