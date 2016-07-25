package com.cottagecoders.marketmogul;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MarketMogul extends AppCompatActivity {

    static boolean isPaused = false;
    ArrayList<Security> securities = new ArrayList<>();
    Handler handler = null;
    Runnable runnable = null;
    DatabaseCode db = null;
    NotificationManager notificationManager;
    boolean displayPleaseWait = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_mogul);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        db = new DatabaseCode(getApplicationContext());
        securities = db.getAllSecurities();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                new GetInfo().execute();
            }
        };
    }

    private boolean isLandscape() {
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onPause() {
        Log.d(getResources().getString(R.string.app_name), "onPause");
        super.onPause();
        isPaused = true;

        // cancel any previous delayed runnable(s).
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        Log.d(getResources().getString(R.string.app_name), "onResume");
        super.onResume();
        isPaused = false;
        displayPleaseWait = true;
        new GetInfo().execute();
    }

    private void tableTitleRow() {
        TableLayout tab = null;
        TableRow tr = null;
        TextView tv = null;

        tab = (TableLayout) findViewById(R.id.tab);
        assert tab != null;
        tab.removeAllViews();

        // create title row:
        tr = new TableRow(getApplicationContext());

        int numCols = 1;
        if (isLandscape())
            numCols = 2;

        for (int i = 0; i < numCols; i++) {
            tv = textViewSetup();
            tv.setText("Ticker");
            tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv.setTextColor(getResources().getColor(R.color.White));
            tr.addView(tv);

            tv = textViewSetup();
            tv.setText("Time");
            tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv.setTextColor(getResources().getColor(R.color.White));
            tr.addView(tv);

            tv = textViewSetup();
            tv.setText("Price");
            tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv.setTextColor(getResources().getColor(R.color.White));
            tr.addView(tv);

            tv = textViewSetup();
            tv.setText("Change");
            tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv.setTextColor(getResources().getColor(R.color.White));
            tr.addView(tv);

            tv = textViewSetup();
            tv.setText("%Chg");
            tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tv.setTextColor(getResources().getColor(R.color.White));
            tr.addView(tv);
        }

        tab.addView(tr);
    }

    private void createTable(ArrayList<Security> sec) {
        TableLayout tab = null;
        tab = (TableLayout) findViewById(R.id.tab);
        assert tab != null;

        TableRow tr = null;

        // create title row:
        tableTitleRow();

        int leftRight = 0;
        for (int i = 0; i < sec.size(); i++) {
            Security s = sec.get(i);

            if (isLandscape()) {
                if (leftRight % 2 == 0) {
                    tr = new TableRow(getApplicationContext());
                }
            } else {
                tr = new TableRow(getApplicationContext());
            }
            TextView tv = textViewSetup();
            tv.setText(s.getTicker());
            tv.setTextColor(getResources().getColor(R.color.White));
            tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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
            if (s.getCurrPrice() != 0) {
                double pchg = (s.getChange() / s.getCurrPrice()) * 100;
                setChange(tv, pchg, "%");
            } else {
                tv.setText("0.0");
            }
            tr.addView(tv);

            leftRight++;
            if (isLandscape()) {
                if (leftRight % 2 == 1) {
                    // check if this is the last item, and it would be on
                    // the left side of the two-column display.
                    if (i == sec.size() - 1) {
                        tab.addView(tr);
                    }
                    continue;
                }
            }
            tab.addView(tr);
        }
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
        Log.d(getResources().getString(R.string.app_name), "onActiviryResult");
        switch (requestCode) {
            case 111: //edit
                securities = db.getAllSecurities();
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
        ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            Log.d(getResources().getString(R.string.app_name), "AsyncTask - onPreExecute");
            if (displayPleaseWait == true) {
                if (dialog == null) {
                    dialog = new ProgressDialog(MarketMogul.this);
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setTitle("MarketMogul");
                    dialog.setMessage("Retrieving data.  Please wait...");
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }
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
            Log.d(getResources().getString(R.string.app_name), "AsyncTask - onPostExecute");
            createTable(securities);
            if (securities.size() != 0) {
                // cancel any previous delayed runnable(s).
                handler.removeCallbacks(runnable);
                // set it to run in a minute.
                if (!isPaused) {
                    displayPleaseWait = false;
                    handler.postDelayed(runnable, 60000);
                }
            }
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
            displayPleaseWait = false;
        }
    }

    private void getSecurityInfo(Security security) {

        String url = "http://finance.google.com/finance/info?client=ig&q=";

        url += security.getTicker();

        String output = null;
        try {
            output = myHttpGET(url);
        } catch (Exception e) {
            Log.d(getResources().getString(R.string.app_name), "getSecurityInfo: http fail... " + e);
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

        CharSequence bracket = "[";
        output = output.replace(bracket, nothing);

        bracket = "]";
        output = output.replace(bracket, nothing);

        //Log.v(getResources().getString(R.string.app_name), "output: " + output);

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
            Log.d(getResources().getString(R.string.app_name), "id exception " + e);
        }
        if (id == null || id == "") {
            return;
        }

        // get ticker.
        String tkr = "";
        try {
            tkr = json.getString("t");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(getResources().getString(R.string.app_name), "tkr exception " + e);
        }
        if (tkr == null || tkr == "") {
            return;
        }

        if (!security.getTicker().equalsIgnoreCase(tkr)) {
            Log.d(getResources().getString(R.string.app_name), "TICKER MISMATCH.  EPIC FAIL returned \"" + tkr + "\"  looking for \"" + security.getTicker() + "\"");
            return;
        }

        //get time
        String tmp = "";
        try {
            tmp = json.getString("ltt");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(getResources().getString(R.string.app_name), "time exception " + e);
        }
        if (tkr == null || tkr == "") {
            return;
        }
        security.setTime(tmp);

        //get last price
        double val = 0.0;

        try {
            tmp = json.getString("l_fix");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(getResources().getString(R.string.app_name), "last price exception " + e);
        }

        if (tmp == null || tmp == "") {
            return;
        }
        try {
            security.setCurrPrice(Double.parseDouble(tmp));
        } catch (Exception e) {
            Log.d(getResources().getString(R.string.app_name), "error parsing currPrice. " + tmp);
            security.setTime("ERROR");
            security.setCurrPrice(0);
        }

        //get change on day.
        try {
            tmp = json.getString("c");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(getResources().getString(R.string.app_name), "change-on-day exception " + e);
        }
        if (tmp == null || tmp == "") {
            return;
        }
        try {
            security.setChange(Double.parseDouble(tmp));
        } catch (Exception e) {
            Log.d(getResources().getString(R.string.app_name), "error parsing change. " + tmp);
            security.setTime("ERROR");
            security.setChange(0);
        }
    }

    private String myHttpGET(String u) {


        NetworkInfo net = db.getNetworkInfo();
        net.setSent(net.getSent() + u.length());

        URL url = null;
        try {
            url = new URL(u);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpURLConnection httpconn = null;
        try {
            httpconn = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedReader input = null;
        try {
            if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                input = new BufferedReader(new InputStreamReader(
                        httpconn.getInputStream()), 8192);
            } else {
                Log.d(getResources().getString(R.string.app_name), "myHttpGET(): bad http return code. url: " + u
                        + " code "
                        + httpconn.getResponseCode()
                        + " "
                        + httpconn.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder response = new StringBuilder();
        String strline = null;
        try {
            while ((strline = input.readLine()) != null) {
                response.append(strline);
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        net.setReceived(net.getReceived() + response.length());
        net.setSince(System.currentTimeMillis());
        db.updateNetworkStats(net);

        return response.toString();
    }
}
