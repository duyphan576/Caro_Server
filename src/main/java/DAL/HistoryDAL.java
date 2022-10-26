/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.History;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author jukut
 */
public class HistoryDAL extends DatabaseConnection{
    public HistoryDAL()
    {
        super();
    }
    public int addHistory(History ht) throws SQLException {
        String query = "INSERT INTO history (Mathchld,  PlayTime, StartTime, EndTime, IdPlayerWin, IdPlayerLose) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement p = UserDAL.getConnection().prepareStatement(query);
        p.setInt(1,ht.getMatchId());
        p.setTime(2,ht.getPlayTime());
        p.setDate(3,ht.getStartTime());
        p.setDate(4,ht.getEndTime());
        p.setInt(5,ht.getIdPlayerWin());
        p.setInt(6,ht.getIdPlayerLose());
        int result = p.executeUpdate();
        return result;
    }
    public ArrayList loadhistory() throws SQLException {
        String query = "SELECT * FROM history";
        PreparedStatement p = HistoryDAL.getConnection().prepareStatement(query);
        ResultSet rs = p.executeQuery();
        ArrayList<History> historyList = new ArrayList<>();
        if (rs != null) {
            while (rs.next()) {
                History ht = new History();
                ht.setMatchId(rs.getInt("MathId"));
                ht.setPlayTime(rs.getTime("PlayTime"));
                ht.setStartTime(rs.getDate("StartTime"));
                ht.setEndTime(rs.getDate("EndTime"));
                ht.setIdPlayerWin(rs.getInt("IdPlayerWin"));
                ht.setIdPlayerLose(rs.getInt("IdPlayerLose"));
                historyList.add(ht);
            }
        }
        return historyList;
    }

    public History findHistoryMathId(int id) throws SQLException {
        String query = "SELECT * FROM history WHERE MatchId = ?";
        PreparedStatement p = HistoryDAL.getConnection().prepareStatement(query);
        p.setInt(1, id);
        ResultSet rs = p.executeQuery();
        History ht = new History();
        if (rs != null) {
            while (rs.next()) {
                ht.setMatchId(rs.getInt("MathId"));
                ht.setPlayTime(rs.getTime("PlayTime"));
                ht.setStartTime(rs.getDate("StartTime"));
                ht.setEndTime(rs.getDate("EndTime"));
                ht.setIdPlayerWin(rs.getInt("IdPlayerWin"));
                ht.setIdPlayerLose(rs.getInt("IdPlayerLose"));
            }
        }
        return ht;
    }
    
    public History findHistoryIdPlayer(int id) throws SQLException {
        String query = "SELECT * FROM history WHERE IdPlayerWin = ? OR IdPlayerLose = ?";
        PreparedStatement p = HistoryDAL.getConnection().prepareStatement(query);
        p.setInt(1, id);
        p.setInt(2, id);
        ResultSet rs = p.executeQuery();
        History ht = new History();
        if (rs != null) {
            while (rs.next()) {
                ht.setMatchId(rs.getInt("MathId"));
                ht.setPlayTime(rs.getTime("PlayTime"));
                ht.setStartTime(rs.getDate("StartTime"));
                ht.setEndTime(rs.getDate("EndTime"));
                ht.setIdPlayerWin(rs.getInt("IdPlayerWin"));
                ht.setIdPlayerLose(rs.getInt("IdPlayerLose"));
            }
        }
        return ht;
    }
}
