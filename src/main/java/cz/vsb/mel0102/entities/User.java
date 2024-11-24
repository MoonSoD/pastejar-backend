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

    public static User fromRow(ResultSet rs)  {
        try {
            return User.builder()
                    .id(rs.getLong("id"))
                    .username(rs.getString("username"))
                    .email(rs.getString("email"))
                    .password(rs.getString("password"))
                    .createdAt(rs.getTimestamp("createdAt"))
                    .updatedAt(rs.getTimestamp("updatedAt"))
                    .activated(rs.getBoolean("activated"))
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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
