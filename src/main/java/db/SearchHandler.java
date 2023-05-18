package db;

import beans.Product;
import beans.Search;
import beans.SearchRequest;
import beans.SearchType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marshall.UnmarshallHandler;

import java.sql.*;
import java.util.LinkedList;

public class SearchHandler extends DBtask {


    @Override
    public String doExecute(String s, int userID) {
        SearchType searchType = UnmarshallHandler.unmarshall(s, SearchType.class);
        SearchRequest buyProductRequest = new SearchRequest();
        buyProductRequest.type = "search";
        buyProductRequest.payload = search(searchType.payload);
        try {
            return new ObjectMapper().writeValueAsString(buyProductRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Product[] search(Search parameters) {
        Connection con = DataBaseConnection.getDatabaseConnection();
        LinkedList<Product> products = new LinkedList<>();
        SearchBuilder searchBuilder = new SearchBuilder(con);
        searchBuilder.productType(parameters.productType)
                .minPrice(parameters.minPrice)
                .maxPrice(parameters.maxPrice)
                .condition(parameters.condition);
        try {
            PreparedStatement stm = searchBuilder.build();
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                Product product = new Product();
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
            return products.toArray(new Product[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static Product[] getRandomProducts() {
        Connection con = DataBaseConnection.getDatabaseConnection();
        String query = "select * from products where status = 'available' order by random() limit 100;";
        LinkedList<Product> products = new LinkedList<>();
        try {
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery(query);
            while (rs.next()) {
                Product product = new Product();
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
        return products.toArray(new Product[0]);
    }


    private String addCondition(String query, String condition) {
        query += " and conditions = '" + condition + "'";
        return query;
    }



}

