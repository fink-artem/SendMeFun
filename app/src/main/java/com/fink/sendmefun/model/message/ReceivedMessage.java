package com.fink.sendmefun.model.message;

public class ReceivedMessage extends Message{

    public static final int TYPE = 1;

    public ReceivedMessage(String text) {
        super(text);
        type = TYPE;
    }
}
