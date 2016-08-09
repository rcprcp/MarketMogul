package com.cottagecoders.marketmogul;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class About extends AppCompatActivity {

    DatabaseCode db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar a = getSupportActionBar();
        a.setDisplayHomeAsUpEnabled(true);
        a.setTitle("About Market Mogul");

        db = new DatabaseCode(getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();

        updateScreen();

        ((Button) findViewById(R.id.resetButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.updateNetworkStats(new NetworkUse(0, 0, System.currentTimeMillis()));
                updateScreen();
            }
        });
    }

    void updateScreen() {
        NetworkUse nn = db.getNetworkInfo();

        Date dd = new Date(nn.getSince());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm z");
        ((TextView) findViewById(R.id.dataUse)).setText(nn.getReceived() + " bytes received, "
                + nn.getSent() + " bytes sent"
                + " since " + sdf.format(dd));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK, getIntent());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

