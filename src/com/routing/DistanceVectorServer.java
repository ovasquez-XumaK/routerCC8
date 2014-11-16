package com.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Jose on 05/11/2014.
 */
public class DistanceVectorServer implements Runnable {
    TableStructure tableOfStructure;
    Socket stablishConection;

    public DistanceVectorServer(TableStructure table,Socket socket ){
        tableOfStructure = table;
        stablishConection = socket;
    }

    @Override
    public void run() {
        BufferedReader lectura = null;
        try{
            lectura = new BufferedReader(new InputStreamReader(this.stablishConection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            System.out.println("hola mundo");
            parseMessage(lectura);
        }
    }

    private void parseMessage(BufferedReader message) {
        boolean flagKeepAlive = false;
        String messagePart;
        String[] messagePartSplit;
        String adjacentName = null;
        int length = 0;
        try {
             messagePart = message.readLine();
            messagePartSplit = messagePart.split(":");
            if(messagePartSplit[0].equals("From")){
                adjacentName = messagePartSplit[1];
            } else {
                System.out.println("Error al recibir nombre del router");
            }
            if(tableOfStructure.containsAdjacent(adjacentName)){
                messagePart = message.readLine();
                if(messagePart.equals("Type:HELLO")){
                    sendHelloMessage();
                } else if(messagePart.equals("Type:DV")) {
                    messagePart = message.readLine();
                    messagePartSplit = messagePart.split(":");
                    if(messagePartSplit[0].equals("Len")){
                        length = Integer.parseInt(messagePartSplit[1]);
                        if(length > 0) {
                            for (int i = 0; i < length; i++) {
                                messagePart = message.readLine();
                                messagePartSplit = messagePart.split(":");
                                String tempName = messagePartSplit[0];
                                int tempCost = Integer.parseInt(messagePartSplit[1]);
                                if(tableOfStructure.containsRoute(tempName)){
                                    if(tableOfStructure.isFaster(tempName,tempCost)){
                                        tableOfStructure.setRoute(tempName,stablishConection.getInetAddress(),tempCost);
                                    }
                                } else {
                                    AdjacentNode tempAdjacent = tableOfStructure.getAdjacent(stablishConection.getInetAddress());
                                    ReachNode tempReach = new ReachNode(tempName,tempCost,tempAdjacent);
                                    tableOfStructure.addRoute(tempName,tempReach);
                                }
                            }
                        } else {
                            System.out.println("Algoritmo con Type:DV y length = 0");
                        }
                    }
                } else if(messagePart.equals("Type:KeepAlive")){
                    flagKeepAlive = true;
                } else {
                    System.out.println("Algoritmo erroneo --> " + messagePart);
                }
            } else {
                System.out.println("Falsa identificación intentando acceder "+ adjacentName);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHelloMessage(){
        String message;
        message = "From:" + tableOfStructure.routerName + "\n";
        message = message + "Type:WELCOME";
        try {
            PrintWriter escritura = new PrintWriter(this.stablishConection.getOutputStream(), true);
            escritura.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
