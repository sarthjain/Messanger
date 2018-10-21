package com.example.risha.first.model;


import java.io.Serializable;

public class Status implements Serializable {
    public boolean isOnline;
    public long timestamp;

    public Status(){
        isOnline = false;
        timestamp = 0;
    }
}
