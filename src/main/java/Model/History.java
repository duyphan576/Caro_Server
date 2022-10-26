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
    int MatchId;
    Time PlayTime;
    Date StartTime, EndTime;
    int IdPlayerWin, IdPlayerLose;

    public History() {
    }

    public History(int MatchId, Time PlayTime, Date StartTime, Date EndTime, int IdPlayerWin, int IdPlayerLose) {
        this.MatchId = MatchId;
        this.PlayTime = PlayTime;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.IdPlayerWin = IdPlayerWin;
        this.IdPlayerLose = IdPlayerLose;
    }

    public int getMatchId() {
        return MatchId;
    }

    public void setMatchId(int MatchId) {
        this.MatchId = MatchId;
    }

    public Time getPlayTime() {
        return PlayTime;
    }

    public void setPlayTime(Time PlayTime) {
        this.PlayTime = PlayTime;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date StartTime) {
        this.StartTime = StartTime;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date EndTime) {
        this.EndTime = EndTime;
    }

    public int getIdPlayerWin() {
        return IdPlayerWin;
    }

    public void setIdPlayerWin(int IdPlayerWin) {
        this.IdPlayerWin = IdPlayerWin;
    }

    public int getIdPlayerLose() {
        return IdPlayerLose;
    }

    public void setIdPlayerLose(int IdPlayerLose) {
        this.IdPlayerLose = IdPlayerLose;
    }
    
}
