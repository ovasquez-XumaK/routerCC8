package com.routing;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Main {

    public static TableStructure tableOfDistance = new TableStructure();

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Router " + tableOfDistance.routerName);
            BufferedReader inFile = new BufferedReader(new FileReader("adyacentes.txt"));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
