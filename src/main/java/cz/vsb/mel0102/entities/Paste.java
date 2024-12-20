package cz.vsb.mel0102.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Paste implements IEntity {
    public Long id;

    public String content;

    public Long userId;

    public Timestamp createdAt;

    public Timestamp updatedAt;

    public static Paste fromRow(ResultSet rs) {
        try {
            return Paste.builder()
                    .id(rs.getLong("id"))
                    .content(rs.getString("content"))
                    .userId(rs.getLong("userId"))
                    .createdAt(rs.getTimestamp("createdAt"))
                    .updatedAt(rs.getTimestamp("updatedAt"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Paste fromRowExplicit(ResultSet rs) {
        try {
            return Paste.builder()
                    .id(rs.getLong("Paste.id"))
                    .content(rs.getString("Paste.content"))
                    .userId(rs.getLong("Paste.userId"))
                    .createdAt(rs.getTimestamp("Paste.createdAt"))
                    .updatedAt(rs.getTimestamp("Paste.updatedAt"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Paste{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
