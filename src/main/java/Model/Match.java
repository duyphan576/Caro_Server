/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author duyph
 */
public class Match {
    int MatchId, RoomId, IdPlayer1, IdPlayer2;

    public Match() {
    }

    public Match(int MatchId, int RoomId, int IdPlayer1, int IdPlayer2) {
        this.MatchId = MatchId;
        this.RoomId = RoomId;
        this.IdPlayer1 = IdPlayer1;
        this.IdPlayer2 = IdPlayer2;
    }

    public int getMatchId() {
        return MatchId;
    }

    public void setMatchId(int MatchId) {
        this.MatchId = MatchId;
    }

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int RoomId) {
        this.RoomId = RoomId;
    }

    public int getIdPlayer1() {
        return IdPlayer1;
    }

    public void setIdPlayer1(int IdPlayer1) {
        this.IdPlayer1 = IdPlayer1;
    }

    public int getIdPlayer2() {
        return IdPlayer2;
    }

    public void setIdPlayer2(int IdPlayer2) {
        this.IdPlayer2 = IdPlayer2;
    }
    
    
}
