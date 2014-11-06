package com.routing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

/**
 * Created by Jose on 05/11/2014.
 */
public class DistanceVectorClient implements Runnable {

    private AdjacentNode adjacentReference;
    public Socket adjacentSocket;
    public TableStructure tableOfStructure;

    public DistanceVectorClient(AdjacentNode adjacent, TableStructure table){
        adjacentReference = adjacent;
        tableOfStructure = table;
    }

    @Override
    public void run() {
        while(true) {
            if(null == adjacentSocket) {
                try {
                    adjacentSocket = new Socket(InetAddress.getByName(adjacentReference.getIP()), 9080);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //Agregar validaciones para enviar DV o KeepAlive
            }
        }
    }

    public String buildDVMessage(){
        String message = "";
        String bodyMessage = "";
        int numberOfLines = 0;
        Iterator keys = tableOfStructure.getTable().keySet().iterator();
        while(keys.hasNext()){
            String peerNameTemp = keys.next().toString();
            ReachNode nodeTemp = tableOfStructure.getRoute(peerNameTemp);
            bodyMessage = bodyMessage + peerNameTemp + ":" + nodeTemp.getCost() + "\n";
            numberOfLines = 0;
        }
        message = "From:" + tableOfStructure.routerName + "\n";
        message = message + "Type:DV" + "\n";
        message = message + "Len:" + numberOfLines + "\n";
        message = message + bodyMessage;

        return bodyMessage;
    }

    public String buildKeepAlive(){
        String message = "From:" + tableOfStructure.routerName + "\n";
        message = message + "Type:KeepAlive" + "\n";
        return message;
    }
}
