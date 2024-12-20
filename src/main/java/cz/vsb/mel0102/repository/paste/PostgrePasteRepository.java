package cz.vsb.mel0102.repository.paste;

import cz.vsb.mel0102.database.DatabaseExecutor;
import cz.vsb.mel0102.entities.Paste;
import cz.vsb.mel0102.entities.User;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class PostgrePasteRepository implements IPasteRepository {

    private final DatabaseExecutor executor;

    @Override
    public void insert(Paste entity) {
        var sql = """
            INSERT INTO "Paste" (title, createdAt, updatedAt)
            VALUES (?, ?, ?)""";

        executor.executeUpdate(sql, ps -> {
            ps.setString(1, entity.getContent());
            ps.setTimestamp(2, entity.getCreatedAt());
            ps.setTimestamp(3, entity.getCreatedAt());
        });
    }

    @Override
    public void update(long id, Paste entity) {
        var sql = """
            UPDATE "Paste"
            SET content = ?
            WHERE id = ?""";

        executor.executeUpdate(sql, ps -> {
            ps.setString(1, entity.getContent());
            ps.setLong(2, id);
        });
    }

    @Override
    public void delete(long id) {
        var sql = """
            DELETE FROM "Paste"
            WHERE id = ?""";

        executor.executeUpdate(sql, ps -> {
            ps.setLong(1, id);
        });
    }

    @Override
    public void deleteAll() {
        var sql = """
            DELETE FROM "Paste" """;

        executor.executeUpdate(sql, ps -> {});
    }

    @Override
    public Optional<Paste> findById(long id) {
        var sql = """
            SELECT * FROM "Paste"
            WHERE id = ?""";

        return Optional.ofNullable(
                executor.executeQuerySingle(sql, ps -> {
                    ps.setLong(1, id);
                }, Paste::fromRow)
        );
    }

    @Override
    public List<Paste> findAll() {
        var sql = """
            SELECT * FROM "Paste" """;

        return executor.executeQueryList(sql, ps -> {}, Paste::fromRow);
    }

    @Override
    public List<User> deleteExpired() {
        var sqlGetExpired =
            """
            SELECT DISTINCT
                "User"."id" as "User.id",
                "User"."username" as "User.username",
                "User"."email" as "User.email",
                "User"."password" as "User.password",
                "User"."createdat" as "User.createdat",
                "User"."updatedat" as "User.updatedat",
                "User"."activated" as "User.activated",
                "Paste"."id" as "Paste.id",
                "Paste"."content" as "Paste.content",
                "Paste"."userid" as "Paste.userid",
                "Paste"."createdat" as "Paste.createdat",
                "Paste"."updatedat" as "Paste.updatedat"
            FROM "User"
            LEFT JOIN "Paste" ON "Paste"."userid" = "User"."id"
            WHERE "Paste"."createdat" < CURRENT_DATE - INTERVAL '1 day'
            GROUP BY "User.id", "Paste.id"
            """;

        var expiredPastes = executor.executeQueryList(sqlGetExpired, ps -> {}, User::fromRowWithoutPastes);

        var sqlDeleteExpired = """
                DELETE FROM "Paste"
                WHERE "createdat" < CURRENT_DATE - INTERVAL '1 month'
                """;

        //executor.executeUpdate(sqlDeleteExpired, ps -> {});

        return expiredPastes;
    }
}
