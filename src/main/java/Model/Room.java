/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

import Controller.ServerThread;
import DAL.UserDAL;

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
//        System.out.println("Tạo phòng thành công, ID là: " + Server.ID_room);
        this.password = " ";
//        this.ID = Server.ID_room++;
        userDal = new UserDAL();
        this.user1 = user1;
        this.user2 = null;
    }

    public int getNumberOfUser() {
        return user2 == null ? 1 : 2;
    }


}
