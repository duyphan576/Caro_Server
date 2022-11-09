/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Crypto.ServerCryptography;
import DAL.GradeDAL;
import DAL.UserDAL;
import Model.Grade;
import Model.User;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
<<<<<<< Updated upstream
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
=======
import java.text.ParseException;
import java.text.SimpleDateFormat;
>>>>>>> Stashed changes
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
    private Grade grade;
    private GradeDAL gradeDAL;
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

    public void start() throws IOException, NoSuchAlgorithmException {
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
            String intialMsg = sc.processInitialMsg(encryptedInput);
            String[] part = intialMsg.split(";");
            if (intialMsg.equals("bye")) {
                break;
            } else if (part[0].equals("Register")) {
<<<<<<< Updated upstream
                user = new User();
                UserDAL userdal = new UserDAL();
                user.setUserName(part[1]);
                user.setPassword(part[2]);
                user.setNickname(part[3]);
                user.setSex(Integer.parseInt(part[4]));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date parsed = format.parse(part[5]);
                java.sql.Date sql = new java.sql.Date(parsed.getTime());
                user.setBirthday(sql);
                userdal.addUser(user);
            } else if (part[0].equals("Rank")) {
                GradeDAL gradeDAL = new GradeDAL();
                List list = gradeDAL.getRank();
                String msg = "Rank";
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        Grade grd = (Grade) list.get(i);
                        msg += ";" + String.valueOf(grd.getUserId()) + ";" + String.valueOf(grd.getGrade())+ ";" + String.valueOf(grd.getWinMatch())+ ";" 
                                + String.valueOf(grd.getLoseMatch())+ ";" + String.valueOf(grd.getDrawMatch())
                                + ";" + String.valueOf(grd.getCurrentWinStreak())+ ";" + String.valueOf(grd.getCurrentLoseStreak())+ ";" 
                                + String.valueOf(grd.getMaxWinStreak())+ ";" +String.valueOf(grd.getMaxLoseStreak())+";"+Float.toString(gradeDAL.getWinRate(grd.getUserId()));
                    }
                }
                byte[] encryptedOutput = sc.symmetricEncryption(msg);
                push(encryptedOutput);
=======
                register(part);
            } else if (part[0].equals("Login")) {
                login(part);
>>>>>>> Stashed changes
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
        Grade gr = gradeDAL.getGrade(us.getUserId());
        String msg = "Success;" + String.valueOf(us.getUserId()) + ";" + us.getUserName() + ";" + us.getPassword() + ";" + us.getNickname() + ";" + String.valueOf(us.getSex()) + ";" + us.getBirthday().toString()
                + ";" + String.valueOf(gr.getUserId()) + ";" + String.valueOf(gr.getGrade()) + ";" + String.valueOf(gr.getWinMatch()) + ";" + String.valueOf(gr.getLoseMatch()) + ";" + String.valueOf(gr.getDrawMatch())
                + ";" + String.valueOf(gr.getCurrentWinStreak()) + ";" + String.valueOf(gr.getMaxWinStreak())
                + ";" + String.valueOf(gr.getCurrentLoseStreak()) + ";" + String.valueOf(gr.getMaxLoseStreak()) + ";" + Float.toString(gradeDAL.getWinRate(gr.getUserId()));
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

<<<<<<< Updated upstream
    public static byte[] object2Byte(Object obj) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream dout = null;
        try {
            dout = new ObjectOutputStream(bout);
            dout.writeObject(obj);
            dout.close();
        } catch (IOException e) {
            System.out.println("Unable to object to bytes");
        }
        return bout.toByteArray();
    }

    public static void main(String[] args) throws SQLException {
        GradeDAL g = new GradeDAL();
        List list = g.getRank();
        for (int i = 0; i < list.size(); i++) {
            Grade grd = (Grade) list.get(i);
            byte[] ob = object2Byte(grd);
            System.out.println(ob.toString());
        }
=======
    public void register(String[] part) throws SQLException, ParseException {
        user = new User();
        user.setUserName(part[1]);
        user.setPassword(part[2]);
        user.setNickname(part[3]);
        user.setSex(Integer.parseInt(part[4]));
        Date date = (Date) new SimpleDateFormat("dd/MM/YYY").parse(part[5]);
        user.setBirthday(date);
        userDAL.addUser(user);
>>>>>>> Stashed changes
    }
}
