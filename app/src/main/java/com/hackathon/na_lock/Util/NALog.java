package com.hackathon.na_lock.Util;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by karthik on 21-06-2016.
 */
public class NALog {
    private static final boolean INFO = true;
    private static final boolean DEBUG = true;
    private static final boolean ERROR = true;
    private static final boolean LOGGABLE = false;



    //custom info log
    public static void info(String tag, String msg) {
        if (INFO)
            Log.i(tag, msg);

        appendLog(tag + " -- >" + msg);
    }

    //custom debug log
    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);

        appendLog(tag + " -- >" + msg);
    }

    //custom error log
    public static void e(String tag, String msg, Throwable tr) {
        if (ERROR)
            Log.e(tag, msg, tr);

        appendLog(tag + " -- >" + msg);
    }

    //custom error cause log
    public static void e(String tag, String msg) {
        if (ERROR)
            Log.e(tag, msg);

        appendLog(tag + " -- >" + msg);
    }

    public static void appendLog(String text) {

        if(!LOGGABLE)
            return;
        File logFile = new File("sdcard/NALog.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
