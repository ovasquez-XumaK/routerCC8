package com.routing;

/**
 * Created by Jose on 05/11/2014.
 */
public class ReachNode {
    public String reachNodeName = "";
    public int reachNodeCost = 0;
    public AdjacentNode reachNodegate;

    public ReachNode(String name, int cost, AdjacentNode root){
        reachNodeName = name;
        reachNodegate = root;
        reachNodeCost = cost + reachNodegate.getCost();
    }

    public String getName(){
        return reachNodeName;
    }

    public int getCost(){
        return reachNodeCost;
    }

    public AdjacentNode getGate(){
        return reachNodegate;
    }

    public void setCost(int cost){
        reachNodeCost = cost + reachNodegate.getCost();
    }

    public void setGate(AdjacentNode gate){
        reachNodegate = gate;
    }

}
