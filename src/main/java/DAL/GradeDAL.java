/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.Grade;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author duyph
 */
public class GradeDAL extends DatabaseConnection {

    public GradeDAL() {
        super();
        this.connectDB();
    }

    public int addGrade(Grade gr) throws SQLException {
        String query = "INSERT INTO grade (`UserId`, `Grade`, `WinMatch`, `LoseMatch`, `DrawMatch`, `CurrentWinStreak`, `MaxWinStreak`, `CurrentLoseStreak`, `MaxLoseStreak`)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement p = GradeDAL.getConnection().prepareStatement(query);
        p.setInt(1, gr.getUserId());
        p.setInt(2, gr.getGrade());
        p.setInt(3, gr.getWinMatch());
        p.setInt(4, gr.getLoseMatch());
        p.setInt(5, gr.getDrawMatch());
        p.setInt(6, gr.getCurrentWinStreak());
        p.setInt(7, gr.getMaxWinStreak());
        p.setInt(8, gr.getCurrentLoseStreak());
        p.setInt(1, gr.getMaxLoseStreak());
        int result = p.executeUpdate();
        return result;
    }

    public Grade setGrade(int id) throws SQLException {
        String query = "SELECT * FROM grade WHERE UserId = ?";
        PreparedStatement p = GradeDAL.getConnection().prepareStatement(query);
        p.setInt(1, id);
        ResultSet rs = p.executeQuery();
        Grade gr = new Grade();
        if (rs != null) {
            while (rs.next()) {
                gr.setUserId(rs.getInt("UserId"));
                gr.setGrade(rs.getInt("Grade"));
                gr.setWinMatch(rs.getInt("WinMatch"));
                gr.setLoseMatch(rs.getInt("LoseMatch"));
                gr.setDrawMatch(rs.getInt("DrawMatch"));
                gr.setCurrentWinStreak(rs.getInt("CurrentWinStreak"));
                gr.setMaxWinStreak(rs.getInt("MaxWinStreak"));
                gr.setCurrentLoseStreak(rs.getInt("CurrentLoseStreak"));
                gr.setMaxLoseStreak(rs.getInt("MaxLoseStreak"));
            }
        }
        return gr;
    }

    public int getMatch(int id) throws SQLException {
        String query = "SELECT WinMatch, LoseMatch, DrawMatch FROM Grade where UserID=?";
        PreparedStatement p = GradeDAL.getConnection().prepareStatement(query);
        p.setInt(1, id);
        ResultSet rs = p.executeQuery();
        int all = 0;
        if (rs != null) {
            while (rs.next()) {
                all = rs.getInt("WinMatch") + rs.getInt("LoseMatch") + rs.getInt("DrawMatch");
            }
        }
        return all;
    }

    public double getWinRate(int id) throws SQLException {
        String query = "SELECT WinMatch FROM Grade where UserID=?";
        PreparedStatement p = GradeDAL.getConnection().prepareStatement(query);
        p.setInt(1, id);
        ResultSet rs = p.executeQuery();
        double rate = 0;
        if (rs != null) {
            while (rs.next()) {
                rate = rs.getInt("WinMatch");
            }
        }
        return rate;
    }
    
    public static void main(String[] args) throws SQLException {
        GradeDAL g = new GradeDAL();
        System.out.println(g.getMatch(1));
    }
}
