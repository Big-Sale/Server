package beans;

public class PendingOrder {
    public int buyerId;
    public String buyer;
    // Detta ska ändras så att bönan innehåler information om det vi exakt vill få in i pendingorders i frontend.
    // Måste göra om funktionen för selectpendingorders i SQL så att den returnerar userID + alla vi vill ha som ett table.
    public Product product;
}
