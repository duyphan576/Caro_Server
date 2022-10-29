package DAL;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import Model.Match;
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
        this.connectDB();
    }
    public int addMatch(Match mt) throws SQLException {
        String query = "INSERT INTO matches (MatchId , RoomId , IdPlayer1 , IdPlayer2) VALUES (?, ?, ?, ?)";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        p.setInt(1,mt.getMatchId());
        p.setInt(2,mt.getRoomId());
        p.setInt(3,mt.getIdPlayer1());
        p.setInt(4,mt.getIdPlayer2());
        int result = p.executeUpdate();
        return result;
    }

    public ArrayList loadMatch() throws SQLException {
        String query = "SELECT * FROM matches";
        PreparedStatement p = this.getConnection().prepareStatement(query);
        ResultSet rs = p.executeQuery();
        ArrayList<Match> matchList = new ArrayList<>();
        if (rs != null) {
            while (rs.next()) {
                Match mt = new Match();
                mt.setMatchId(rs.getInt("MatchId"));
                mt.setRoomId(rs.getInt("RoomId"));
                mt.setIdPlayer1(rs.getInt("IdPlayer1"));
                mt.setIdPlayer2(rs.getInt("IdPlayer2"));
                matchList.add(mt);
            }
        }
        return matchList;
    }
}
