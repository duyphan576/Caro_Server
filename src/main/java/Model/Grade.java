/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author duyph
 */
public class Grade {
    int UserId;
    int Grade;
    int WinMatch;
    int LoseMatch;
    int DrawMatch;
    int CurrentWinStreak;
    int MaxWinStreak;
    int CurrentLoseStreak;
    int MaxLoseStreak;

    public Grade() {
    }

    public Grade(int UserId, int Grade, int WinMatch, int LoseMatch, int DrawMatch, int CurrentWinStreak, int MaxWinStreak, int CurrentLoseStreak, int MaxLoseStreak) {
        this.UserId = UserId;
        this.Grade = Grade;
        this.WinMatch = WinMatch;
        this.LoseMatch = LoseMatch;
        this.DrawMatch = DrawMatch;
        this.CurrentWinStreak = CurrentWinStreak;
        this.MaxWinStreak = MaxWinStreak;
        this.CurrentLoseStreak = CurrentLoseStreak;
        this.MaxLoseStreak = MaxLoseStreak;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

    public int getGrade() {
        return Grade;
    }

    public void setGrade(int Grade) {
        this.Grade = Grade;
    }

    public int getWinMatch() {
        return WinMatch;
    }

    public void setWinMatch(int WinMatch) {
        this.WinMatch = WinMatch;
    }

    public int getLoseMatch() {
        return LoseMatch;
    }

    public void setLoseMatch(int LoseMatch) {
        this.LoseMatch = LoseMatch;
    }

    public int getDrawMatch() {
        return DrawMatch;
    }

    public void setDrawMatch(int DrawMatch) {
        this.DrawMatch = DrawMatch;
    }

    public int getCurrentWinStreak() {
        return CurrentWinStreak;
    }

    public void setCurrentWinStreak(int CurrentWinStreak) {
        this.CurrentWinStreak = CurrentWinStreak;
    }

    public int getMaxWinStreak() {
        return MaxWinStreak;
    }

    public void setMaxWinStreak(int MaxWinStreak) {
        this.MaxWinStreak = MaxWinStreak;
    }

    public int getCurrentLoseStreak() {
        return CurrentLoseStreak;
    }

    public void setCurrentLoseStreak(int CurrentLoseStreak) {
        this.CurrentLoseStreak = CurrentLoseStreak;
    }

    public int getMaxLoseStreak() {
        return MaxLoseStreak;
    }

    public void setMaxLoseStreak(int MaxLoseStreak) {
        this.MaxLoseStreak = MaxLoseStreak;
    }
    
    
}
