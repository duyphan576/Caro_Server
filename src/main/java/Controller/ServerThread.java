/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Crypto.ServerCryptography;
import DAL.UserDAL;
import Model.User;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.SQLException;
import java.text.ParseException;
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

    public ServerThread(Socket s) {
        this.socket = s;
    }

    @Override
    public void run() {
        try {
            start();
            process();
            stop();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void start() throws IOException, NoSuchAlgorithmException, Exception {
        System.out.println("Client " + socket.toString() + " accepted.");
        in = new DataInputStream(new DataInputStream(socket.getInputStream()));
        out = new DataOutputStream(new DataOutputStream(socket.getOutputStream()));
        sc = new ServerCryptography();
        // Generate publickey
        sc.generateAsymmetricKeyPair();
        byte[] publicKey = sc.getPublicKeyAsByteArray();
        // Write to client: byte[] publicKey
        out.writeInt(publicKey.length);
        out.write(publicKey);
        out.flush();
        int length = in.readInt();
        byte[] encryptedInput = new byte[0];
        if (length > 0) {
            encryptedInput = new byte[length];
            // read the message
            in.readFully(encryptedInput, 0, encryptedInput.length);
        }
        String input = sc.processInitialMsg(encryptedInput);
    }

    public void process() throws IOException, Exception {
        while (true) {
            // Servers nhận dữ liệu từ client qua stream
            // read length of incoming message
            int length = in.readInt();
            byte[] encryptedInput = new byte[0];
            if (length > 0) {
                encryptedInput = new byte[length];
                // read the message
                in.readFully(encryptedInput, 0, encryptedInput.length);
            }
            //Read from client: byte[] encryptedMsg
            String intialMsg = sc.symmetricDecryption(encryptedInput);
            String[] part = intialMsg.split(";");
            if (intialMsg.equals("bye")) {
                break;
            } else if (part[0].equals("Register")) {
                register(part);
            } else if (part[0].equals("Rank")) {
                UserDAL gradeDAL = new UserDAL();
                List list = gradeDAL.getRank();
                String msg = "Rank";
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        User grd = (User) list.get(i);
                        msg += ";" + String.valueOf(grd.getUserId()) + ";" + String.valueOf(grd.getGrade()) + ";" + String.valueOf(grd.getWinMatch()) + ";"
                                + String.valueOf(grd.getLoseMatch()) + ";" + String.valueOf(grd.getDrawMatch())
                                + ";" + String.valueOf(grd.getCurrentWinStreak()) + ";" + String.valueOf(grd.getCurrentLoseStreak()) + ";"
                                + String.valueOf(grd.getMaxWinStreak()) + ";" + String.valueOf(grd.getMaxLoseStreak()) + ";" + Float.toString(gradeDAL.getWinRate(grd.getUserId()));
                    }
                }
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
            } else if (part[0].equals("Login")) {
                login(part);
            } else if (part[0].equals("FriendOnl")) {
                List<User> l = userDAL.findUserOnl();
                String tempp = String.valueOf(l.size());
                for (User use : l) {
                    tempp += ";" + use.getNickname() + ";" + String.valueOf(use.getStatus());
                }
                byte[] encryptedOutput = sc.symmetricEncryption(tempp);
                push(encryptedOutput);
            }
            System.out.println("Server received: " + intialMsg + " from client " + socket.getPort());

        }
    }

    public void stop() throws IOException {
        // Close socket
        System.out.println("Closed socket for client " + socket.toString());
        in.close();
        out.close();
        socket.close();
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

    public void login(String[] part) throws IOException, Exception {
        user = new User();
        user.setUserName(part[1].trim());
        user.setPassword(getMD5(part[2].trim()));
        userDAL = new UserDAL();
        User us = userDAL.verifyUser(user);
        name = us.getNickname();
        User gr = userDAL.getGrade(us.getUserId());
        String msg = "Success;" + String.valueOf(us.getUserId()) + ";" + us.getUserName() + ";" + us.getPassword() + ";" + us.getNickname() + ";" + String.valueOf(us.getSex()) + ";" + us.getBirthday().toString()
                + ";" + String.valueOf(gr.getUserId()) + ";" + String.valueOf(gr.getGrade()) + ";" + String.valueOf(gr.getWinMatch()) + ";" + String.valueOf(gr.getLoseMatch()) + ";" + String.valueOf(gr.getDrawMatch())
                + ";" + String.valueOf(gr.getCurrentWinStreak()) + ";" + String.valueOf(gr.getMaxWinStreak())
                + ";" + String.valueOf(gr.getCurrentLoseStreak()) + ";" + String.valueOf(gr.getMaxLoseStreak()) + ";" + Float.toString(userDAL.getWinRate(gr.getUserId()));
        if (us.getUserId() != 0) {
            byte[] encryptedOutput = sc.symmetricEncryption(msg);
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
        } else {
            byte[] encryptedOutput = sc.symmetricEncryption("Fail");
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
        }
    }

    public void register(String[] part) throws SQLException, ParseException {
        user = new User();
        UserDAL userdal = new UserDAL();
        user.setUserName(part[1]);
        user.setPassword(getMD5(part[2].trim()));
        user.setNickname(part[3]);
        user.setSex(Integer.parseInt(part[4]));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsed = format.parse(part[5]);
        java.sql.Date sql = new java.sql.Date(parsed.getTime());
        user.setBirthday(sql);
        userdal.addUser(user);
    }
}
