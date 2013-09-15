package com.m.Ade_Planning.process;

public interface CONSTANT_STRINGS {
    public static final String[] BASE_URLS = new String[]{
            //"http://sedna.univ-fcomte.fr/directPlanning2013.html",
            "https://sedna.univ-fcomte.fr/jsp/standard/direct_planning.jsp?data=d80fddf1b920ab42d1edc037dbe4a0760091ef306820eb3eff8f12d8f118c4a0f9c4bc151f5a34002b1ac076a75584273408d4f83b36fa9b59fb1a2fffdfaaa6d151d7022736b8e572cf37ffd68fd25a9e3566339db5b6e567c5f6923b60b09b5dff9f4275037661",
            "https://sedna.univ-fcomte.fr/jsp/custom/modules/plannings/pianoDays.jsp?forceLoad=true",
            "https://sedna.univ-fcomte.fr/jsp/custom/modules/plannings/pianoWeeks.jsp?forceLoad=true",
            "https://sedna.univ-fcomte.fr/jsp/standard/gui/tree.jsp?category=trainee&expand=false&forceLoad=false&reload=false&scroll=0",
            "https://sedna.univ-fcomte.fr/jsp/standard/gui/tree.jsp?branchId=3950&expand=false&forceLoad=false&reload=false&scroll=0"};

    public static final String SELECT_IDS_URL = "https://sedna.univ-fcomte.fr/jsp/standard/gui/tree.jsp?selectId=%s&reset=false&forceLoad=false&scroll=0";
    public static final String BOUNDS_URL = "https://sedna.univ-fcomte.fr/jsp/custom/modules/plannings/bounds.jsp?clearTree=false";
    public static final String IMAGEMAP_URL = "https://sedna.univ-fcomte.fr/jsp/custom/modules/plannings/imagemap.jsp?clearTree=false&width=1543&height=624";

    public static final String START_PATTERN = "<img border=0 src=\"https://sedna.univ-fcomte.fr/";
    public static final String REMOVE_PATTERN = "<img border=0 src=\"";
    public static final String END_PATTERN = "\"";
}
