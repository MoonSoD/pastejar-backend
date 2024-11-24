package cz.vsb.mel0102.database;

import java.sql.SQLException;

@FunctionalInterface
public interface CheckedConsumer<T> {
    void accept(T t) throws SQLException;
}
