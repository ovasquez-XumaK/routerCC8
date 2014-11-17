package com.routing;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Jose on 02/11/2014.
 */


public class TableStructure {
    public static String routerName = "jose";
    private HashMap<String,ReachNode> distanceVectorTable = new HashMap<String, ReachNode>();
    private HashMap<String,AdjacentNode> adjacentTable = new HashMap<String, AdjacentNode>();

    //Start adjacent Methods

        public void addAdjacent(String name, AdjacentNode adjacent){
            adjacentTable.put(name,adjacent);
        }

        public void deleteAdjacent(String name){
            adjacentTable.remove(name);
        }

        public void setAdjacent(String name,AdjacentNode adjacent){
            adjacentTable.replace(name,adjacent);
        }

        public AdjacentNode getAdjacent(String name){
            return adjacentTable.get(name);
        }

        public AdjacentNode getAdjacent(InetAddress ip){
            Iterator table = adjacentTable.keySet().iterator();
            while(table.hasNext()){
                String name = table.next().toString();
                AdjacentNode tempCompare = adjacentTable.get(name);
                if(tempCompare.getIP().equals(ip.toString())){
                    return tempCompare;
                }
            }
            return null;
        }

        public boolean containsAdjacent(InetAddress ip){
            Iterator table = adjacentTable.keySet().iterator();
            while(table.hasNext()){
                String name = table.next().toString();
                AdjacentNode tempCompare = adjacentTable.get(name);
                if(tempCompare.getIP().equals(ip.toString())){
                    return true;
                }
            }
            return false;
        }


        public boolean containsAdjacent(String name){
            return adjacentTable.containsKey(name);
        }

        public HashMap getAdjacentTable(){return adjacentTable;}

    //Finish adjacent Methods

    //Start distance vector methods

        public void addRoute(String name, ReachNode node){

            distanceVectorTable.put(name,node);
            this.setChangeOnTable();
        }

        public void deleteRoute(String name){
            distanceVectorTable.remove(name);
        }

        public void setRoute(String name, InetAddress gate, int cost){
            ReachNode tempRoute = getRoute(name);
            AdjacentNode tempAdjacent = getAdjacent(gate);
            tempRoute.setGate(tempAdjacent);
            tempRoute.setCost(cost);
            distanceVectorTable.replace(name,tempRoute);
            this.setChangeOnTable();
        }

        public ReachNode getRoute(String name){
            return distanceVectorTable.get(name);
        }

        public boolean isFaster(String name, int cost){
            ReachNode tempNode = distanceVectorTable.get(name);
            int tempCost = tempNode.getCost();
            if( cost < tempCost){
                return true;
            } else {
                return false;
            }
        }

        public boolean containsRoute(String name){
            return distanceVectorTable.containsKey(name);
        }

        public HashMap getTable(){
            return distanceVectorTable;
        }

    //Finish distance vector methods

    //set change on table
        public void setChangeOnTable(){
            Iterator adjacentTableIterator = adjacentTable.keySet().iterator();
            while(adjacentTableIterator.hasNext()){
                String nameIterator = adjacentTableIterator.next().toString();
                AdjacentNode iteratorNode = adjacentTable.get(nameIterator);
                iteratorNode.tableChange();
            }
        }
}
