package com.cmpe295.grabz;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the document of "baskets" collection.
 * Document Structure:
 * <pre>
 * {
    "_id": {
        "$oid": "550cc05215fa7b03d91cd93e"
    },
    "name": "MyBasket-1",
    "itemIds": ["id1","id2","id3"]
}
 * </pre>
 * @author Sina Nikkhah, Amit Dikkar, Shaji Thiyarathodi, Priyanka Deo
 *
 */
public class BasketModel {

	/**
	 * Name of the basket. 
	 * **/
	private String name;
	
	/**
	 * List of itemIds this basket contains. 
	 * **/
	private List<String> itemIds = new ArrayList<String>();
	
	public BasketModel(){
	}

	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the itemIds
	 */
	public List<String> getItemIds() {
		return itemIds;
	}

	/**
	 * @param itemIds the itemIds to set
	 */
	public void setItemIds(List<String> itemIds) {
		this.itemIds = itemIds;
	}
}
