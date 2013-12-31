package com.m.Ade_Planning;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.m.Ade_Planning.process.PlanningImageGrabber;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ShowPlaningActivity extends Activity implements CONSTANTS {
    public static final String PREFERENCES_GROUPE_KEY = ShowPlaningActivity.class.getSimpleName() + ".groupe";
    public static final String BUNDLE_DATE_KEY = ShowPlaningActivity.class.getSimpleName() + ".date";
    private final PlaningStorage storage = new PlaningStorage(); //gestionnaire de fichiers internes
    private ActionBar actionBar = null;
    private WebView webview = null;
    private PlanningImageGrabber planning = null;
    private ProgressBar webViewProgressBar = null;
    private ImageButton reloadBtn = null;
    private Menu menu = null;
    private Date date = new Date();
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor prefeditor = null;
    private Animation openrigth = null;
    private Animation closerigth = null;
    private Animation opendefault = null;
    private Animation closedefault = null;
    private Animation openleft = null;
    private Animation closeleft = null;
    private SubMenu submenu;
    private int screen_width;
    private int screen_height;
    private boolean first_start = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setTheme(android.R.style.Theme_Holo);
        super.onCreate(savedInstanceState);
        restoreFromSavedInstanceState(savedInstanceState);

        Point size = new Point();
        Display display = ShowPlaningActivity.this.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;

        testFileWriting();
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        prefeditor = preferences.edit();
        setContentView(R.layout.main);//parse le xml et "crée" la vue.
        widgetLoads();
        animationLoads();
        configWebView();
        planning = new PlanningImageGrabber(getGroupePreference().replace("-", ""));
        fetch_planning();
        initBoutonsListener();
    }

    private void initBoutonsListener() {
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPlaningActivity.this.fetch_planning();
            }
        });
    }

    private void testFileWriting() {
        File mFile = new File(BASE_STORAGE_PATH + "TEST_WRITE");
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {

            try {
                mFile.getParentFile().mkdirs();
                mFile.createNewFile();
                mFile.delete();
            } catch (IOException e) {
                Toast t = Toast.makeText(this.getApplicationContext(), "Impossible de sauvegarder les images.", Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    private void restoreFromSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.date.setTime(savedInstanceState.getLong(BUNDLE_DATE_KEY, date.getTime()));
        }
    }

    private void widgetLoads() {
        webview = (WebView) findViewById(R.id.webView);
        webViewProgressBar = (ProgressBar) findViewById(R.id.webViewProgressBar);
        reloadBtn = (ImageButton) findViewById(R.id.reloadBtn);
    }

    private void animationLoads() {
        openrigth = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.openrigth);
        closerigth = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.closerigth);
        opendefault = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.opendefault);
        closedefault = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.closedefault);
        openleft = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.openleft);
        closeleft = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.closeleft);
    }

    private void configWebView() {
        final WebSettings settings = webview.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        webview.setInitialScale(45);
    }

    private void fetch_planning() {
        fetchPlanning(this.opendefault, this.closedefault);
    }

    @SuppressWarnings("unchecked")
    private void fetchPlanning(final Animation open, final Animation close) {
        this.webViewProgressBar.setVisibility(View.VISIBLE);
        this.reloadBtn.setVisibility(View.GONE);
        final boolean closeanimation = this.webview.getVisibility() == View.VISIBLE;
        if (closeanimation) {
            this.webview.startAnimation(close);
        }
        this.webview.setVisibility(View.GONE);
        this.updateDateTitleActionBar();

        AsyncTask task = new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    String urltoload;

                    String filename = ShowPlaningActivity.this.storage.createFileName(getGroupePreference(), ShowPlaningActivity.this.date, screen_width, screen_height);
                    if (ShowPlaningActivity.this.storage.fileExist(filename)) { //check if alrdy downloaded.
                        urltoload = ShowPlaningActivity.this.storage.createFileNameUrlAdress(filename);
                    } else {//download and try to store the img
                        final int week = PlaningUtils.dateToADEWeek(ShowPlaningActivity.this.date);
                        String url = ShowPlaningActivity.this.planning.getPlanningImgUrl(week);
                        PlaningUrl planing = new PlaningUrl(url);
                        planing.setHeight(screen_height * 2);
                        planing.setWidth(screen_width * 2);
                        planing.setWeek(week);
                        urltoload = planing.toString();
                        try {
                            ShowPlaningActivity.this.storage.store(planing, filename);
                            if (ShowPlaningActivity.this.storage.fileExist(filename)) {
                                urltoload = ShowPlaningActivity.this.storage.createFileNameUrlAdress(filename);
                            } else throw new IOException("Le fichier n'existe pas.");
                        } catch (Exception e) {
                            Log.e(ADE_PLANNING_LOG_IDENTIFIER, "", e);
                        }
                    }
                    Log.d(ADE_PLANNING_LOG_IDENTIFIER, "Webview Load url : " + urltoload);
                    ShowPlaningActivity.this.webview.loadUrl(urltoload);
                    Runnable openwebview = new Runnable() {
                        @Override
                        public void run() {
                            ShowPlaningActivity.this.webview.setVisibility(View.VISIBLE);
                            ShowPlaningActivity.this.webview.setAlpha(1.f);
                            int max = 1000;
                            while (ShowPlaningActivity.this.webview.getProgress() != 100 && max-- > 0) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                }
                            }
                            ShowPlaningActivity.this.webViewProgressBar.setVisibility(View.GONE);
                            ShowPlaningActivity.this.webview.startAnimation(open);

                        }
                    };
                    if (closeanimation) {
                        ShowPlaningActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowPlaningActivity.this.webViewProgressBar.setVisibility(View.GONE);
                            }
                        });
                        Thread.sleep(150);
                    }
                    ShowPlaningActivity.this.runOnUiThread(openwebview);

                } catch (final Exception e) {
                    Log.e(ADE_PLANNING_LOG_IDENTIFIER, "", e);
                    ShowPlaningActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ShowPlaningActivity.this.webViewProgressBar.setVisibility(View.GONE);
                            ShowPlaningActivity.this.reloadBtn.setVisibility(View.VISIBLE);
                            final Toast toast = Toast.makeText(ShowPlaningActivity.this.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }
                return null;
            }
        };
        task.execute();
    }

    private void updateDateTitleActionBar() {
        if (this.actionBar == null) {
            return;
        }
        // Get calendar set to current date and time
        Calendar c = Calendar.getInstance();
        c.setTime(this.date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        DateFormat df = new SimpleDateFormat("dd/MM");
        String s = df.format(c.getTime());
        c.add(Calendar.DATE, 6);
        s += " - " + df.format(c.getTime());
        if (PlaningUtils.dateToADEWeek(this.date) == PlaningUtils.dateToADEWeek(new Date())) {
            s = "<b>" + s + "</b>";
        }
        this.actionBar.setTitle(Html.fromHtml(s));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        boolean ret = super.onCreateOptionsMenu(menu);
        this.menu = menu;
        this.submenu = this.menu.findItem(R.id.configgroupe).getSubMenu();
        this.submenu.setHeaderIcon(R.drawable.ic_action_settings);//TODO
        this.actionBar = getActionBar();
        this.updateDateTitleActionBar();
        initCheckableTPItemMenu(getGroupePreference());
        return ret;
    }

    private String getGroupePreference() {
        return preferences.getString(PREFERENCES_GROUPE_KEY, "TP1-B");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                prevCalendar();
                return true;
            case R.id.action_forward:
                nextCalendar();
                return true;

            case R.id.reloadimg:
                String filename = this.storage.createFileName(getGroupePreference(), ShowPlaningActivity.this.date, screen_width, screen_height);
                if (ShowPlaningActivity.this.storage.fileExist(filename)) { //check if alrdy downloaded.
                    File f = new File(filename);
                    f.delete();
                }
                this.fetch_planning();
                return true;
            case R.id.exit:
                finish();
                return true;
            case R.id.deleteall:
                for (File file : PlaningUtils.listFiles()) {
                    if (!file.delete()) {
                        final String fname = file.getName();
                        ShowPlaningActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final Toast toast = Toast.makeText(ShowPlaningActivity.this.getApplicationContext(), "Impossible de supprimer le fichier :" + fname, Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                    }
                }
                this.fetch_planning();
                return true;

            case R.id.itemTP1A:
            case R.id.itemTP1B:
            case R.id.itemTP2B:
            case R.id.itemTP2C:
                this.uncheckItemsSubMenu();
                item.setChecked(true);
                String value = item.getTitle().toString();
                boolean change = !getGroupePreference().equals(value);
                this.prefeditor.putString(PREFERENCES_GROUPE_KEY, value);
                this.prefeditor.commit();
                this.planning = new PlanningImageGrabber(getGroupePreference().replace("-", ""));
                if (change) {
                    this.fetch_planning();
                }
                if (this.first_start) {
                    final MenuItem configgrp = this.menu.findItem(R.id.configgroupe);
                    CharSequence s = configgrp.getTitle();
                    configgrp.setTitle(s.toString().replace("<h2><b>", "").replace("</b></h2>", ""));
                    this.first_start = false;
                }

                return true;
        }
        return false;
    }

    private void initCheckableTPItemMenu(String s) {
        assert s.startsWith("TP");
        int size = submenu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = submenu.getItem(i);
            if (item.isCheckable() && item.getTitle().toString().equals(s)) {
                item.setChecked(true);
            } else {
                item.setChecked(false);
            }
        }
    }

    private void uncheckItemsSubMenu() {
        SubMenu submenu = this.menu.findItem(R.id.configgroupe).getSubMenu();
        int size = submenu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = submenu.getItem(i);
            if (item.isCheckable() && item.getTitle().toString().startsWith("TP")) {
                item.setChecked(false);
            }
        }
    }

    private void prevCalendar() {
        this.date = PlaningUtils.prevWeek(ShowPlaningActivity.this.date);
        this.fetchPlanning(ShowPlaningActivity.this.openrigth, ShowPlaningActivity.this.closeleft);
    }

    private void nextCalendar() {
        this.date = PlaningUtils.nextWeek(ShowPlaningActivity.this.date);
        this.fetchPlanning(ShowPlaningActivity.this.openleft, ShowPlaningActivity.this.closerigth);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong(BUNDLE_DATE_KEY, this.date.getTime());
    }

    @Override
    public void onAttachedToWindow() {
        if (!preferences.contains(PREFERENCES_GROUPE_KEY)) {
            this.first_start = true;
            this.openOptionsMenu();
            final MenuItem configgrp = this.menu.findItem(R.id.configgroupe);
            CharSequence s = configgrp.getTitle();
            configgrp.setTitle(Html.fromHtml("<h2><b>" + s + "</b></h2>"));
        }
    }
}
