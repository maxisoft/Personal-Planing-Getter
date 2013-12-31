package com.m.Ade_Planning;

import android.os.Environment;

public interface CONSTANTS {
    public final static String BASE_STORAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + CONSTANTS.class.getPackage().getName() + "/files/";
    public final static int WEEK_DIFF = -21; // represente la difference entre le nombre de semaines réel et celui sur http://sedna.univ-fcomte.fr
    public final static long WEEK_IN_SECONDS = 60 * 60 * 24 * 7;
    public final static long WEEK_IN_MILI_SECONDS = 1000 * WEEK_IN_SECONDS;
    public final static String ADE_PLANNING_LOG_IDENTIFIER = "Ade Planning";
}
