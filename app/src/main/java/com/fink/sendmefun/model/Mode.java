package com.fink.sendmefun.model;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by Admin on 03.12.2015.
 */
public class Mode {

    private static final String MODE_FILE = "mode.txt";
    private static final int OFF = 0;
    private static final int ON = 1;
    private static final int UNKNOWN = 2;
    private static Activity activity;
    private static Mode mode = null;
    private int writeMode = UNKNOWN;

    private Mode() {
    }

    public static Mode getInstance(Activity activity) {
        Mode.activity = activity;
        if (mode == null) {
            mode = new Mode();
        }
        return mode;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean getWriteMode() {
        if (writeMode == UNKNOWN) {
            try (Scanner reader = new Scanner(activity.openFileInput(MODE_FILE))) {
                if (reader.hasNext()) {
                    writeMode = reader.nextInt();
                } else {
                    writeMode = ON;
                }
            } catch (IOException ignored) {
            }
        }
        return writeMode == ON;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setWriteMode(boolean mode) {
        writeMode = mode ? ON : OFF;
        try (PrintWriter out = new PrintWriter(activity.openFileOutput(MODE_FILE, Context.MODE_PRIVATE))) {
            out.println(writeMode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
