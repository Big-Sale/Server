package beans;

public class ProductWithId {
    public String productType;
    public float price;
    public String date;
    public String colour;
    public int condition;
    public int status;
    public String productName;
    public int userId;

    public ProductWithId(Product product, int id) {
        productType = product.productType;
        price = product.price;
        colour = product.colour;
        condition = product.condition;
        status = 1;
        productName = product.productName;
        userId = id;
        date = product.date;
    }
}
