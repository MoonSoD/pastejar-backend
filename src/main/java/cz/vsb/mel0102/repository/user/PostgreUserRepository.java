package cz.vsb.mel0102.repository.user;

import cz.vsb.mel0102.database.DatabaseExecutor;
import cz.vsb.mel0102.entities.User;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PostgreUserRepository implements IUserRepository {

    private final DatabaseExecutor executor;

    @SneakyThrows
    @Override
    public Optional<User> findByUsername(String username) {
        var sql = """
        SELECT "User".id as "User.id",
               "User".username as "User.username",
               "User".email as "User.email",
               "User".password as "User.password",
               "User".createdAt as "User.createdAt",
               "User".updatedAt as "User.updatedAt",
               "User".activated as "User.activated",
               "Paste".id as "Paste.id",
               "Paste".content as "Paste.content",
               "Paste".userid as "Paste.userId",
               "Paste".createdat as "Paste.createdAt",
               "Paste".updatedat as "Paste.updatedAt"
        FROM "User" 
        LEFT JOIN "Paste" ON "Paste"."userid" = "User"."id" 
        WHERE "username" = ?
        """;
        return Optional.ofNullable(
                executor.executeQuerySingle(sql,
                        ps -> ps.setString(1, username),
                        User::fromRow
                )
        );
    }

    @SneakyThrows
    @Override
    public Optional<User> findByEmail(String email) {
        var sql = "SELECT * FROM \"User\" WHERE email = ?";
        return Optional.ofNullable(
                executor.executeQuerySingle(sql,
                        ps -> ps.setString(1, email),
                        User::fromRow
                )
        );
    }

    @Override
    public void insert(User entity) {
        var sql = "INSERT INTO \"User\" (username, email, password, createdAt, updatedAt, activated) VALUES (?, ?, ?, ?, ?, ?)";
        executor.executeUpdate(sql, ps -> {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getPassword());
            ps.setTimestamp(4, entity.getCreatedAt());
            ps.setTimestamp(5, entity.getUpdatedAt());
            ps.setBoolean(6, entity.isActivated());
        });
    }

    @Override
    public void update(long id, User entity) {
        var sql = "UPDATE \"User\" SET username = ?, email = ?, password = ?, updated_at = ?, activated = ? WHERE id = ?";
        executor.executeUpdate(sql, ps -> {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getPassword());
            ps.setTimestamp(4, entity.getUpdatedAt());
            ps.setBoolean(5, entity.isActivated());
        });
    }

    @Override
    public void delete(long id) {
        var sql = "DELETE FROM \"User\" WHERE id = ?";
        executor.executeUpdate(sql, ps -> {});
    }

    @Override
    public void deleteAll() {
        var sql = "DELETE FROM \"User\"";
        executor.executeUpdate(sql, ps -> {});
    }

    @SneakyThrows
    @Override
    public Optional<User> findById(long id) {
        var sql = """
                SELECT * FROM "User"
                INNER JOIN "Paste" on "Paste".userId = "User".id"
                WHERE id = ?
        """;
        return Optional.ofNullable(
                executor.executeQuerySingle(sql,
                        ps -> {
                            ps.setLong(1, id);
                        },
                        User::fromRow
                )
        );
    }

    @SneakyThrows
    @Override
    public List<User> findAll() {
        var sql = "SELECT * FROM \"User\"";
        return executor.executeQueryList(sql,
                ps -> {},
                User::fromRow
        );
    }
}
