/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import GUI.ServerGUI;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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
    public static volatile ServerGUI ui;

    public static void main(String[] args) {
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
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            CheckTime ct = new CheckTime();
            ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(ct, 300, 1, TimeUnit.SECONDS);
            int i = 1;
            roomId = 1000;
            ui = new ServerGUI();
            ui.run();
            while (true) {
                socket = server.accept();
                ServerThread client = new ServerThread(socket, Integer.toString(i++));
                clientList.add(client);
                executor.execute(client);
                scheduledFuture.cancel(true);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

class CheckTime implements Runnable {
    @Override
    public void run() {
        System.out.println("Stop server. Time " + Instant.now());
    }
}
