package com.fink.sendmefun.model.message;

import java.io.Serializable;

public abstract class Message implements Serializable{

    private String text;
    protected int type;

    public Message(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }
}
