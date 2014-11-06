package com.routing;
/**
 * Created by Jose on 05/11/2014.
 */
public class AdjacentNode {
    public String adjacentName = "";
    public String adjacentIP = "";
    public int adjacentCost = 0;

    public AdjacentNode(String name, String ip, int cost){
        adjacentName = name;
        adjacentIP = ip;
        adjacentCost = cost;
    }

    public String getIP(){
        return adjacentIP;
    }

    public String getName(){
        return adjacentName;
    }

    public int getCost(){
        return adjacentCost;
    }

    public void setIP(String ip){
        adjacentIP = ip;
    }

    public void setCost(int cost){
        adjacentCost = cost;
    }


}
