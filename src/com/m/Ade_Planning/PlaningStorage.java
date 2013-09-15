package com.m.Ade_Planning;

import com.m.Ade_Planning.process.URLConnectionHelper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlaningStorage implements CONSTANTS {

    public String createFileName(String groupe, int width, int height) {
        return createFileName(groupe, new Date(), width, height);
    }

    public String createFileName(String groupe, Date d, int width, int height) {
        String week = new SimpleDateFormat("w").format(d);
        return BASE_STORAGE_PATH + groupe + "_" + week + "_" + width + "_" + height + ".gif";
    }

    public String createFileNameUrlAdress(String filename) throws MalformedURLException {
        return new File(filename).toURI().toURL().toExternalForm();
    }

    public boolean fileExist(String file) {
        File f = getFile(file);
        return f.exists();
    }

    private File getFile(String file) {
        if (!file.startsWith(BASE_STORAGE_PATH)) {
            file = BASE_STORAGE_PATH + file;
        }
        return new File(file);
    }

    public void store(String url, String groupe, Date d, int width, int height) throws IOException {
        String filename = this.createFileName(groupe, d, width, height);
        URLConnectionHelper.downloadFile(url, filename);
    }

    public void store(String url, String filename) throws IOException {
        URLConnectionHelper.downloadFile(url, filename);
    }

    public void store(PlaningUrl url, String filename) throws IOException {
        store(url.toString(), filename);
    }

    public void store(PlaningUrl url, String groupe, Date d, int width, int height) throws IOException {
        store(url.toString(), groupe, d, width, height);
    }

}
