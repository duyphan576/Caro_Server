/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author duyph
 */
public class UserDAL extends DatabaseConnection {

    public UserDAL() {
        super();
        this.connectDB();
    }

    public int addUser(User us) throws SQLException {
        String query = "INSERT INTO user (Username, Password, Nickname, Sex, Birthday, isBlocked, Avatar) VALUES (?, ?, ?, ?, ?, 1)";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setString(1, us.getUserName());
        p.setString(2, us.getPassword());
        p.setString(3, us.getNickname());
        p.setInt(4, us.getSex());
        p.setDate(5, us.getBirthday());
        int result = p.executeUpdate();
        return result;
    }

    public int updateUser(User us) throws SQLException {
        String query = "UPDATE user SET Password = ?, Nickname = ?, Sex = ?, Birthday = ?, isBlocked = ? WHERE UserId = ?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setString(1, us.getPassword());
        p.setString(2, us.getNickname());
        p.setInt(3, us.getSex());
        p.setDate(4, us.getBirthday());
        p.setInt(6, us.getUserId());
        int result = p.executeUpdate();
        return result;
    }

    public ArrayList loadUser() throws SQLException {
        String query = "SELECT * FROM User";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        ResultSet rs = p.executeQuery();
        ArrayList<User> userList = new ArrayList();
        if (rs != null) {
            while (rs.next()) {
                User us = new User();
                us.setUserId(rs.getInt("UserId"));
                us.setUserName(rs.getString("Username"));
                us.setPassword(rs.getString("Password"));
                us.setNickname(rs.getString("Nickname"));
                us.setSex(rs.getInt("Sex"));
                us.setBirthday(rs.getDate("Birthday"));
                us.setIsBlocked(rs.getInt("isBlocked"));
                userList.add(us);
            }
        }
        return userList;
    }

    public User findUser(int id) throws SQLException {
        String query = "SELECT * FROM User WHERE UserId = ?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setInt(1, id);
        ResultSet rs = p.executeQuery();
        User us = new User();
        if (rs != null) {
            while (rs.next()) {
                us.setUserId(rs.getInt("UserId"));
                us.setUserName(rs.getString("Username"));
                us.setPassword(rs.getString("Password"));
                us.setNickname(rs.getString("Nickname"));
                us.setSex(rs.getInt("Sex"));
                us.setBirthday(rs.getDate("Birthday"));
                us.setIsBlocked(rs.getInt("isBlocked"));
            }
        }
        return us;
    }

    public Boolean checkDuplicate(String Username) throws SQLException {
        String query = "SELECT * FROM user WHERE username = ?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setString(1, Username);
        ResultSet rs = p.executeQuery();
        return rs.next();
    }

    public User verifyUser(User user) throws SQLException {
        String query = "SELECT * FROM user WHERE username = ? AND password = ? AND isBlocked = 'flase'";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setString(1, user.getUserName());
        p.setString(2, user.getPassword());
        ResultSet rs = p.executeQuery();
        User us = new User();
        if (rs != null) {
            while (rs.next()) {
                us.setUserId(rs.getInt("UserId"));
                us.setUserName(rs.getString("Username"));
                us.setPassword(rs.getString("Password"));
                us.setNickname(rs.getString("Nickname"));
                us.setSex(rs.getInt("Sex"));
                us.setBirthday(rs.getDate("Birthday"));
                us.setIsBlocked(rs.getInt("isBlocked"));
            }
        }
        return us;
    }

    public static void main(String[] args) throws SQLException {
//        UserDAL dal = new UserDAL();
//        User us = new User();
//        us.setUserName("Admin");
//        us.setPassword("e3afed0047b08059d0fada10f400c1e5");
//        User user = dal.verifyUser(us);
//        System.out.println(user.getUserName());
//        if (user.getUserId() != 0) {
//            System.out.println("True");
//        } else {
//            System.out.println("False");
//        }
    }
}
