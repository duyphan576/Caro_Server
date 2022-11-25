/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Controller.Server;
import Controller.ServerThread;
import DAL.UserDAL;
import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author duyph
 */
public class Room {

    private int ID;
    private ServerThread user1;
    private ServerThread user2;
    private String password;
    private UserDAL userDal;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ServerThread getUser1() {
        return user1;
    }

    public void setUser1(ServerThread user1) {
        this.user1 = user1;
    }

    public ServerThread getUser2() {
        return user2;
    }

    public void setUser2(ServerThread user2) {
        this.user2 = user2;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserDAL getUserDal() {
        return userDal;
    }

    public void setUserDal(UserDAL userDal) {
        this.userDal = userDal;
    }

    public Room(ServerThread user1) {
        System.out.println("Create new room successfully, ID is: " + Server.roomId);
        this.password = " ";
        this.ID = Server.roomId++;
        userDal = new UserDAL();
        this.user1 = user1;
        this.user2 = null;
    }

    public int getNumberOfUser(){
        return user2==null?1:2;
    }
    
    public void boardCast(byte[] message){
        try {
            user1.push(message);
            user2.push(message);
        } catch (IOException ex) {
        }
    }

    public ServerThread getCompetitor(String name){
        if(user1.getName().equals(name))
            return user2;
        return user1;
    }

    public void setUsersToPlaying() throws SQLException{
        userDal.setStatus(user1.getUser().getUserId(), 2);
        if(user2!=null){
            userDal.setStatus(user2.getUser().getUserId(), 2);
        }
    }
    
    public void setUsersToNotPlaying() throws SQLException{
        userDal.setStatus(user1.getUser().getUserId(), 1);
        if(user2!=null){
            userDal.setStatus(user2.getUser().getUserId(), 1);
        }
    }
    
    public void updateWinMatch() throws SQLException{
        userDal.updateWinMatch(user1.getUser().getUserId());
        userDal.updateWinMatch(user2.getUser().getUserId());
    }
    
    public void updateLoseMatch() throws SQLException{
        userDal.updateLoseMatch(user1.getUser().getUserId());
        userDal.updateLoseMatch(user2.getUser().getUserId());
    }
    
    public void updateDrawMatch() throws SQLException{
        userDal.updateDrawMatch(user1.getUser().getUserId());
        userDal.updateDrawMatch(user2.getUser().getUserId());
    }
    
}
