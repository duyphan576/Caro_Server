/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author duyph
 */
public class DatabaseConnection {
    public static Connection c;
    private static Statement s;
    private static PreparedStatement p;
    private String host, port, dbName, dbUser, dbPassword;

    public DatabaseConnection() {
        host = "localhost";
        port = "3306";
        dbUser = "root";
        dbName = "caro";
        dbPassword = "";
    }
    
        public void connectDB() {
        String dbPath = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useUnicode=yes&characterEncoding=UTF-8";
        try {
            c = (Connection) DriverManager.getConnection(dbPath, dbUser, dbPassword);
            s = c.createStatement();
            System.out.println("Connected");
        } catch (SQLException ex) {
            System.out.print(ex.getMessage());
        }

    }

    public ResultSet doReadQuery(String sql) {
        ResultSet rs = null;
        try {
            rs = s.executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    public void doUpdateQuery() throws SQLException {
        try {
            p.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConnection() {
        return c;
    }
    
}
