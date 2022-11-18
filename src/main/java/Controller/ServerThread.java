/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

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
    private boolean isClosed;
    private Room room;
    private String clientIP;

    public ServerThread(Socket s, String n) throws IOException {
        this.socket = s;
        this.name = n;
        in = new DataInputStream(new DataInputStream(socket.getInputStream()));
        out = new DataOutputStream(new DataOutputStream(socket.getOutputStream()));
        sc = new ServerCryptography();
        userDAL = new UserDAL();
        isClosed = false;
        room = null;
        //Trường hợp test máy ở server sẽ lỗi do hostaddress là localhost
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
            System.out.println(encryptedMsg);
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
                System.out.println(encryptedMsg);
                String[] part = encryptedMsg.split(";");
                if (part[0].equals("Exit")) {
                    userDAL.setOnlOff(Integer.parseInt(part[1]), 0);
                    break;
                } else if (part[0].equals("Register")) {
                    register(part);
                } else if (part[0].equals("Rank")) {
                    rank(part);
                } else if (part[0].equals("Login")) {
                    login(part);
                } else if (part[0].equals("userStatus")) {
                    userStatus(part);
                } else if (part[0].equals("createRoom")) {
                    createRoom(part);
                } else if (part[0].equals("Logout")) {
                    userDAL.setOnlOff(Integer.parseInt(part[1]), 0);
                } else if (part[0].equals("Broadcast")) {
                    broadcast(part);
                } else if (part[0].equals("viewListRoom")) {
                    viewListRoom(part);
                } else if (part[0].equals("caro")) {
                    byte[] msg = room.getCompetitor(this.name).sc.symmetricEncryption(encryptedMsg);
                    room.getCompetitor(this.name).push(msg);
                } else if (part[0].equals("send-lose")) {
                    byte[] msg = room.getCompetitor(this.name).sc.symmetricEncryption(encryptedMsg);
                    room.getCompetitor(this.name).push(msg);
                }else if (part[0].equals("joinRoom")) {
                    joinRoom(part);
                } else if (part[0].equals("draw-request")) {
                    drawrequest();
                } else if (part[0].equals("draw-confirm")) {
                    userDAL.updateDrawMatch(user.getUserId());
                    room.getCompetitor(this.name).userDAL.updateDrawMatch(room.getCompetitor(this.name).getUser().getUserId());
                    drawconfigfishned();
                } else if (part[0].equals("lose-request")) {
                    userDAL.updateWin(Integer.parseInt(part[1]));
                    userDAL.updateLose(Integer.parseInt(part[2]));
                    loseconfigfishned();
                }else if (part[0].equals("win-request")) {
                    userDAL.updateLose(Integer.parseInt(part[1]));
                    userDAL.updateWin(Integer.parseInt(part[2]));
                    winrequest();
                }else if (part[0].equals("again-refuse")) {
                    againrefuse();
                } else if (part[0].equals("again-confirm")) {
                    againconfirm1();
                } else if (part[0].equals("again-confirm-1")) {
                    againconfirm();
                } else if (part[0].equals("quick-room")) {
                    quickroom();
                }
            }
            System.out.println("Closed socket for client " + socket.toString());
            in.close();
            out.close();
            socket.close();
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void push(byte[] msg) throws IOException {
        out.writeInt(msg.length);
        out.write(msg);
        out.flush();
    }

    public void joinRoom(String[] part) {
        String[] messageSplit = part;
        int ID_room = Integer.parseInt(messageSplit[1]);
        for (ServerThread client : Server.clientList) {
            if (client.room != null && client.room.getID() == ID_room && messageSplit.length==2) {
                this.room = client.getRoom();
                client.room.setUser2(this);
                System.out.println("Đã vào phòng " + room.getID());
                goToPartnerRoom();
                break;
            }else if (client.room != null && client.room.getID() == ID_room && messageSplit.length==3 && client.room.getPassword().equals(messageSplit[2])){
                this.room = client.getRoom();
                client.room.setUser2(this);
                System.out.println("Đã vào phòng " + room.getID());
                goToPartnerRoom();
            }
        }
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

    public void playNow(String[] part) {
        boolean isFinded = false;
        for (ServerThread client : Server.clientList) {
            if (client.room != null && client.room.getNumberOfUser() == 1 && client.room.getPassword().equals(" ")) {
                try {
                    client.room.setUser2(this);
                    this.room = client.room;
                    System.out.println("Đã vào phòng " + room.getID());
                    goToPartnerRoom();
                    userDAL.setOnlOff(this.user.getUserId(), 2);
                    isFinded = true;
                    //Xử lý phần mời cả 2 người chơi vào phòng
                    break;
                } catch (SQLException ex) {
                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void viewListRoom(String[] part) {
        try {
            String msg = "viewListRoomSuccess;";
            for (ServerThread client : Server.clientList) {
                if (client.room != null && client.room.getNumberOfUser() == 1) {
                    msg += client.room.getID() + ";" + client.room.getPassword() + ";";
                }
            }
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
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
            String msg = "loginSuccess;" + getStringFromUser(user);
            if (user.getUserId() != 0) {
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
                userDAL.setOnlOff(user.getUserId(), 1);
                System.out.println("User " + name + " online");
            } else {
                byte[] encryptedOutput = sc.symmetricEncryption("Fail");
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
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
                String msg = "registerSuccess;";
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
                System.out.println("Add user");
            } else {
                byte[] encryptedOutput = sc.symmetricEncryption("Fail");
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void rank(String[] part) {
        try {
            List list = userDAL.getRank();
            String msg = "rankSuccess";
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
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createRoom(String[] part) {
        room = new Room(this);
        if (part.length == 2) {
            try {
                room.setPassword(part[1]);
                String msg = "createRoomSuccess;" + room.getID() + ";" + part[1];
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
                System.out.println("Create new room successfully, password is " + part[1]);
            } catch (Exception ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                String msg = "createRoomSuccess;" + room.getID();
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                // Write to client: byte[] encryptedOutput
                push(encryptedOutput);
                System.out.println("Create new room successfully");
            } catch (Exception ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void userStatus(String[] part) {
        try {
            List<User> l = userDAL.findUserOnline();
            String msg = "userStatusSuccess;" + String.valueOf(l.size());
            for (User us : l) {
                msg += ";" + us.getNickname() + ";" + String.valueOf(us.getStatus());
            }
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            push(encryptedOutput);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logout(String[] part) {
        try {
            String msg = "logoutSuccess;";
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            push(encryptedOutput);
            userDAL.setOnlOff(Integer.parseInt(part[1]), 0);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void broadcast(String[] part) {
        try {
            String msg = "broadcastSuccess;" + part[1];
            for (ServerThread client : Server.clientList) {
                if (!name.equals(client.name)) {
                    client.out.writeInt(client.sc.symmetricEncryption(msg).length);
                    client.out.write(client.sc.symmetricEncryption(msg));
                    client.out.flush();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
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
            String msg = "goToRoom;" + room.getID() + ";" + room.getCompetitor(this.getName()).getClientIP() + ";1;"
                    + getStringFromUser(room.getCompetitor(this.getName()).getUser());
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
            msg = "goToRoom;" + room.getID() + ";" + this.clientIP + ",0," + getStringFromUser(user);
            encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            room.getCompetitor(this.name).push(encryptedOutput);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void goToPartnerRoom() {
        try {
            String msg = "goToRoom;" + room.getID() + ";" + room.getCompetitor(this.getName()).getClientIP() + ";0;" //getcomettitor la tra ve user trai nguoc
                    + getStringFromUser(room.getCompetitor(this.getName()).getUser());
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
            msg = "goToRoom;" + room.getID() + ";" + this.clientIP + ";1;" + getStringFromUser(user);
            encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            room.getCompetitor(this.name).push(encryptedOutput);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void drawrequest() throws Exception {
        String msg = "draw-request;";
        byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
        // Write to client: byte[] encryptedOutput
        room.getCompetitor(this.name).push(encryptedOutput);
    }
    public void againrefuse() throws Exception {
        String msg = "again-refuse;";
        byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
        room.getCompetitor(this.name).push(encryptedOutput);
        byte[] encryptedOutput1 = sc.symmetricEncryption(msg);
        push(encryptedOutput1);
    }
    public void againconfirm1() throws Exception {
        String msg = "again-confirm-1;";
        byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
        room.getCompetitor(this.name).push(encryptedOutput);
    }
    public void againconfirm() throws Exception {
        String msg = "again-confirm;";
        byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
        room.getCompetitor(this.name).push(encryptedOutput);
         byte[] encryptedOutput1 = sc.symmetricEncryption(msg);
        push(encryptedOutput1);
    }

    public void drawconfigfishned() {
        try {
            String msg = "draw-confirm-fishned";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
            String msg1 = "draw-confirm-fishned";
            byte[] encryptedOutput1 = sc.symmetricEncryption(msg1);
            push(encryptedOutput1);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void winrequest(){
        try {
            String msg = "win-request;0";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
            String msg1 = "win-request;1";
            byte[] encryptedOutput1 = sc.symmetricEncryption(msg1);
            push(encryptedOutput1);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void loseconfigfishned() {
        try {
            String msg = "lose-confirm-fishned";
            byte[] encryptedOutput = room.getCompetitor(this.name).sc.symmetricEncryption(msg);
            room.getCompetitor(this.name).push(encryptedOutput);
            String msg1 = "lose-confirm-fishned";
            byte[] encryptedOutput1 = sc.symmetricEncryption(msg1);
            push(encryptedOutput1);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void quickroom(){
        try {
            for (ServerThread client : Server.clientList) {
                if (client.room!= null && client.room.getNumberOfUser() == 1 && client.room.getPassword().equals(" ")) {
                            client.room.setUser2(this);
                            this.room = client.room;
                            System.out.println("Đã vào phòng " + room.getID());
                            goToPartnerRoom();
                            break;
                        }
            }
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
