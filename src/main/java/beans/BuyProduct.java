package beans;

public class BuyProduct {
    public int productId;
    public String productType;

    public float price;

    public String colour;
    public String condition;

    public String productName;
    public int seller;
    public String yearOfProduction;


    @Override
    public boolean equals(Object obj) {
        return obj instanceof BuyProduct && ((BuyProduct) obj).productId == productId;
    }
}
