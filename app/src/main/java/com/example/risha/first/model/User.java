package com.example.risha.first.model;


import com.example.risha.first.data.StaticConfig;

import java.io.Serializable;

public class User implements Serializable {
    public String name;
    public String email;
    public String number;
    public String avata;
    public Status status;
    public Message message;
    public String Native_Language;
    public String gender;
    public String dob;


    public User(){
        status = new Status();
        message = new Message();
        status.isOnline = false;
        status.timestamp = 0;
        message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.timestamp = 0;
        Native_Language = "en";
        avata = StaticConfig.STR_DEFAULT_BASE64;
    }
}
