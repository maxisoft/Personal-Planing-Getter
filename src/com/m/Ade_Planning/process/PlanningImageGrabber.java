package com.m.Ade_Planning.process;

import java.io.IOException;
import java.util.Date;


public class PlanningImageGrabber implements CONSTANT_STRINGS {

    private int groupeid;
    private boolean cookies = false;
    private Date lastcookieupdate;

    private final static long COOKIES_OUTDATED_MILI_SECONDS = 1000 * 60 * 3; //3 min

    public PlanningImageGrabber(String groupe) {
        this.groupeid = Groupe_Value.getIntValueFromString(groupe);
        URLConnectionHelper.cleanCookies();
        lastcookieupdate = new Date();
    }

    public boolean isCookiesUpToDate() {
        return new Date().getTime() - this.lastcookieupdate.getTime() < COOKIES_OUTDATED_MILI_SECONDS && this.cookies;
    }

    public void fetchCookies() throws IOException {
        if (this.isCookiesUpToDate()) {
            return;
        }
        for (String url : BASE_URLS) {
            URLConnectionHelper.connect(url);
        }
        this.lastcookieupdate = new Date();
        this.cookies = true;
    }

    public String formatSelectIdUrl() {
        return String.format(SELECT_IDS_URL, this.getGroupeid());
    }

    public String getPlanningImgUrl() throws IOException, PlanningGrabberException {
        this.fetchCookies();
        URLConnectionHelper.connect(this.formatSelectIdUrl());
        URLConnectionHelper.connect(BOUNDS_URL);
        String s = URLConnectionHelper.read(IMAGEMAP_URL);
        return this.extractUrl(s).replace("&idPianoDay=0%2C1%2C2%2C3%2C4%2C5%2C6", "&idPianoDay=0%2C1%2C2%2C3%2C4");
    }

    private String extractUrl(String htmlbody) throws PlanningGrabberException {
        try {
            int startindex = htmlbody.indexOf(START_PATTERN);
            startindex += REMOVE_PATTERN.length();
            int endindex = htmlbody.indexOf(END_PATTERN, startindex);
            return htmlbody.substring(startindex, endindex);
        } catch (StringIndexOutOfBoundsException e) {
            throw new PlanningGrabberException("Impossible d'extraire l'url.", e);
        }
    }

    public int getGroupeid() {
        return groupeid;
    }
}

