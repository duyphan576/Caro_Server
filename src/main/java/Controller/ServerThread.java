/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Crypto.ServerCryptography;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author duyph
 */
public class ServerThread implements Runnable {

    private static Socket socket;
//    private static DataInputStream in = null;
//    private static DataOutputStream out = null;
//
//    private static ServerCryptography sc = null;

    public ServerThread(Socket s) {
        this.socket = s;
    }

//    public static void start() throws IOException, NoSuchAlgorithmException {
//        System.out.println("Client " + socket.toString() + " accepted.");
//        in = new DataInputStream(new DataInputStream(socket.getInputStream()));
//        out = new DataOutputStream(new DataOutputStream(socket.getOutputStream()));
//        sc = new ServerCryptography();
//        sc.generateAsymmetricKeyPair();
//        byte[] publicKey = sc.getPublicKeyAsByteArray();
//        push(publicKey);
//    }
//
//    public static void stop() throws IOException {
//        System.out.println("Closed socket for client " + socket.toString());
//        in.close();
//        out.close();
//        socket.close();
//    }
//
//    public static void process() throws Exception {
//        while (true) {
//            int length = in.readInt();                    // read length of incoming message
//            byte[] encryptedInput = new byte[0];
//            if (length > 0) {
//                encryptedInput = new byte[length];
//                in.readFully(encryptedInput, 0, encryptedInput.length); // read the message
//            }
//            //Read from client: byte[] encryptedMsg
//            String intialMsg = sc.processInitialMsg(encryptedInput);
//
//            if (intialMsg.equals("bye")) {
//                break;
//            }
//            System.out.println("Server received: " + intialMsg);
//            byte[] encryptedOutput = sc.symmetricEncryption("Hi");
//            push(encryptedOutput);
//        }
//    }
//
//    public static void push(byte[] msg) throws IOException {
//        out.writeInt(msg.length);
//        out.write(msg);
//        out.flush();
//    }
    @Override
    public void run() {
        try {
            System.out.println("Client " + socket.toString() + " accepted.");
            DataInputStream in = new DataInputStream(new DataInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new DataOutputStream(socket.getOutputStream()));
            ServerCryptography sc = new ServerCryptography();
            sc.generateAsymmetricKeyPair();
            byte[] publicKey = sc.getPublicKeyAsByteArray();
            out.writeInt(publicKey.length);
            out.write(publicKey);
            out.flush();
            while (true) {
                int length = in.readInt();                    // read length of incoming message
                byte[] encryptedInput = new byte[0];
                if (length > 0) {
                    encryptedInput = new byte[length];
                    in.readFully(encryptedInput, 0, encryptedInput.length); // read the message
                }
                //Read from client: byte[] encryptedMsg
                String intialMsg = sc.processInitialMsg(encryptedInput);

                if (intialMsg.equals("bye")) {
                    break;
                }
                System.out.println("Server received: " + intialMsg);
                byte[] encryptedOutput = sc.symmetricEncryption("Hi");
                out.writeInt(encryptedOutput.length);
                out.write(encryptedOutput);
                out.flush();
            }
            System.out.println("Closed socket for client " + socket.toString());
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
