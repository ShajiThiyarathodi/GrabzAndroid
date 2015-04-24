package com.cmpe295.grabz.domain;

/**
 * Created by Sina on 4/23/2015.
 */
public class ItemModel {
    private String itemId;

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

    /**
     * Color of the item.
     */
    private String color;

    /**
     * Size of the item.
     */
    private String size;

    /**
     * Category of the item.
     * **/
    private String category;

    /**
     * Image url of the item.
     * **/
    private String imageUrl;

    public ItemModel(String itemId, String name, String brand,
                     String description, String color, String size, String category,
                     String imageUrl) {
        this.itemId = itemId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.color = color;
        this.size = size;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public ItemModel(){

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
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @param brand the brand to set
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId the itemId to set
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @param imageUrl the imageUrl to set
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
