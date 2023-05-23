package db;

import beans.Product;
import beans.Search;
import beans.SearchRequest;
import beans.SearchType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import marshall.UnmarshallHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SearchTask extends DBtask {

    @Override
    protected String doExecute(String s, int userId) {
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

    private class SearchBuilder {
        private Connection connection;
        private List<Object> parameters = new ArrayList<>();
        private StringBuilder query = new StringBuilder();

        public SearchBuilder(Connection connection) {
            this.connection = connection;
            query.append("SELECT * FROM products WHERE status = ?");
            parameters.add("available");
        }

        public SearchBuilder productType(String productType) {
            if (productType != null && !productType.isEmpty()) {
                query.append(" AND producttype LIKE ?");
                parameters.add("%" + productType + "%");
            }
            return this;
        }

        public SearchBuilder minPrice(float minPrice) {
            if (minPrice >= 0) {
                query.append(" AND price >= ?");
                parameters.add(minPrice);
            }
            return this;
        }

        public SearchBuilder maxPrice(float maxPrice) {
            if (maxPrice >= 0) {
                query.append(" AND price <= ?");
                parameters.add(maxPrice);
            }
            return this;
        }

        public SearchBuilder condition(String condition) {
            if (condition != null && !condition.isEmpty()) {
                query.append(" AND conditions = ?");
                parameters.add(condition);
            }
            return this;
        }

        public PreparedStatement build() throws SQLException {
            PreparedStatement statement = connection.prepareStatement(query.toString());
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }
            return statement;
        }
    }
}

