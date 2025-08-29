package bws.hofheim.project.service;

import bws.hofheim.project.api.model.User;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class UserService {

    public User getUser(int userID) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT userid, username, role, status FROM users WHERE userid =" + userID)) {

            while (rs.next()) {
                System.out.println("ppppppppppppp");
                User userInDB = new User(rs.getInt(1), rs.getString(2),rs.getString(3), rs.getString(4));
                System.out.println(user);
                return userInDB;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
}
