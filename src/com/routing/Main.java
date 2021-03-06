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
    public static HashMap<String,Thread> threadOfClient = new HashMap<String, Thread>();
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Router " + tableOfDistance.routerName);
            BufferedReader inFile = new BufferedReader(new FileReader("C:\\Users\\Jose\\Desktop\\proyectoFinalCC8\\routerCC8\\src\\com\\routing\\adyacentes.txt"));
            String temp;
            while((temp = inFile.readLine()) != null){
                try{
                    StringTokenizer tokens = new StringTokenizer(temp,",");
                    String adjacentName = tokens.nextToken();
                    String adjacentIp = tokens.nextToken();
                    int adjacentCost = Integer.parseInt(tokens.nextToken());
                    AdjacentNode tempAdjacent = new AdjacentNode(adjacentName,adjacentIp,adjacentCost);
                    tableOfDistance.addAdjacent(adjacentName,tempAdjacent);
                    ReachNode tempReachNode = new ReachNode(adjacentName,0,tempAdjacent);
                    tableOfDistance.addRoute(adjacentName,tempReachNode);
                } catch (NumberFormatException e)
                {
                    System.out.println("ERROR in adyacentes.txt.\n");
                }
            }
            inFile.close();

            //instance the servers client
            HashMap iterateTable = tableOfDistance.getAdjacentTable();
            Iterator iteratorTable = iterateTable.keySet().iterator();
            while(iteratorTable.hasNext()){
                String name = iteratorTable.next().toString();
                Thread newThread = new Thread(new DistanceVectorClient(name,tableOfDistance));
                newThread.start();
                threadOfClient.put(name,newThread);
            }

            //Start server Listener
            ServerSocket serverSocket = new ServerSocket(9080);
            Thread serverList = new Thread(new ServerListener(serverSocket,tableOfDistance));
            serverList.start();


            //startClient threads

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
