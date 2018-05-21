package a2.thesis.com.caketory.Entity;

/**
 * Created by Amin on 21/05/2018.
 */

public class ItemOrder {
    private long itemId;
    private long orderId;
    private long productId;
    private int quantity;
    private String productName;
    private long categoryId;
    private int productPrice;
    private String productImage;
    private String productImage2;
    private String productDescription;

    public ItemOrder(long itemId, long orderId, long productId, int quantity, String productName, long categoryId,
                     String productImage, String productImage2, int productPrice, String productDescription) {
        this.setItemId(itemId);
        this.setOrderId(orderId);
        this.setProductId(productId);
        this.setQuantity(quantity);
        this.setProductName(productName);
        this.setCategoryId(categoryId);
        this.setProductImage(productImage);
        this.setProductImage2(productImage2);
        this.setProductPrice(productPrice);
        this.setProductDescription(productDescription);
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductImage2() {
        return productImage2;
    }

    public void setProductImage2(String productImage2) {
        this.productImage2 = productImage2;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
}