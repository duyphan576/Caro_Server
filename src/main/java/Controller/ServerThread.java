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
    ServerCryptography sc;

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
            }
            System.out.println("Server received: " + intialMsg + " from client " + socket.getPort());
            login(part);
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
        if (us.getUserId()!=0) {
            byte[] encryptedOutput = sc.symmetricEncryption("Success");
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
        } else {
            byte[] encryptedOutput = sc.symmetricEncryption("Fail");
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
        }
    }
}
