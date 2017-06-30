package com.fink.sendmefun.model.message;

public class SentMessage extends Message {

    public static final int NO_IMAGE = 0;
    public static final int PROGRESS = 1;
    public static final int CHECK_MARK = 2;
    public static final int CROSS = 3;
    public static final int TYPE = 0;

    transient private int status = NO_IMAGE;

    public SentMessage(String text) {
        super(text);
        type = TYPE;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
