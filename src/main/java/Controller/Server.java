/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 *
 * @author duyph
 */
public class Server {

    public static int port = 1234;
    public static Socket socket;
    public static int roomId;
    public static Vector<ServerThread> clientList = new Vector<>();

    public static void main(String[] args){
        try {
            InetAddress ip;
            ip = InetAddress.getLocalHost();
            String api = "https://api-generator.retool.com/VoJlkt/data/1";
            String jsonData = "{\"ip\": \"" + ip.getHostAddress() + "\"}";
            Jsoup.connect(api).ignoreContentType(true).ignoreHttpErrors(true).header("Content-Type", "application/json")
                    .requestBody(jsonData).method(Connection.Method.PUT).execute();
            ServerSocket server = new ServerSocket(port);
            System.out.println("Server binding at port " + port);
            System.out.println("Waiting for client...");
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    10, // corePoolSize
                    100, // maximumPoolSize
                    10, // thread timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(8) // queueCapacity
            );
            int i = 1;
            roomId = 1000;
            while (true) {
                socket = server.accept();
                ServerThread client = new ServerThread(socket, Integer.toString(i++));
                clientList.add(client);
                executor.execute(client);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
