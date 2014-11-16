package com.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;

/**
 * Created by Jose on 05/11/2014.
 */
public class DistanceVectorClient implements Runnable{

    private AdjacentNode adjacentReference;
    private String adjacentNameGlobal;
    public Socket adjacentSocket;
    public TableStructure tableOfStructure;
    private static String valueFrom = "From";
    private static String valueType = "Type";
    private boolean converStarted = false;
    private boolean isFirstTime = true;
    private String valueName;

    public DistanceVectorClient(String adjacentName, TableStructure table){
        adjacentNameGlobal = adjacentName;
        tableOfStructure = table;
        adjacentReference = tableOfStructure.getAdjacent(adjacentName);
    }

    @Override
    public void run() {
        while(true) {
            if(null == adjacentSocket) {
                try {
                    System.out.println(adjacentReference.getIP());
                    adjacentSocket = new Socket(InetAddress.getByName(adjacentReference.getIP()), 9080);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try{
                    adjacentReference = tableOfStructure.getAdjacent(adjacentNameGlobal);
                    PrintWriter outputStream = new PrintWriter(this.adjacentSocket.getOutputStream(),true);
                    if(!converStarted){
                        BufferedReader inputSream = new BufferedReader(new InputStreamReader(this.adjacentSocket.getInputStream()));
                        startCommunication(inputSream,outputStream);
                    } else if(isFirstTime || adjacentReference.isTableHasChenge()) {
                        isFirstTime = false;
                        sendDVMessage(outputStream,false);
                    } else {
                        sendDVMessage(outputStream,true);
                    }
                    Thread.sleep( 3000 );
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
            System.out.println(">>>" + peerNameTemp);
            ReachNode nodeTemp = tableOfStructure.getRoute(peerNameTemp);
            bodyMessage = bodyMessage + peerNameTemp + ":" + nodeTemp.getCost() + "\n";
            numberOfLines += 1;
        }
        message = "From:" + tableOfStructure.routerName + "\n";
        message = message + "Type:DV" + "\n";
        message = message + "Len:" + numberOfLines + "\n";
        message = message + bodyMessage;

        return message;
    }

    public String buildKeepAlive(){
        String message = "From:" + tableOfStructure.routerName + "\n";
        message = message + "Type:KeepAlive" + "\n";
        return message;
    }

    public void startCommunication(BufferedReader inputSream,PrintWriter outputStream){
        try {
            String message = valueFrom + ":" + tableOfStructure.routerName + "\n";
            message = message + valueType + ":HELLO";
            outputStream.println(message);
            //read response from adjacent
            String inputMessage = inputSream.readLine();
            String[] tokensInputMessage = inputMessage.split(":");
            if (tokensInputMessage[0].equals(valueFrom)) {
                valueName = tokensInputMessage[1];
                System.out.println(valueName);
            }
            if (valueName.equals(adjacentReference.getName())) {
                tokensInputMessage = inputSream.readLine().split(":");
                if (tokensInputMessage[0].equals(valueType)) {
                    if (tokensInputMessage[1].equals("WELCOME")) {
                        converStarted = true;
                    }
                }

            } else {
                System.out.println("Authentication Failure expected:" + adjacentReference.getName() + " Found:" + valueName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDVMessage(PrintWriter outputStream,boolean isKeepAlive){
        if(isKeepAlive){
            String messageKeepAlive = buildKeepAlive();
            outputStream.print(messageKeepAlive);
        } else {
            String messageToSend = buildDVMessage();
            System.out.println(messageToSend);
            outputStream.print(messageToSend);
        }
    }
}
