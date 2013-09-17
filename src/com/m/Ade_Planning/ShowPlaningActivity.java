package com.m.Ade_Planning;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
    final private PlaningStorage storage = new PlaningStorage(); //gestionnaire de fichiers interne
    ActionBar actionBar = null;
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

    @Override
    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        this.setTheme(android.R.style.Theme_Holo);
        super.onCreate(savedInstanceState);
        restoreFromSavedInstanceState(savedInstanceState);
        testFileWriting();
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        prefeditor = preferences.edit();
        setContentView(R.layout.main);//parse le xml et "crée" la vue.
        widgetLoads();
        animationLoads();
        configWebView();
        planning = new PlanningImageGrabber(preferences.getString("groupe", "TP1-B").replace("-", ""));
        fetch_planning();
        initBoutonListener();
    }

    private void initBoutonListener() {
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
            this.date.setTime(savedInstanceState.getLong("ShowPlaningActivity.date", date.getTime()));
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

                    Display display = ShowPlaningActivity.this.getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    final int width = size.x;
                    final int height = size.y;
                    String urltoload;

                    String filename = ShowPlaningActivity.this.storage.createFileName(preferences.getString("groupe", "TP1-B"), ShowPlaningActivity.this.date, width, height);
                    if (ShowPlaningActivity.this.storage.fileExist(filename)) { //check if alrdy downloaded.
                        urltoload = ShowPlaningActivity.this.storage.createFileNameUrlAdress(filename);
                    } else {//download and try to store the img
                        String url = ShowPlaningActivity.this.planning.getPlanningImgUrl();
                        PlaningUrl planing = new PlaningUrl(url);
                        planing.setHeight(height * 2);
                        planing.setWidth(width * 2);
                        planing.setWeek(PlaningUtils.dateToADEWeek(ShowPlaningActivity.this.date));
                        urltoload = planing.toString();
                        try {
                            ShowPlaningActivity.this.storage.store(planing, filename);
                            if (ShowPlaningActivity.this.storage.fileExist(filename)) {
                                urltoload = ShowPlaningActivity.this.storage.createFileNameUrlAdress(filename);
                            } else throw new IOException("Le fichier n'existe pas.");
                        } catch (Exception e) {
                            Log.e("Ade Planning", "", e);
                        }
                    }
                    Log.d("Ade Planning", "Webview Load url : " + urltoload);
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
                    Log.e("Ade Planning", "", e);
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
        // Get calendar set to current date and time
        Calendar c = Calendar.getInstance();
        c.setTime(ShowPlaningActivity.this.date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        DateFormat df = new SimpleDateFormat("dd/MM");
        String s = df.format(c.getTime());
        c.add(Calendar.DATE, 6);
        s += " - " + df.format(c.getTime());
        if (this.actionBar != null) {
            this.actionBar.setTitle(s);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        boolean ret = super.onCreateOptionsMenu(menu);
        this.menu = menu;
        this.actionBar = getActionBar();
        this.updateDateTitleActionBar();
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_back:
                this.date = PlaningUtils.prevWeek(ShowPlaningActivity.this.date);
                this.fetchPlanning(ShowPlaningActivity.this.openrigth, ShowPlaningActivity.this.closeleft);
                return true;
            case R.id.action_forward:
                this.date = PlaningUtils.nextWeek(ShowPlaningActivity.this.date);
                this.fetchPlanning(ShowPlaningActivity.this.openleft, ShowPlaningActivity.this.closerigth);
                return true;
        }

        this.uncheckItemsMenu();
        item.setChecked(!item.isChecked());
        String value = item.getTitle().toString();
        boolean change = !preferences.getString("groupe", "TP1-B").equals(value);
        this.prefeditor.putString("groupe", value);
        this.prefeditor.commit();
        this.planning = new PlanningImageGrabber(preferences.getString("groupe", "TP1-B").replace("-", ""));
        if (change) {
            this.fetch_planning();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Suppresion des Images Téléchargées")
                .setMessage("Supprimer toutes les images de plannings ?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        for (File file : PlaningUtils.listFiles()) {
                            try {
                                file.delete();
                            } catch (final Exception e) {
                                Log.e("Ade Planning", "", e);
                                ShowPlaningActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Toast toast = Toast.makeText(ShowPlaningActivity.this.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                });
                            }
                        }
                        ShowPlaningActivity.this.fetch_planning();
                    }
                }).create().show();
    }

    private void uncheckItemsMenu() {
        int size = this.menu.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = this.menu.getItem(i);
            if (item.isCheckable()) {
                item.setChecked(false);
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong("ShowPlaningActivity.date", this.date.getTime());
    }

}
