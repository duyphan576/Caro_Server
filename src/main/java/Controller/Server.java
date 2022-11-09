/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author duyph
 */
public class Server {

    public static int port = 1234;
    private static ServerSocket server;

    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        try {
            server = new ServerSocket(port);
            System.out.println("Server blinding at port " + port);
            System.out.println("Waiting for client...");
            while (true) {
                Socket socket = server.accept();
                executor.execute(new ServerThread(socket));
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }

}