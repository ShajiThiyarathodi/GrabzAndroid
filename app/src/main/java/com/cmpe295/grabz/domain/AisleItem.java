package com.cmpe295.grabz.domain;

public class AisleItem{

    /**
     * This id represents one of the items of Items collection.
     * **/
    private String itemId;

    /**
     * Name of the item.
     * **/
    private String name;

    /**
     * Price of the Item (of given id) in a specific outlet.
     * **/
    private float price;

    /**
     * Url to image of the item
     */
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * @return the itemId
     */
    public AisleItem(){}
    public String getItemId() {
        return itemId;
    }
    private boolean onPromotion = false;
    private float promotionalPrice = 0;
    private String promotionName = "NO_NAME";

    public boolean isOnPromotion() {
        return onPromotion;
    }

    public void setOnPromotion(boolean onPromotion) {
        this.onPromotion = onPromotion;
    }

    public float getPromotionalPrice() {
        return promotionalPrice;
    }

    public void setPromotionalPrice(float promotionalPrice) {
        this.promotionalPrice = promotionalPrice;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public void setItemId(String itemId){
        this.itemId = itemId;
		/*try{
			System.out.println("Inside AisleItem - setting the item name");
			ItemsDao dao = new ItemsDaoImpl();
			this.name = dao.getItemNameById(itemId);
		}
		catch(UnknownHostException ex){
			this.name = "NO_NAME";
		}*/
    }

    /**
     * @return the price
     */
    public float getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(float price) {
        this.price = price;
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
}