package com.cottagecoders.marketmogul;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MarketMogul extends AppCompatActivity {
    static final String TAG = "MarketMogul";
    static boolean isPaused = false;
    ArrayList<Security> securities = new ArrayList<>();
    Handler handler = null;
    Runnable runnable = null;
    DatabaseCode db = null;
    NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_mogul);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        db = new DatabaseCode(getApplicationContext());
        securities = db.getAllSecurities();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                new GetInfo().execute();
            }
        };

        runnable.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "called onPause");
        isPaused = true;
        // cancel any previous delayed runnable(s).
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "called onResume");
        isPaused = false;
        new GetInfo().execute();
    }

    private void createTable(ArrayList<Security> sec) {
        TableRow tr = null;
        TableLayout tab = null;
        TextView tv = null;

        tab = (TableLayout) findViewById(R.id.tab);
        assert tab != null;
        tab.removeAllViews();

        // create title row:
        tr = new TableRow(getApplicationContext());
        tv = textViewSetup();
        tv.setText("Ticker");
        tv.setBackgroundColor(getResources().getColor(R.color.SteelBlue));
        tv.setTextColor(getResources().getColor(R.color.White));
        tr.addView(tv);

        tv = textViewSetup();
        tv.setText("Time");
        tv.setBackgroundColor(getResources().getColor(R.color.SteelBlue));
        tv.setTextColor(getResources().getColor(R.color.White));
        tr.addView(tv);

        tv = textViewSetup();
        tv.setText("Price");
        tv.setBackgroundColor(getResources().getColor(R.color.SteelBlue));
        tv.setTextColor(getResources().getColor(R.color.White));
        tr.addView(tv);

        tv = textViewSetup();
        tv.setText("Change");
        tv.setBackgroundColor(getResources().getColor(R.color.SteelBlue));
        tv.setTextColor(getResources().getColor(R.color.White));
        tr.addView(tv);

        tv = textViewSetup();
        tv.setText("%Chg");
        tv.setBackgroundColor(getResources().getColor(R.color.SteelBlue));
        tv.setTextColor(getResources().getColor(R.color.White));
        tr.addView(tv);

        tab.addView(tr);

        for (Security s : sec) {
            if(s.getTicker().contains("aap")) {
            //if(s.isOnStatus() == true) {
               // notifyViaStatusBar(s);
            }
            tr = new TableRow(getApplicationContext());

            tv = textViewSetup();
            tv.setText(s.getTicker());
            tr.addView(tv);

            tv = textViewSetup();
            tv.setText(s.getTime());
            tr.addView(tv);

            tv = textViewSetup();
            tv.setText("" + s.getCurrPrice());
            tr.addView(tv);

            tv = textViewSetup();
            setChange(tv, s.getChange(), "");
            tr.addView(tv);

            tv = textViewSetup();
            if(s.getCurrPrice() != 0) {
                double pchg = (s.getChange() / s.getCurrPrice()) * 100;
                Log.d(TAG, "DEBUG: pchg " + pchg + " change, price " + s.getChange()+ " " + s.getCurrPrice());
                setChange(tv, pchg, "%");
            } else {
                tv.setText("0.0");
            }
            tr.addView(tv);

            tab.addView(tr);
        }

    }

    private void notifyViaStatusBarWithIcon(Security sec) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_edit)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MarketMogul.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MarketMogul.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

// mId allows you to update the notification later on.
        int mId = 111;
        mNotificationManager.notify(mId, mBuilder.build());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.market_mogul_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_edit:
                edit();
                return true;
            case R.id.menu_about:
                about();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case 111: //edit
                securities = db.getAllSecurities();
                new GetInfo().execute();
                break;
            case 222: //about
                break;
            default:
                break;
        }
    }


    void about() {
        Intent intent = new Intent(getApplicationContext(), About.class);
        startActivityForResult(intent, 222);
    }

    void edit() {
        Intent intent = new Intent(getApplicationContext(), Edit.class);
        startActivityForResult(intent, 111);
    }

    void setChange(TextView tv, double change, String suffix) {
        tv.setText(String.format("%.2f%s", change, suffix));
        if (change < 0) {
            tv.setTextColor(getResources().getColor(R.color.Red));
        } else if (change > 0) {
            tv.setTextColor(getResources().getColor(R.color.Green));
        } else {
            tv.setTextColor(getResources().getColor(R.color.Black));
        }
        return;
    }

    private TextView textViewSetup() {
        TextView tv = new TextView(getApplicationContext());
        tv.setTextColor(getResources().getColor(R.color.Black));
        tv.setPadding(5, 10, 10, 5);
        return tv;
    }


    private class GetInfo extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            /*********************8
             Log.d(TAG, "AsyncTask - onPreExecute");
             progress = new ProgressDialog(act);
             progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
             progress.setIndeterminate(false);
             progress.setCancelable(true);
             progress.setMax(upcs.size());
             progress.setTitle("Processing...");
             progress.setMessage("HaHa you're doomed.");
             progress.show();
             ************************/
        }

        @Override
        protected Integer doInBackground(Void... Void) {

            for (Security s : securities) {

                getSecurityInfo(s);
            }
            return 1;   // must return 1 or onPostExecute will NOT be called.
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, "AsyncTask - onPostExecute");
            createTable(securities);
            if (securities.size() != 0) {
                // cancel any previous delayed runnable(s).
                handler.removeCallbacks(runnable);
                // set it to run in a minute.
                if (!isPaused) {
                    handler.postDelayed(runnable, 60000);
                }
            }
        }
    }

    private void getSecurityInfo(Security security) {
        //   String url = "http://finance.google.com/finance/info?client=ig&q=NASDAQ%3aMSFT";
        String url = "http://finance.google.com/finance/info?client=ig&q=NASDAQ%3a";
        //  String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"
        //         + upc;

        url += security.getTicker();
        Log.d(TAG, "url: " + url);

        String output = null;
        try {
            output = myHttpGET(url);
        } catch (Exception e) {
            Log.d(TAG, "getSecurityInfo: http fail... " + e);
        }

        if (output == null) {
            security.setCurrPrice(0);
            security.setHighPrice(0);
            security.setLowPrice(0);
            security.setChange(0);
            security.setVolume(0);
            security.setTime("ERROR");
            return;
        }

        // google's reply is *almost* in JSON format.
        // it has leading // and it has [] around the whole thing.
        // seems easier to parse if we can treat it as a JSONObject...

        CharSequence nothing = "";
        CharSequence slash = "//";
        output = output.replace(slash, nothing);

        CharSequence bro = "[";
        output = output.replace(bro, nothing);

        CharSequence brc = "]";
        output = output.replace(brc, nothing);

        Log.v(TAG, "output: " + output);


        // JSON testing and debugging code.
        JSONObject json = null;
        try {
            json = new JSONObject(output);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        // get id.
        String id = "";
        try {
            id = json.getString("id");
            assert id != null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "id exception " + e);
        }
        Log.d(TAG, "id  is " + id);
        if (id == null || id == "") {
            return;
        }

        // get ticker.
        String tkr = "";
        try {
            tkr = json.getString("t");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "tkr exception " + e);
        }
        Log.d(TAG, "tkr is " + tkr);
        if (tkr == null || tkr == "") {
            return;
        }

        if (!security.getTicker().equalsIgnoreCase(tkr)) {
            Log.d(TAG, "TICKER MISMATCH.  EPIC FAIL returned \"" + tkr + "\"  looking for \"" + security.getTicker() + "\"");
            return;
        }

        //get time
        String tmp = "";
        try {
            tmp = json.getString("ltt");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "time exception " + e);
        }
        Log.d(TAG, "tmp  is " + tmp);
        if (tkr == null || tkr == "") {
            return;
        }
        Log.d(TAG, "time tmp is " + tkr);
        security.setTime(tmp);

        //get last price
        double val = 0.0;

        try {
            tmp = json.getString("l_fix");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "last price exception " + e);
        }

        if (tmp == null || tmp == "") {
            return;
        }
        Log.d(TAG, "last price tmp is " + tkr);
        try {
            security.setCurrPrice(Double.parseDouble(tmp));
        } catch (Exception e) {
            Log.d(MarketMogul.TAG, "error parsing currPrice. " + tmp);
            security.setTime("ERROR");
            security.setCurrPrice(0);
        }

        //get change on day.
        try {
            tmp = json.getString("c");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "tkr exception " + e);
            Log.d(TAG, "tkr exception " + e);
        }
        Log.d(TAG, "change is " + tmp);
        if (tmp == null || tmp == "") {
            return;
        }
        try {
            security.setChange(Double.parseDouble(tmp));
        } catch (Exception e) {
            Log.d(MarketMogul.TAG, "error parsing change. " + tmp);
            security.setTime("ERROR");
            security.setChange(0);
        }
    }

    private String myHttpGET(String u) throws IOException {

        URL url = null;
        url = new URL(u);

        HttpURLConnection httpconn = null;
        httpconn = (HttpURLConnection) url.openConnection();

        BufferedReader input = null;
        if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            input = new BufferedReader(new InputStreamReader(
                    httpconn.getInputStream()), 8192);
        }
        StringBuilder response = new StringBuilder();
        String strline = null;
        while ((strline = input.readLine()) != null) {
            response.append(strline);
        }
        input.close();
        return response.toString();
    }
}
