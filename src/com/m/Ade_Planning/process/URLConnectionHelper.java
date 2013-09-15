package com.m.Ade_Planning.process;

import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.*;

public class URLConnectionHelper {
    protected static CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    static {
        CookieHandler.setDefault(cookieManager);
    }

    private URLConnectionHelper() {
    }

    public static String read(String url_s) throws IOException {
        URL url = new URL(url_s);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream()));
            String str;
            StringBuffer sb = new StringBuffer();
            while ((str = in.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            in.close();
            //EntityUtils.consume(entity);
            return sb.toString();
        } finally {
            urlConnection.disconnect();
        }
    }

    public static void connect(String url_s) throws IOException {
        URL url = new URL(url_s);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.connect();
        urlConnection.getResponseCode();
        urlConnection.disconnect();
    }

    public static void downloadFile(String imageURL, String fileName) throws IOException {
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            URL url = new URL(imageURL);
            File file = new File(fileName);
            URLConnection ucon = url.openConnection();
            is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (is != null) {
                is.close();
            }

        }

    }

    public static void cleanCookies() {
        cookieManager.getCookieStore().removeAll();
    }
}
