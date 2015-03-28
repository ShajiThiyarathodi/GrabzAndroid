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
	 * Auto-generated document id. No need to set this field explicitly. 
	 * **/

	private String _id;
	
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

	/**
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * @return the name
	 */
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
