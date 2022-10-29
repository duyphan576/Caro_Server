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

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    ServerCryptography sc;

    public ServerThread(Socket s) {
        this.socket = s;
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

            if (intialMsg.equals("bye")) {
                break;
            }
            System.out.println("Server received: " + intialMsg + " from client " + socket.getPort());
            byte[] encryptedOutput = sc.symmetricEncryption("Hi");
            // Write to client: byte[] encryptedOutput
            push(encryptedOutput);
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
}
