/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;
public class Room {
    private static int roomId;
    private static String password;
    private ServerThread user1,user2;

    public static int getRoomId() {
        return roomId;
    }

    public static void setRoomId(int roomId) {
        Room.roomId = roomId;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Room.password = password;
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
    
}
