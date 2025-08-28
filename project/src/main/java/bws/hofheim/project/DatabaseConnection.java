package bws.hofheim.project;

import java.sql.*;

public class DatabaseConnection {
    public void ConnectToDataBase() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "worti";

        try (Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version()")) {

                while (rs.next()) {
                    System.out.println("Postgres Version: " + rs.getString(1));
                }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
/*
    public

             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT version()")) {

            while (rs.next()) {
                System.out.println("Postgres Version: " + rs.getString(1));
            }

            ResultSet result = stmt.executeQuery("insert into words (wordid, word) values (55, 'aaaaa'); ");

            while (result.next()) {
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */

        public class GetUser  {
        ResultSet userData = stmt.executeQuery("SELECT userid, username, role FROM  users WHERE username =" + frontendUsername);
        while (userData.next()) {
            return userData;
            }

        }



    }
