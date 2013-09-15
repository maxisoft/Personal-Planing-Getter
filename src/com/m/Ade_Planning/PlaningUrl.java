package com.m.Ade_Planning;

import java.util.Date;

/**
 * Classe qui permet de manipuler une Url pointant vers l'image d'un planning.
 */
public class PlaningUrl {
    private String url;
    private int week;
    private int width = 1543;
    private int height = 624;

    public PlaningUrl(String url) {
        if (!url.startsWith("https://sedna.univ-fcomte.fr/jsp/imageEt")) {
            throw new InstantiationError("Url parameter must be a valid url.");
        }
        this.url = url;
        this.week = PlaningUtils.dateToADEWeek(new Date());
    }

    @Override
    public String toString() {
        return url;
    }

    public void setWeek(int week) {
        this.url = this.url.replace("idPianoWeek=" + this.week, "idPianoWeek=" + week);
        this.week = week;
    }

    public void setWidth(int width) {
        this.url = this.url.replace("width=" + this.width, "width=" + width);
        this.width = width;
    }

    public void setHeight(int height) {
        this.url = this.url.replace("height=" + this.height, "height=" + height);
        this.height = height;

    }

    public int getWeek() {
        return week;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
