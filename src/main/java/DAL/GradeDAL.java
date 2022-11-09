/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.Grade;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        PreparedStatement p = this.getConnection().prepareStatement(query);
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

    public Grade getGrade(int id) throws SQLException {
        String query = "SELECT * FROM grade WHERE UserId = ?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
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
        PreparedStatement p = this.getConnection().prepareStatement(query);
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

    public float getWinRate(int id) throws SQLException {
        String query = "SELECT WinMatch FROM Grade where UserID=?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setInt(1, id);
        ResultSet rs = p.executeQuery();
        float rate = 0;
        if (rs != null) {
            while (rs.next()) {
                rate = (float) rs.getInt("WinMatch") / getMatch(id);
            }
        }
        return (rate * 100);
    }

    public int updateWinMatch(int id) throws SQLException {
        Grade gr = getGrade(id);
        String query = "";
        if (gr.getCurrentWinStreak() < gr.getMaxWinStreak()) {
            query = "UPDATE grade SET WinMatch = ?, CurrentWinStreak = ? ,CurrentLoseStreak = 0 WHERE UserID = ?";
            PreparedStatement p = this.getConnection().prepareStatement(query);
            int value1 = gr.getWinMatch() + 1;
            int value2 = gr.getCurrentWinStreak() + 1;
            p.setInt(1, value1);
            p.setInt(2, value2);
            p.setInt(3, id);
            int rs = p.executeUpdate();
            return rs;
        } else if ((gr.getCurrentWinStreak() >= gr.getMaxWinStreak())) {
            query = "UPDATE caro.grade SET `WinMatch` = ?, `CurrentWinStreak` = ?, `MaxWinStreak` = ?, `CurrentLoseStreak` = 0 WHERE `UserId` = ?";
            PreparedStatement p = this.getConnection().prepareStatement(query);
            int value1 = gr.getWinMatch() + 1;
            int value2 = gr.getCurrentWinStreak() + 1;
            p.setInt(1, value1);
            p.setInt(2, value2);
            p.setInt(3, value2);
            p.setInt(4, id);
            int rs = p.executeUpdate();
            return rs;
        }
        return 0;
    }

    public int updateLoseMatch(int id) throws SQLException {
        Grade gr = getGrade(id);
        String query = "";
        if (gr.getCurrentLoseStreak() < gr.getMaxLoseStreak()) {
            query = "UPDATE grade SET LoseMatch = ?, CurrentLoseStreak = ? ,CurrentWinStreak = 0 WHERE UserID = ?";
            PreparedStatement p = this.getConnection().prepareStatement(query);
            int value1 = gr.getLoseMatch() + 1;
            int value2 = gr.getCurrentLoseStreak() + 1;
            p.setInt(1, value1);
            p.setInt(2, value2);
            p.setInt(3, id);
            int rs = p.executeUpdate();
            return rs;
        } else if ((gr.getCurrentLoseStreak() >= gr.getMaxLoseStreak())) {
            query = "UPDATE caro.grade SET `LoseMatch` = ?, `CurrentLoseStreak` = ?, `MaxLoseStreak` = ?, `CurrentWinStreak` = 0 WHERE `UserId` = ?";
            PreparedStatement p = this.getConnection().prepareStatement(query);
            int value1 = gr.getLoseMatch() + 1;
            int value2 = gr.getCurrentLoseStreak() + 1;
            p.setInt(1, value1);
            p.setInt(2, value2);
            p.setInt(3, value2);
            p.setInt(4, id);
            int rs = p.executeUpdate();
            return rs;
        }
        return 0;
    }

    public int updateDrawMatch(int id) throws SQLException {
        Grade gr = getGrade(id);
        String query = "UPDATE grade SET `DrawMatch` = ?, `CurrentWinStreak` = 0, `CurrentLoseStreak` = 0 WHERE `UserId` = ?";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        int value = gr.getDrawMatch() + 1;
        p.setInt(1, value);
        p.setInt(2, id);
        int rs = p.executeUpdate();
        return rs;
    }
<<<<<<< Updated upstream
    
    public ArrayList getRank() throws SQLException{
        String query = "SELECT * from grade ORDER BY Grade DESC";
=======

    public ArrayList getRank() throws SQLException {
        String query = "SELECT UserId, Grade from grade ORDER BY Grade DESC";
>>>>>>> Stashed changes
        PreparedStatement p = this.getConnection().prepareStatement(query);
        ResultSet rs = p.executeQuery();
        ArrayList<Grade> gradeList = new ArrayList<>();
        if (rs != null) {
            while (rs.next()) {
                Grade grade = new Grade();
                grade.setUserId(rs.getInt("UserID"));
                grade.setGrade(rs.getInt("Grade"));
                grade.setWinMatch(rs.getInt("WinMatch"));
                grade.setLoseMatch(rs.getInt("LoseMatch"));
                grade.setDrawMatch(rs.getInt("DrawMatch"));
                grade.setCurrentWinStreak(rs.getInt("CurrentWinStreak"));
                grade.setMaxWinStreak(rs.getInt("MaxWinStreak"));
                grade.setCurrentLoseStreak(rs.getInt("CurrentLoseStreak"));
                grade.setMaxLoseStreak(rs.getInt("MaxLoseStreak"));
                gradeList.add(grade);
            }
        }
        return gradeList;
    }



    public static void main(String[] args) throws SQLException, IOException {
        GradeDAL g = new GradeDAL();
        List list = g.getRank();
        for(int i=0;i<list.size();i++){
            Grade gr = (Grade) list.get(i);
            System.out.println(gr.getUserId());
            
        }
    }
}
