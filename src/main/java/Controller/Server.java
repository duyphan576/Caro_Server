/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author duyph
 */
public class Server {

    public static int port = 1234;
    public static ServerSocket server;
    public static int roomId;
    public static Vector<ServerThread> clientList = new Vector<>();
    

    public static void main(String[] args) throws IOException {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server binding at port " + port);
            System.out.println("Waiting for client...");
            int i =1;
            roomId = 1000;
            while(true) {
                Socket socket = server.accept();
                ServerThread client = new ServerThread(socket, Integer.toString(i++));
                clientList.add(client);
                executor.execute(client);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}