package cz.vsb.mel0102.database;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
public class DatabaseExecutor {

    private final DataSource dataSource;

    public int executeUpdate(String sql, CheckedConsumer<PreparedStatement> paramSetter) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            paramSetter.accept(stmt);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("executeUpdate failed: " + e.getMessage(), e);
        }
    }

    public <T> T executeQuerySingle(String sql, CheckedConsumer<PreparedStatement> paramSetter, Function<ResultSet, T> mapper) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            paramSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("executeQuerySingle failed: " + e.getMessage(), e);
        }

        return null;
    }

    public <T> List<T> executeQueryList(String sql, CheckedConsumer<PreparedStatement> paramSetter, Function<ResultSet, T> mapper) {
        List<T> results = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            paramSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    StringBuilder row = new StringBuilder();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        String columnValue = rs.getString(i);
                        row.append(columnName).append("=").append(columnValue).append(", ");
                    }
                    System.out.println("Row data: " + row.toString());

                    results.add(mapper.apply(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("executeQueryList failed " + e.getMessage(), e);
        }

        return results;
    }

}
