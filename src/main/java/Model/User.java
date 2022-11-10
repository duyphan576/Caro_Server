/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import java.sql.Date;

/**
 *
 * @author duyph
 */
public class User {

    int userId, sex, isBlocked, Grade;
    String userName, password, nickname;
    Date Birthday;
    int WinMatch;
    int LoseMatch;
    int DrawMatch;
    int CurrentWinStreak;
    int MaxWinStreak;
    int CurrentLoseStreak;
    int MaxLoseStreak;
    int Status;
    public User() {
    }

    public User(int userId, int sex, int isBlocked, int Status, String userName, String password, String nickname, Date Birthday,int Grade, int WinMatch, int LoseMatch, int DrawMatch, int CurrentWinStreak, int MaxWinStreak, int CurrentLoseStreak, int MaxLoseStreak) {
        this.userId = userId;
        this.sex = sex;
        this.isBlocked = isBlocked;
        this.userName = userName;
        this.password = password;
        this.nickname = nickname;
        this.Birthday = Birthday;
        this.WinMatch = WinMatch;
        this.LoseMatch = LoseMatch;
        this.DrawMatch = DrawMatch;
        this.CurrentWinStreak = CurrentWinStreak;
        this.MaxWinStreak = MaxWinStreak;
        this.CurrentLoseStreak = CurrentLoseStreak;
        this.MaxLoseStreak = MaxLoseStreak;
        this.Grade=Grade;
        this.Status=Status;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public Date getBirthday() {
        return Birthday;
    }

    public void setBirthday(Date Birthday) {
        this.Birthday = Birthday;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(int isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public int getGrade() {
        return Grade;
    }

    public void setGrade(int Grade) {
        this.Grade = Grade;
    }
    
}
