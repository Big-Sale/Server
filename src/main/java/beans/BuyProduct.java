package beans;

public class BuyProduct {

    public int productId;
    public String productName;
    public String productType;
    public float price;
    public String yearOfProduction;
    public String colour;
    public String condition;
    public int seller;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BuyProduct && ((BuyProduct) obj).productId == productId;
    }
}
