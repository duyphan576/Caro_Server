/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import DAL.GradeDAL;
import DAL.HistoryDAL;
import DAL.MatchDAL;
import Model.User;
import DAL.UserDAL;
import Model.Grade;
import Model.History;
import Model.Match;
import Model.Room;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

/**
 *
 * @author duyph
 */
public class ServerThread implements Runnable {

    private User user;
    private Grade grade;
    private Match match;
    private History history;
    private UserDAL userDal;
    private GradeDAL gradeDal;
    private MatchDAL matchDal;
    private HistoryDAL historyDal;
    private Room room;
    private Socket socketOfServer;
    private int clientNumber;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;
    private String clientIP;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public UserDAL getUserDal() {
        return userDal;
    }

    public void setUserDal(UserDAL userDal) {
        this.userDal = userDal;
    }

    public GradeDAL getGradeDal() {
        return gradeDal;
    }

    public void setGradeDal(GradeDAL gradeDal) {
        this.gradeDal = gradeDal;
    }

    public MatchDAL getMatchDal() {
        return matchDal;
    }

    public void setMatchDal(MatchDAL matchDal) {
        this.matchDal = matchDal;
    }

    public HistoryDAL getHistoryDal() {
        return historyDal;
    }

    public void setHistoryDal(HistoryDAL historyDal) {
        this.historyDal = historyDal;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Socket getSocketOfServer() {
        return socketOfServer;
    }

    public void setSocketOfServer(Socket socketOfServer) {
        this.socketOfServer = socketOfServer;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public BufferedReader getIs() {
        return is;
    }

    public void setIs(BufferedReader is) {
        this.is = is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public void setOs(BufferedWriter os) {
        this.os = os;
    }

    public boolean isIsClosed() {
        return isClosed;
    }

    public void setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        userDal = new UserDAL();
        isClosed = false;

        //Trường hợp test máy ở server sẽ lỗi do hostaddress là localhost
        if (this.socketOfServer.getInetAddress().getHostAddress().equals("127.0.0.1")) {
            clientIP = "127.0.0.1";
        } else {
            clientIP = this.socketOfServer.getInetAddress().getHostAddress();
        }
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
