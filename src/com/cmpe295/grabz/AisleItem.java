package com.cmpe295.grabz;

public class AisleItem {

	private String itemId;

	private float price;

	public AisleItem(String itemId, float price) {
		super();
		this.itemId = itemId;
		this.price = price;
	}


	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

}
