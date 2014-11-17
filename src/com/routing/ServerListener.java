package com.routing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Jose on 05/11/2014.
 */
public class ServerListener implements Runnable {

    ServerSocket stablishConection;
    TableStructure tableOfStructure;
    HashMap <String,String> valuesToSave = new HashMap<String, String>();

    public ServerListener(ServerSocket socketConection, TableStructure table)
    {
        this.tableOfStructure = table;
        this.stablishConection = socketConection;
    }

    @Override
    public void run() {
        while(true){
            try {
                Socket conectionWait = stablishConection.accept();
                if(tableOfStructure.containsAdjacent(conectionWait.getInetAddress())){
                        if(tableOfStructure.threadIsAlive.containsKey(tableOfStructure.getAdjacent(conectionWait.getInetAddress()).getName())) {
                            if (tableOfStructure.threadIsAlive.get(tableOfStructure.getAdjacent(conectionWait.getInetAddress()).getName()).equals("true")) {
                                tableOfStructure.threadIsAlive.replace(tableOfStructure.getAdjacent(conectionWait.getInetAddress()).getName(), "false");
                            }
                        }
                        Thread newThread = new Thread(new DistanceVectorServer(tableOfStructure, conectionWait));
                        newThread.start();
                } else {
                    System.out.println("Intento de conexión Router no Adyacent --> " + conectionWait.getInetAddress());
                }

            } catch (IOException e) {
                System.out.println("Conexion fallida");
            }
        }
    }
}
