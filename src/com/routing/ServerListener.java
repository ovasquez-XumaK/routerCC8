package com.routing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jose on 05/11/2014.
 */
public class ServerListener implements Runnable {

    ServerSocket stablishConection;
    TableStructure tableOfStructure;

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
                    DistanceVectorServer newConection = new DistanceVectorServer(tableOfStructure,conectionWait);
                    newConection.run();
                } else {
                    System.out.println("Intento de conexión Router no Adyacent --> " + conectionWait.getInetAddress());
                }

            } catch (IOException e) {
                System.out.println("Conexion fallida");
            }
        }
    }
}
