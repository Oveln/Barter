package me.oveln.barter;

import java.io.Serializable;

public class packetpair implements Serializable {
    private String x,y;
    public packetpair(String x,String y) {
        this.x = x;
        this.y = y;
    }
    public String getX() {return x;}
    public String getY() {return y;}
}
