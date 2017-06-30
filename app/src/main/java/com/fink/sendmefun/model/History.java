package com.fink.sendmefun.model;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import com.fink.sendmefun.model.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class History {

    private static final String HISTORY_FILE = "history.txt";
    private static Activity activity;
    private static History history = null;

    public static History getInstance(Activity activity){
        History.activity = activity;
        if(history == null){
            history = new History();
        }
        return history;
    }

    private History(){
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public List<Message> readHistory(){
        List<Message> messages = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(activity.openFileInput(HISTORY_FILE))) {
            messages = (List<Message>)objectInputStream.readObject();
        } catch (ClassNotFoundException|IOException ignored) {
        }
        if(messages == null){
            messages = new ArrayList<>();
        }
        return messages;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void writeHistory(Message messagesHistory) {
        List<Message> FileMessagesHistory = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(activity.openFileInput(HISTORY_FILE))) {
            FileMessagesHistory = (List<Message>) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException ignored) {
        }
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(activity.openFileOutput(HISTORY_FILE, Context.MODE_PRIVATE))) {
            List<Message> ser = new ArrayList<>();
            if (FileMessagesHistory != null) {
                ser.addAll(FileMessagesHistory);
            }
            ser.add(messagesHistory);
            objectOutputStream.writeObject(ser);
        } catch (IOException ignored) {
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void clearHistory() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(activity.openFileOutput(HISTORY_FILE, Context.MODE_PRIVATE))) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
