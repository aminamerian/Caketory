package a2.thesis.com.caketory.Entity;

/**
 * Created by Amin on 24/01/2018.
 */

public class ItemProduct {
    private long productId;
    private String productName;
    private long categoryId;
    private int productPrice;
    private String productImage;
    private String productImage2;
    private String productDescription;
    private String productDesDetail;
    private int productStock;

    public ItemProduct(long productId, String productName, String productImage, int productPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
    }

    public ItemProduct(long productId, String productName, String productImage, int productPrice, String productDescription) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
    }

    public ItemProduct(long productId, String productName, long categoryId,
                       String productImage, String productImage2, int productPrice,
                       int productStock, String productDescription, String productDesDetail) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.productImage = productImage;
        this.setProductImage2(productImage2);
        this.productPrice = productPrice;
        this.setProductStock(productStock);
        this.productDescription = productDescription;
        this.setProductDesDetail(productDesDetail);
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getProductStock() {
        return productStock;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }

    public String getProductImage2() {
        return productImage2;
    }

    public void setProductImage2(String productImage2) {
        this.productImage2 = productImage2;
    }

    public String getProductDesDetail() {
        return productDesDetail;
    }

    public void setProductDesDetail(String productDesDetail) {
        this.productDesDetail = productDesDetail;
    }
}
