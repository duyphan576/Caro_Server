/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAL;

import Model.Match;
import Model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author jukut
 */
public class MatchDAL extends DatabaseConnection{
    public MatchDAL(){
        super();
    }
    public int addMatch(Match mt) throws SQLException {
        String query = "INSERT INTO match (MatchId , RoomId , IdPlayer1 , Idplayer2) VALUES (?, ?, ?, ?)";
        PreparedStatement p = MatchDAL.getConnection().prepareStatement(query);
        p.setInt(1,mt.getMatchId());
        p.setInt(2,mt.getRoomId());
        p.setInt(3,mt.getIdPlayer1());
        p.setInt(4,mt.getIdPlayer2());
        int result = p.executeUpdate();
        return result;
    }

    public ArrayList loadMatch() throws SQLException {
        String query = "SELECT * FROM match";
        PreparedStatement p = MatchDAL.getConnection().prepareStatement(query);
        ResultSet rs = p.executeQuery();
        ArrayList<Match> matchList = new ArrayList<>();
        if (rs != null) {
            while (rs.next()) {
                Match mt = new Match();
                mt.setMatchId(rs.getInt("MatchId"));
                mt.setRoomId(rs.getInt("RoomId"));
                mt.setIdPlayer1(rs.getInt("IdPlayer1"));
                mt.setIdPlayer2(rs.getInt("Idplayer2"));
                matchList.add(mt);
            }
        }
        return matchList;
    }

    public User findUser(int id) throws SQLException {
        String query = "SELECT * FROM User WHERE UserId = ?";
        PreparedStatement p = UserDAL.getConnection().prepareStatement(query);
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
}
