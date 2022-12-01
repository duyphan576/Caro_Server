/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import static Controller.Server.clientList;
import Crypto.ServerCryptography;
import DAL.UserDAL;
import Model.User;
import Model.Room;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author duyph
 */
public class ServerThread implements Runnable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private User user;
    private UserDAL userDAL;
    private ServerCryptography sc;
    private String name;
    private Room room;
    private String clientIP;
    private boolean isLogin = false;

    public ServerThread(Socket s, String n) throws IOException {
        this.socket = s;
        this.name = n;
        in = new DataInputStream(new DataInputStream(socket.getInputStream()));
        out = new DataOutputStream(new DataOutputStream(socket.getOutputStream()));
        sc = new ServerCryptography();
        userDAL = new UserDAL();
        room = null;
        if (this.socket.getInetAddress().getHostAddress().equals("127.0.0.1")) {
            clientIP = "127.0.0.1";
        } else {
            clientIP = this.socket.getInetAddress().getHostAddress();
        }
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }

    public String getClientIP() {
        return clientIP;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public ServerCryptography getSc() {
        return sc;
    }

    @Override
    public void run() {
        try {
            System.out.println("Client " + socket.toString() + " accepted");
            String encryptedMsg = null;
            sc.generateAsymmetricKeyPair();
            byte[] key = sc.getPublicKeyAsByteArray();
            push(key);
            // read length of incoming message
            int length = in.readInt();
            byte[] encryptedInput = new byte[0];
            if (length > 0) {
                encryptedInput = new byte[length];
                // read the message
                in.readFully(encryptedInput, 0, encryptedInput.length);
            }
            encryptedMsg = sc.processInitialMsg(encryptedInput);
            while (true) {
                // Servers nhận dữ liệu từ client qua stream
                // read length of incoming message
                length = in.readInt();
                encryptedInput = new byte[0];
                if (length > 0) {
                    encryptedInput = new byte[length];
                    // read the message
                    in.readFully(encryptedInput, 0, encryptedInput.length);
                }
                //Read from client: byte[] encryptedMsg
                encryptedMsg = sc.symmetricDecryption(encryptedInput);
                String[] part = encryptedMsg.split(";");
//                System.out.println(encryptedMsg);
                if (part[0].equals("Exit")) {
                    userDAL.setStatus(Integer.parseInt(part[1]), 0);
                    userStatus();
                    break;
                } else if (part[0].equals("Register")) {
                    register(part);
                } else if (part[0].equals("Rank")) {
                    rank(part);
                } else if (part[0].equals("Login")) {
                    login(part);
                } else if (part[0].equals("UserStatus")) {
                    userStatus();
                } else if (part[0].equals("CreateRoom")) {
                    createRoom(part);
                } else if (part[0].equals("Logout")) {
                    userDAL.setStatus(Integer.parseInt(part[1]), 0);
                    userStatus();
                } else if (part[0].equals("Broadcast")) {
                    broadcast(part);
                } else if (part[0].equals("ViewListRoom")) {
                    viewListRoom(part);
                } else if (part[0].equals("Caro")) {
                    byte[] msg = room.getCompetitor(this.name).sc.symmetricEncryption(encryptedMsg);
                    room.getCompetitor(this.name).push(msg);
                } else if (part[0].equals("SendLose")) {
                    byte[] msg = room.getCompetitor(this.name).sc.symmetricEncryption(encryptedMsg);
                    room.getCompetitor(this.name).push(msg);
                } else if (part[0].equals("JoinRoom")) {
                    joinRoom(part);
                } else if (part[0].equals("DrawRequest")) {
                    drawRequest();
                } else if (part[0].equals("DrawConfirm")) {
                    userDAL.updateDrawMatch(user.getUserId());
                    room.getCompetitor(this.name).userDAL.updateDrawMatch(room.getCompetitor(this.name).getUser().getUserId());
                    drawConfirm();
                } else if (part[0].equals("SurrenderRequest")) {
                    userDAL.updateWinMatch(Integer.parseInt(part[1]));
                    userDAL.updateLoseMatch(Integer.parseInt(part[2]));
                    surrenderConfirm();
                } else if (part[0].equals("WinRequest")) {
                    userDAL.updateLoseMatch(Integer.parseInt(part[1]));
                    userDAL.updateWinMatch(Integer.parseInt(part[2]));
                    winRequest();
                } else if (part[0].equals("AgainRefuse")) {
                    againRefuse();
                } else if (part[0].equals("AgainConfirm")) {
                    againConfirm1();
                } else if (part[0].equals("AgainConfirm1")) {
                    againConfirm();
                } else if (part[0].equals("QuickPlay")) {
                    quickPlay();
                } else if (part[0].equals("Chat")) {
                    chat(part);
                } else if (part[0].equals("ChangePassword")) {
                    changePassword(part);
                } else if (part[0].equals("ChangeInfo")) {
                    changeInfo(part);
                } else if (part[0].equals("GetInfo")) {
                    getInfo(part[1]);
                } else if (part[0].equals("DrawRefuse")) {
                    drawRefuse();
                }else if (part[0].equals("Lose")) {
                    lose(part[1],part[2]);
                }
            }
            System.out.println("Closed socket for client " + socket.toString());
            clientList.remove(this);
            isLogin = false;
            in.close();
            out.close();
            socket.close();
        } catch (Exception ex) {
            try {
                if (user != null) {
                    userDAL.setStatus(user.getUserId(), 0);
                    userStatus();
                }
                isLogin = false;
                clientList.remove(this);
                System.out.println("Closed socket for client " + socket.toString());
                in.close();
                out.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void push(byte[] msg) throws IOException {
        out.writeInt(msg.length);
        out.write(msg);
        out.flush();
    }

    public String convertByteToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return convertByteToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String getStringFromUser(User us) throws SQLException {
        return String.valueOf(us.getUserId()) + ";" + us.getUserName() + ";" + us.getPassword() + ";" + us.getNickname() + ";" + String.valueOf(us.getSex()) + ";" + us.getBirthday().toString()
                + ";" + String.valueOf(us.getUserId()) + ";" + String.valueOf(us.getGrade()) + ";" + String.valueOf(us.getWinMatch()) + ";" + String.valueOf(us.getLoseMatch()) + ";" + String.valueOf(us.getDrawMatch())
                + ";" + String.valueOf(us.getCurrentWinStreak()) + ";" + String.valueOf(us.getMaxWinStreak())
                + ";" + String.valueOf(us.getCurrentLoseStreak()) + ";" + String.valueOf(us.getMaxLoseStreak()) + ";" + Float.toString(userDAL.getWinRate(us.getUserId()));
    }

    public void goToOwnRoom() {
        try {
            String msg = "GoToRoom;" + room.getID() + ";" + room.getCompetitor(this.getName()).getClientIP() + ";1;"
                    + getStringFromUser(room.getCompetitor(this.getName()).getUser());
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
            msg = "GoToRoom;" + room.getID() + ";" + this.clientIP + ",0," + getStringFromUser(user);
            encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            room.getCompetitor(this.name).push(encryptedOutput);
        } catch (Exception ex) {
            System.out.println("Error");
        }

    }

    public void goToPartnerRoom() {
        try {
            String msg = "GoToRoom;" + room.getID() + ";" + room.getCompetitor(this.getName()).getClientIP() + ";0;" //getcomettitor la tra ve user trai nguoc
                    + getStringFromUser(room.getCompetitor(this.getName()).getUser());
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
            msg = "GoToRoom;" + room.getID() + ";" + this.clientIP + ";1;" + getStringFromUser(user);
            encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            room.getCompetitor(this.name).push(encryptedOutput);
        } catch (Exception ex) {
            System.out.println("Error");
        }
    }

    public void chat(String[] part) {
        try {
            String msg = "Chat;" + part[1];
            byte[] encryptedOutput = room.getCompetitor(name).sc.symmetricEncryption(msg);
            room.getCompetitor(name).push(encryptedOutput);
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    public void changePassword(String[] part) {
        try {
            User us = new User();
            if (user.getPassword().equals(getMD5(part[2]))) {
                us.setUserId(Integer.parseInt(part[1]));
                us.setPassword(getMD5(part[3]));
                userDAL.updatePassword(us);
                String msg = "ChangePassword;Success";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } else {
                String msg = "ChangePassword;Your old password is not correct";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            }
        } catch (Exception ex) {
            System.out.println("Error");
        }
    }

    public void changeInfo(String[] part) {

        try {
            User us = new User();
            us.setUserId(Integer.parseInt(part[1]));
            us.setNickname(part[2]);
            us.setSex(Integer.parseInt(part[3]));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parsed = format.parse(part[4]);
            java.sql.Date sql = new java.sql.Date(parsed.getTime());
            us.setBirthday(sql);
            userDAL.updateInfo(us);
            String msg = "ChangeInfo;Success";
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            push(encryptedOutput);
        } catch (Exception ex) {
            System.out.println("Error");
        }

    }

    public void joinRoom(String[] part) {
        String[] messageSplit = part;
        int ID_room = Integer.parseInt(messageSplit[1]);
        for (ServerThread client : Server.clientList) {
            if (client.room != null && client.room.getID() == ID_room && messageSplit.length == 2) {
                try {
                    this.room = client.getRoom();
                    client.room.setUser2(this);
                    client.userDAL.setStatus(client.user.getUserId(), 2);
                    goToPartnerRoom();
                    break;
                } catch (SQLException ex) {
                    System.out.println("Error");
                }
            } else if (client.room != null && client.room.getID() == ID_room && messageSplit.length == 3 && client.room.getPassword().equals(messageSplit[2])) {
                try {
                    this.room = client.getRoom();
                    client.room.setUser2(this);
                    client.userDAL.setStatus(client.user.getUserId(), 2);
                    goToPartnerRoom();
                } catch (SQLException ex) {
                    System.out.println("Error");
                }
            }
        }
    }

    public void viewListRoom(String[] part) {
        try {
            String msg = "ViewListRoom;";
            for (ServerThread client : Server.clientList) {
                if (client.room != null && client.room.getNumberOfUser() == 1) {
                    msg += client.room.getID() + ";" + client.room.getPassword() + ";";
                }
            }
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
        } catch (Exception ex) {
            System.out.println("Error");
        }
    }

    public void login(String[] part) {
        try {
            User us = new User();
            us.setUserName(part[1].trim());
            us.setPassword(getMD5(part[2].trim()));
            user = new User();
            user = userDAL.verifyUser(us);
            name = user.getNickname();
            String msg = "Login;" + getStringFromUser(user);
            if (user.getUserId() != 0) {
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
                userDAL.setStatus(user.getUserId(), 1);
                System.out.println("User " + name + " online");
                isLogin = true;
            } else {
                byte[] encryptedOutput = sc.symmetricEncryption("Fail");
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
            }
        } catch (Exception ex) {
            try {
                String msg = "Error;Username or Password is uncorrect.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void register(String[] part) {
        try {
            user = new User();
            user.setUserName(part[1]);
            user.setPassword(getMD5(part[2].trim()));
            user.setNickname(part[3]);
            user.setSex(Integer.parseInt(part[4]));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parsed = format.parse(part[5]);
            java.sql.Date sql = new java.sql.Date(parsed.getTime());
            user.setBirthday(sql);
            if (userDAL.addUser(user) != 0) {
                String msg = "Register;";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } else {
                byte[] encryptedOutput = sc.symmetricEncryption("Fail");
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
            }
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }

    }

    public void rank(String[] part) {
        try {
            List list = userDAL.getRank();
            String msg = "Rank";
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    User grd = (User) list.get(i);
                    msg += ";" + String.valueOf(grd.getUserId()) + ";" + String.valueOf(grd.getGrade()) + ";" + String.valueOf(grd.getWinMatch()) + ";"
                            + String.valueOf(grd.getLoseMatch()) + ";" + String.valueOf(grd.getDrawMatch())
                            + ";" + String.valueOf(grd.getCurrentWinStreak()) + ";" + String.valueOf(grd.getCurrentLoseStreak()) + ";"
                            + String.valueOf(grd.getMaxWinStreak()) + ";" + String.valueOf(grd.getMaxLoseStreak()) + ";" + Float.toString(userDAL.getWinRate(grd.getUserId()));
                }
            }
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            push(encryptedOutput);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void createRoom(String[] part) {
        room = new Room(this);
        if (part.length == 2) {
            try {
                room.setPassword(part[1]);
                String msg = "CreateRoom;" + room.getID() + ";" + part[1];
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
                System.out.println("Create new room successfully, password is " + part[1]);
            } catch (Exception ex) {
                try {
                    String msg = "Error;Something wrong, please try again.";
                    byte[] encryptedOutput = sc.symmetricEncryption(msg);
                    push(encryptedOutput);
                } catch (Exception e) {
                    System.out.println("Error");
                }
            }
        } else {
            try {
                String msg = "CreateRoom;" + room.getID();
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
                System.out.println("Create new room successfully");
            } catch (Exception ex) {
                try {
                    String msg = "Error;Something wrong, please try again.";
                    byte[] encryptedOutput = sc.symmetricEncryption(msg);
                    push(encryptedOutput);
                } catch (Exception e) {
                    System.out.println("Error");
                }
            }
        }
    }

    public void userStatus() {

        try {
            List<User> l = userDAL.findUserOnline();
            String msg = "UserStatus;" + String.valueOf(l.size());
            for (User us : l) {
                msg += ";" + us.getNickname() + ";" + String.valueOf(us.getStatus());
            }
            for (ServerThread client : Server.clientList) {
                if (isLogin) {
                    client.out.writeInt(client.sc.symmetricEncryption(msg).length);
                    client.out.write(client.sc.symmetricEncryption(msg));
                    client.out.flush();
                }
            }
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }

    }

    public void logout(String[] part) {
        try {
            String msg = "Logout;";
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            push(encryptedOutput);
            userDAL.setStatus(Integer.parseInt(part[1]), 0);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void broadcast(String[] part) {
        try {
            String msg = "Broadcast;" + part[1];
            for (ServerThread client : Server.clientList) {
                if (!name.equals(client.name)) {
                    client.out.writeInt(client.sc.symmetricEncryption(msg).length);
                    client.out.write(client.sc.symmetricEncryption(msg));
                    client.out.flush();
                }
            }
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void drawRequest() {
        try {
            String msg = "DrawRequest;";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            room.getCompetitor(this.name).push(encryptedOutput);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void againRefuse() {
        try {
            String msg = "AgainRefuse;";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
            room.getCompetitor(this.name).userDAL.setStatus(room.getCompetitor(this.name).getUser().getUserId(), 1);
            byte[] encryptedOutput1 = sc.symmetricEncryption(msg);
            push(encryptedOutput1);
            userDAL.setStatus(user.getUserId(), 1);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void againConfirm1() {
        try {
            String msg = "AgainConfirm1;";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void againConfirm() {
        try {
            String msg = "AgainConfirm;";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
            byte[] encryptedOutput1 = sc.symmetricEncryption(msg);
            push(encryptedOutput1);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void drawConfirm() {
        try {
            String msg = "DrawConfirm;";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).userDAL.setStatus(room.getCompetitor(this.name).user.getUserId(), 1);
            room.getCompetitor(this.name).push(encryptedOutput);
            msg = "DrawConfirm;";
            encryptedOutput = sc.symmetricEncryption(msg);
            userDAL.setStatus(user.getUserId(), 1);
            push(encryptedOutput);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }

    }

    public void winRequest() {
        try {
            String msg = "WinRequest;0";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
            String msg1 = "WinRequest;1";
            byte[] encryptedOutput1 = sc.symmetricEncryption(msg1);
            push(encryptedOutput1);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void surrenderConfirm() {
        try {
            String msg = "SurrenderConfirm;true";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).userDAL.setStatus(room.getCompetitor(this.name).user.getUserId(), 1);
            room.getCompetitor(this.name).push(encryptedOutput);
            msg = "SurrenderConfirm;false";
            encryptedOutput = sc.symmetricEncryption(msg);
            userDAL.setStatus(user.getUserId(), 1);
            push(encryptedOutput);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void quickPlay() {
        try {
            for (ServerThread client : Server.clientList) {
                if (client.room != null && client.room.getNumberOfUser() == 1 && client.room.getPassword().equals(" ")) {
                    client.room.setUser2(this);
                    this.room = client.room;
                    System.out.println("Entered the room " + room.getID());
                    userDAL.setStatus(this.user.getUserId(), 2);
                    room.getCompetitor(this.name).userDAL.setStatus(room.getCompetitor(this.name).user.getUserId(), 2);
                    goToPartnerRoom();
                    break;
                }
            }
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void getInfo(String a) {
        try {
            user = userDAL.getInfo(Integer.parseInt(a));
            String msg = "GetInfo;" + getStringFromUser(user);
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            push(encryptedOutput);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public void drawRefuse() {
        try {
            String msg = "DrawRefuse;";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
        } catch (Exception ex) {
            try {
                String msg = "Error;Something wrong, please try again.";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }
    public void lose(String a,String b){
        try {
            String msg = "Lose;"+a+";"+b;
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
