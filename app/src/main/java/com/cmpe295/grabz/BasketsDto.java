package com.cmpe295.grabz;

import java.util.List;

/**
 * @author Sina Nikkhah, Amit Dikkar, Shaji Thiyarathodi, Priyanka Deo
 *
 */
public class BasketsDto extends LinksDto {

	private List<BasketModel> baskets;
	
	/**
	 * 
	 */
	public BasketsDto() {
		super();
	}

	public BasketsDto (List<BasketModel> basketItems) {
		super();
		this.baskets = basketItems;
	}
	
	public List<BasketModel> getBasketItems() {
		return baskets;
	}

	public void setBasketItems(List<BasketModel> basketItems) {
		this.baskets = basketItems;
	}

}
