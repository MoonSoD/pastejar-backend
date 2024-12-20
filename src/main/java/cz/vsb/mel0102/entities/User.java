package cz.vsb.mel0102.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User implements IEntity, Serializable {
    public Long id;

    public String username;

    public String email;

    @JsonIgnore
    public String password;

    public Timestamp createdAt;

    public Timestamp updatedAt;

    public boolean activated;

    public List<Paste> pastes;

    public static User fromRow(ResultSet rs, boolean withPastes)  {
        User user = null;
        try {
            user = User.builder()
                    .id(rs.getLong("User.id"))
                    .username(rs.getString("User.username"))
                    .email(rs.getString("User.email"))
                    .password(rs.getString("User.password"))
                    .createdAt(rs.getTimestamp("User.createdAt"))
                    .updatedAt(rs.getTimestamp("User.updatedAt"))
                    .activated(rs.getBoolean("User.activated"))
                    .pastes(new ArrayList<>())
                    .build();

            if (withPastes) {
                do {
                    Paste paste = Paste.fromRowExplicit(rs);
                    user.pastes.add(paste);
                } while (rs.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static User fromRow(ResultSet rs) {
        return fromRow(rs, true);
    }

    public static User fromRowWithoutPastes(ResultSet rs) {
        return fromRow(rs, false);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", activated=" + activated +
                '}';
    }
}
