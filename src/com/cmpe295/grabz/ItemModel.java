package com.cmpe295.grabz;

public class ItemModel {

	/**
	 * Unique id of the item.
	 */
	private String _id;
	
	/**
	 * Name of the item.
	 */
	private String name;
	
	/**
	 * Brand of the item.
	 */
	private String brand;
	
	/**
	 * Small description of the item.
	 */
	private String description;
	
	public ItemModel(String _id, String name, String brand, String description,
			String color, String size) {
		super();
		this._id = _id;
		this.name = name;
		this.brand = brand;
		this.description = description;
		this.color = color;
		this.size = size;
	}

	/**
	 * Color of the item.
	 */
	private String color;
	
	/**
	 * Size of the item.
	 */
	private String size;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {  
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
}
