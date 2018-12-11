package com.example.risha.first.model;


import java.io.Serializable;

public class Message implements Serializable {
    public String idSender;
    public String idReceiver;
    public String text;
    public String orignal_text;
    public String sentiment_score;
    public String sender_name;
    public long timestamp;
}