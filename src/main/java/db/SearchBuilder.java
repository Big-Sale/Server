package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchBuilder {
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
        System.out.println(query.toString());
        PreparedStatement statement = connection.prepareStatement(query.toString());
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }
        return statement;
    }
}
