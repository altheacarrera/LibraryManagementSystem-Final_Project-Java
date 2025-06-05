package example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
 private static final String URL = "jdbc:mysql://localhost:3306/librarydb?useSSL=false&serverTimezone=UTC";
 private static final String USER = "root";
 private static final String PASS = ""; 

 public static Connection getConnection() {
     try {
         Class.forName("com.mysql.cj.jdbc.Driver"); 
         return DriverManager.getConnection(URL, USER, PASS);
     } catch (ClassNotFoundException | SQLException e) {
         e.printStackTrace();
         return null;
     }
 }
}
