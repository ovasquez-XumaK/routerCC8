package com.routing;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class Main {

    public static TableStructure tableOfDistance = new TableStructure();
    public static HashMap<String,DistanceVectorClient> threadOfClient = new HashMap<String, DistanceVectorClient>();
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Router " + tableOfDistance.routerName);
            BufferedReader inFile = new BufferedReader(new FileReader("/home/jose/Escritorio/proyectoAlmostFinal/routerCC8/src/com/routing/adyacentes.txt"));
            String temp;
            while((temp = inFile.readLine()) != null){
                try{
                    System.out.println("Adyacente : " + temp);
                    StringTokenizer tokens = new StringTokenizer(temp,",");
                    String adjacentName = tokens.nextToken();
                    String adjacentIp = tokens.nextToken();
                    int adjacentCost = Integer.parseInt(tokens.nextToken());
                    AdjacentNode tempAdjacent = new AdjacentNode(adjacentName,adjacentIp,adjacentCost);
                    tableOfDistance.addAdjacent(adjacentName,tempAdjacent);
                } catch (NumberFormatException e)
                {
                    System.out.println("ERROR in adyacentes.txt.\n");
                }
            }
            inFile.close();
            //Start server Listener
            ServerSocket serverSocket = new ServerSocket(9080);
            ServerListener server = new ServerListener(serverSocket,tableOfDistance);
            server.run();
            //instance the servers client
            HashMap iterateTable = tableOfDistance.getAdjacentTable();
            Iterator iteratorTable = iterateTable.keySet().iterator();
            while(iteratorTable.hasNext()){
                String name = iteratorTable.next().toString();
                DistanceVectorClient clientDistanceVectorInstance = new DistanceVectorClient(name,tableOfDistance);
                clientDistanceVectorInstance.run();
                threadOfClient.put(name,clientDistanceVectorInstance);
            }

            //startClient threads

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
