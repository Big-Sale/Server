package db;

import beans.BuyProduct;
import beans.Product;
import beans.Search;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import com.fasterxml.jackson.databind.JsonNode;

public class SearchHandler extends DBtask {


    @Override
    public JsonNode doExecute(JsonNode node) {

        /*unmarshal
                prata med db
                få tillbaka
                        marshall
                                return node;*/
        return null;
    }

    public BuyProduct[] search(Search parameters) {
        Connection con = DataBaseConnection.getDatabaseConnection();
        String query = "select * from products where status = 'available' and producttype = '" + parameters.productType + "' and price between " + parameters.minPrice + " and " + parameters.maxPrice + " and conditions = '" + parameters.condition + "';";
        LinkedList<BuyProduct> products = new LinkedList<>();
        try {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                BuyProduct product = new BuyProduct();
                product.productId = rs.getInt("productid");
                product.productType = rs.getString("producttype");
                product.price = rs.getFloat("price");
                product.colour = rs.getString("colour");
                product.productName = rs.getString("productname");
                product.condition = rs.getString("conditions");
                product.seller = rs.getInt("seller");
                product.yearOfProduction = rs.getString("yearofproduction");
                products.add(product);
            }
            rs.close();
            stm.close();
            con.close();
            return products.toArray(new BuyProduct[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static LinkedList<BuyProduct> getRandomProducts() {
        Connection con = DataBaseConnection.getDatabaseConnection();
        String query = "select * from products where status = 'available' order by random() limit 5;";
        LinkedList<BuyProduct> products = new LinkedList<>();
        try {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                BuyProduct product = new BuyProduct();
                product.productId = rs.getInt("productid");
                product.productType = rs.getString("producttype");
                product.price = rs.getFloat("price");
                product.colour = rs.getString("colour");
                product.productName = rs.getString("productname");
                product.condition = rs.getString("conditions");
                product.seller = rs.getInt("seller");
                product.yearOfProduction = rs.getString("yearofproduction");
                if (!products.contains(product)) {
                    products.add(product);
                }
            }
            rs.close();
            stm.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }


    private String addCondition(String query, String condition) {
        query += " and conditions = '" + condition + "'";
        return query;
    }



}

