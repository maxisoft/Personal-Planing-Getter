package com.m.Ade_Planning;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlaningUtils implements CONSTANTS {
    private PlaningUtils() {
    }

    public static File[] listFiles() {
        File f = new File(BASE_STORAGE_PATH);
        return f.listFiles();
    }

    public static String[] listFilesPath() {
        File[] all = listFiles();
        String[] ret = new String[all.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = all[i].getPath();
        }
        return ret;
    }

    public static Date nextWeek(Date d) {
        long milli = d.getTime() + WEEK_IN_MILI_SECONDS;
        return new Date(milli);
    }

    public static Date prevWeek(Date d) {
        long milli = d.getTime() - WEEK_IN_MILI_SECONDS;
        return new Date(milli);
    }

    public static int dateToADEWeek(Date d) {
        String week = new SimpleDateFormat("w").format(d);
        return Integer.parseInt(week) - WEEK_DIFF;
    }


}
