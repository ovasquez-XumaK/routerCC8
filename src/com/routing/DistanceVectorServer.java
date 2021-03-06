package com.routing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jose on 05/11/2014.
 */
public class DistanceVectorServer implements Runnable {
    TableStructure tableOfStructure;
    ServerSocket reconect;
    Socket stablishConection;
    public static InetAddress address;

    public DistanceVectorServer(TableStructure table,Socket socket ){
        tableOfStructure = table;
        stablishConection = socket;
        address = stablishConection.getInetAddress();
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
            if((!stablishConection.isClosed()) && (null != stablishConection)) {
                parseMessage(lectura);
            } else {
                try {
                    stablishConection = reconect.accept();
                    if(stablishConection.getInetAddress() != address){
                        stablishConection = null;
                    }
                } catch (IOException e) {
                    stablishConection = null;
                }
            }
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
            System.out.println("******ServerReceive*******");
            System.out.println(messagePart);
            messagePartSplit = messagePart.split(":");
            if(messagePartSplit[0].equals("From")){
                adjacentName = messagePartSplit[1];
            } else {
                System.out.println("Error al recibir nombre del router");
            }
            if(tableOfStructure.containsAdjacent(adjacentName)){
                messagePart = message.readLine();
                System.out.println(messagePart);
                if(messagePart.equals("Type:HELLO")){
                    System.out.println("********ServerEnd*********");
                    sendHelloMessage();
                } else if(messagePart.equals("Type:DV")) {
                    messagePart = message.readLine();
                    System.out.println(messagePart);
                    messagePartSplit = messagePart.split(":");
                    if(messagePartSplit[0].equals("Len")){
                        length = Integer.parseInt(messagePartSplit[1]);
                        if(length > 0) {
                            for (int i = 0; i < length; i++) {
                                messagePart = message.readLine();
                                System.out.println(messagePart);
                                messagePartSplit = messagePart.split(":");
                                String tempName = messagePartSplit[0];
                                int tempCost = Integer.parseInt(messagePartSplit[1]);
                                if(!(tempName.equals(tableOfStructure.routerName))) {
                                    if (tableOfStructure.containsRoute(tempName)) {
                                        if (tableOfStructure.isFaster(tempName, tempCost)) {
                                            tableOfStructure.setRoute(tempName, stablishConection.getInetAddress(), tempCost);
                                        }
                                    } else {
                                        AdjacentNode tempAdjacent = tableOfStructure.getAdjacent(stablishConection.getInetAddress());
                                        ReachNode tempReach = new ReachNode(tempName, tempCost, tempAdjacent);
                                        tableOfStructure.addRoute(tempName, tempReach);
                                    }
                                }
                            }
                            System.out.println("********ServerEnd*********");
                        } else {
                            System.out.println("Algoritmo con Type:DV y length = 0");
                        }
                    }
                } else if(messagePart.equals("Type:KeepAlive")){
                    System.out.println(tableOfStructure.toDisp());
                    System.out.println("********ServerEnd*********");
                    flagKeepAlive = true;
                } else {
                    System.out.println("Algoritmo erroneo --> " + messagePart);
                }
            } else {
                System.out.println("Falsa identificación intentando acceder "+ adjacentName);
            }


        } catch (IOException e) {

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

        }
    }
}
