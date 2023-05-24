package beans;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class OrderHistoryProduct {
    public int productId;
    public String productType;
    public float price;
    public String colour;
    public String condition;
    public String productName;
    public int seller;
    public String yearOfProduction;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")

    public Date dateOfPurchase;
}
