package com.cottagecoders.marketmogul;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    long lastUpdate = 0;
    Handler handler = null;
    Runnable runnable = null;
    DatabaseCode db = null;

    static final String EUROS = "\u20ac";
    static final String GBP = "\u00a3";

    // just a number to indicate we need to refresh the display.
    static final int MUST_REFRESH = 678;

    // interval in ms.
    final long UPDATE_INTERVAL = 60000;

//   NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_mogul);

//        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

    }

    /**
     * simple helper function to let us know if we're in landscape mode.
     *
     * @return is device in landscape orientation?
     */
    private boolean landscape() {
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

        if (db == null) {
            db = new DatabaseCode(getApplicationContext());
            securities = db.getAllSecurities();
        }

        if (handler == null) {
            handler = new Handler();
        }

        if (runnable == null) {
            runnable = new Runnable() {
                public void run() {
                    new GetInfo().execute();
                }
            };
        }

        isPaused = false;
        // time in seconds.
        long currTime = System.currentTimeMillis();
        if (lastUpdate < currTime - UPDATE_INTERVAL) {
            new GetInfo().execute();
        }
    }

    /**
     * After removing all the rows from the table, this routine initializes
     * the table with the header.
     */
    private void tableTitleRow() {
        TableLayout tab = null;
        TableRow tr = null;
        TextView tv = null;

        tab = (TableLayout) findViewById(R.id.tab);
        assert tab != null;
        tab.removeAllViews();

        // create title row:
        tr = new TableRow(getApplicationContext());

        // portrait - 1 column, landscape 2 columns.
        int numCols = 1;
        if (landscape())
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

            if (landscape()) {
                if (leftRight % 2 == 0) {
                    tr = new TableRow(getApplicationContext());
                }
            } else {
                tr = new TableRow(getApplicationContext());
            }
            TextView tv = textViewSetup();

            String tmp = s.getTicker();
            if (s.getExch().length() > 0) {
                tmp += "(" + s.getExch() + ")";
            }

            tv.setText(tmp);
            tv.setTextColor(getResources().getColor(R.color.White));
            tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tr.addView(tv);

            tv = textViewSetup();
            tv.setText(s.getTime());
            tr.addView(tv);

            tv = textViewSetup();
            tv.setText(s.getCurrency() + s.getCurrPrice());
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
            if (landscape()) {
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
        Log.d(getResources().getString(R.string.app_name), "onActivityResult");

        if (lastUpdate + UPDATE_INTERVAL < System.currentTimeMillis()) {
            lastUpdate = 0;
        }

        switch (requestCode) {
            case 111: //edit
                securities = db.getAllSecurities();
                if (resultCode == MUST_REFRESH) {
                    lastUpdate = 0;   // force screen redraw.
                }
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
                    handler.postDelayed(runnable, UPDATE_INTERVAL);
                }
            }

            // remove dialog box, if on-screen.
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }

            lastUpdate = System.currentTimeMillis();
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

        if (output == null || output.equals("")) {
            security.setCurrPrice(0);
            security.setHighPrice(0);
            security.setLowPrice(0);
            security.setChange(0);
            security.setVolume(0);
            security.setTime("ERROR");
            return;
        }

        // Google's reply is *almost* in JSON format.
        // it has leading "//" and it has "[]" around the whole thing.
        // modify it so we can treat it as a JSONObject...

        output = output.replace("//", "");

        CharSequence bracket = "[";
        output = output.replace(bracket, "");

        bracket = "]";
        output = output.replace(bracket, "");

        // TODO: remove debugging code.
        //Log.v(getResources().getString(R.string.app_name), "output: " + output);

        JSONObject json;
        try {
            json = new JSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        // get id.
        String id = "";
        try {
            id = json.getString("id");
            assert id != null;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(getResources().getString(R.string.app_name), "id exception " + e);
            return;
        }

        if (id == null || id.equals("")) {
            Log.d(getResources().getString(R.string.app_name), "id is null or blank");
            return;
        }

        // get ticker.
        String tkr = "";
        try {
            tkr = json.getString("t");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(getResources().getString(R.string.app_name), "tkr exception " + e);
            return;
        }
        if (tkr == null || tkr.equals("")) {
            return;
        }

        if (!security.getTicker().equalsIgnoreCase(tkr)) {
            Log.d(getResources().getString(R.string.app_name), "TICKER MISMATCH.  EPIC FAIL! returned \"" + tkr + "\"  looking for \"" + security.getTicker() + "\"");
            return;
        }

        // get exchange.
        String exch = "";
        //set currency if not $$.
        String currency = "";
        try {
            exch = json.getString("e");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(getResources().getString(R.string.app_name), "exchange exception " + e);
            return;
        }
        if (exch == null
                || exch.equals("")
                || exch.equalsIgnoreCase("NYSE")
                || exch.equalsIgnoreCase("NASDAQ")
                || exch.equalsIgnoreCase("INDEXSP")) {
            exch = "";
        } else if( exch.equalsIgnoreCase("OTCMKTS")) { // Maybe prices are in Euros?
            currency = EUROS;
        } else if( exch.equalsIgnoreCase("LON")) { // Maybe prives are in GBP?
            currency = GBP;
        }
        security.setExch(exch);
        security.setCurrency(currency);

        //get time
        String tim = "";
        try {
            tim = json.getString("ltt");
        } catch (Exception e) {
            Log.d(getResources().getString(R.string.app_name), "time exception " + e);
            return;
        }

        if (tim == null || tim.equals("")) {
            return;
        }
        security.setTime(tim);

        //get last price
        String pr = "";
        try {
            pr = json.getString("l_fix");
        } catch (Exception e) {
            Log.d(getResources().getString(R.string.app_name), "last price exception " + e);
        }

        if (pr == null || pr.equals("")) {
            return;
        }
        try {
            security.setCurrPrice(Double.parseDouble(pr));
        } catch (Exception e) {
            Log.d(getResources().getString(R.string.app_name), "currPrice parsing exception. " + pr);
            security.setTime("ERROR");
            security.setCurrPrice(0);
        }

        //get change on day.
        String tmp = "";
        try {
            tmp = json.getString("c");
        } catch (Exception e) {
            Log.d(getResources().getString(R.string.app_name), "change-on-day exception " + e);
        }
        if (tmp == null || tmp.equals("")) {
            return;
        }
        try {
            security.setChange(Double.parseDouble(tmp));
        } catch (Exception e) {
            Log.d(getResources().getString(R.string.app_name), "exception parsing time. " + tmp);
            security.setTime("ERROR");
            security.setChange(0);
        }
    }

    private String myHttpGET(String u) {


        NetworkUse net = db.getNetworkInfo();
        net.setSent(net.getSent() + u.length());

        URL url = null;
        try {
            url = new URL(u);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpURLConnection httpConn = null;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BufferedReader input = null;
        try {
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                input = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()), 8192);
            } else {
                Log.d(getResources().getString(R.string.app_name), "myHttpGET(): bad http return code. url: " + u
                        + " code "
                        + httpConn.getResponseCode()
                        + " "
                        + httpConn.getResponseMessage());
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder response = new StringBuilder();
        String strLine = "";
        try {
            while ((strLine = input.readLine()) != null) {
                response.append(strLine);
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
