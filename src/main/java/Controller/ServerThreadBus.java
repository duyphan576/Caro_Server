/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author duyph
 */
public class ServerThreadBus {
    private List<ServerThread> listServerThreads;

    public List<ServerThread> getListServerThreads() {
        return listServerThreads;
    }

    public ServerThreadBus() {
        listServerThreads = new ArrayList<>();
    }

    public void add(ServerThread serverThread){
        listServerThreads.add(serverThread);
    }
    
    public void mutilCastSend(byte[] message){ //like sockets.emit in socket.io
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            try {
                serverThread.push(message);
            } catch (IOException ex) {
            }
        }
    }
    
    public void boardCast(String id, String message){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if (serverThread.getName().equals(id)) {
                continue;
            } else {
                try {
                    byte[] msg = serverThread.getSc().symmetricEncryption(message);
                    serverThread.push(msg);
                } catch (Exception ex) {
                    Logger.getLogger(ServerThreadBus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    public int getLength(){
        return listServerThreads.size();
    }
    
    public void sendMessageToUserID(int id, String message){
        for(ServerThread serverThread : Server.serverThreadBus.getListServerThreads()){
            if(serverThread.getUser().getUserId()==id){
                try {
                    byte[] msg = serverThread.getSc().symmetricEncryption(message);
                    serverThread.push(msg);
                    break;
                } catch (Exception ex) {
                    Logger.getLogger(ServerThreadBus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public ServerThread getServerThreadByUserID(int ID){
        for(int i=0; i<Server.serverThreadBus.getLength(); i++){
            if(Server.serverThreadBus.getListServerThreads().get(i).getUser().getUserId()==ID){
                return Server.serverThreadBus.listServerThreads.get(i);
            }
        }
        return null;
    }
    
    public void remove(String id){
        for(int i=0; i<Server.serverThreadBus.getLength(); i++){
            if(Server.serverThreadBus.getListServerThreads().get(i).getName().equals(id)){
                Server.serverThreadBus.listServerThreads.remove(i);
            }
        }
    }
}
