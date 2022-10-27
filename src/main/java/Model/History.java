/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author duyph
 */
public class History {

    int matchId;
    int playTime;
    Date startTime, endTime;
    int idPlayerWin, idPlayerLose;

    public History() {
    }

    public History(int matchId, int playTime, Date startTime, Date endTime, int idPlayerWin, int idPlayerLose) {
        this.matchId = matchId;
        this.playTime = playTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.idPlayerWin = idPlayerWin;
        this.idPlayerLose = idPlayerLose;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getIdPlayerWin() {
        return idPlayerWin;
    }

    public void setIdPlayerWin(int idPlayerWin) {
        this.idPlayerWin = idPlayerWin;
    }

    public int getIdPlayerLose() {
        return idPlayerLose;
    }

    public void setIdPlayerLose(int idPlayerLose) {
        this.idPlayerLose = idPlayerLose;
    }
}
