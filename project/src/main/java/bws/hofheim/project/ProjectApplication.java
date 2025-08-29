package bws.hofheim.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);

// test

    String url = "jdbc:postgresql://localhost:5432/postgres";
    String user = "postgres";
    String password = "worti";
    try (Connection conn = DriverManager.getConnection(url, user, password);
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT version()")) {

    } catch (SQLException ex) {
        throw new RuntimeException(ex);
    }
}
}
