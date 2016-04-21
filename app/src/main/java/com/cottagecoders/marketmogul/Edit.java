package com.cottagecoders.marketmogul;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;

public class Edit extends AppCompatActivity {
    DatabaseCode db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar a = getSupportActionBar();
        a.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseCode(getApplicationContext());

        displayStuff();
    }

    private void displayStuff() {
        ArrayList<Security> securities = db.getAllSecurities();

        TableLayout tab = (TableLayout) findViewById(R.id.tab);
        assert tab != null;
        tab.removeAllViews();

        // empty for a new one.
        TableRow tr = new TableRow(getApplicationContext());

        EditText et = new EditText(getApplicationContext());
        et.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_border));
        et.setTextColor(getResources().getColor(R.color.Black));
        et.setPadding(10, 10, 10, 10);
        et.setWidth(200);

        CheckBox cb = new CheckBox(getApplicationContext());
        cb.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_border));
        cb.setTextColor(getResources().getColor(R.color.Blue));
        cb.setPadding(10, 10, 10, 10);

        Button bu = new Button(getApplicationContext());
        bu.setTextColor(getResources().getColor(R.color.Red));
        bu.setPadding(10, 10, 10, 10);
        bu.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_border));
        bu.setTag(et);
        bu.setText("save");
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) v.getTag();
                Security s = new Security();
                s.setTicker(et.getText().toString());
                Log.d(MarketMogul.TAG, "MarketMogul: EDIT ticker \"" + s.getTicker() + "\"");
                int rcode = db.insertIntoTickerTable(s);
                if (rcode == 0) { //ok.
                } else {
                    Toast.makeText(getApplicationContext(), "Duplicate ticker " + s.getTicker(), Toast.LENGTH_SHORT).show();
                }
                displayStuff();
            }
        });

        tr.addView(et);
        tr.addView(cb);
        tr.addView(bu);
        tab.addView(tr);

        for (Security s : securities) {
            tr = new TableRow(getApplicationContext());

            et = new EditText(getApplicationContext());
            et.setTextColor(getResources().getColor(R.color.Black));
            et.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_border));
            et.setPadding(10, 10, 10, 10);
            et.setWidth(200);
            et.setText(s.getTicker());
            tr.addView(et);

            cb = new CheckBox(getApplicationContext());
            cb.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_border));
            cb.setTextColor(getResources().getColor(R.color.Blue));
            cb.setPadding(10, 10, 10, 10);
            cb.setChecked(s.isOnStatus() == true ? true : false);
            tr.addView(cb);

            bu = new Button(getApplicationContext());
            bu.setText("X");
            bu.setTag(s);
            bu.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_border));
            bu.setTextColor(getResources().getColor(R.color.Red));
            bu.setPadding(10, 10, 10, 10);
            bu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Security s = (Security) v.getTag();
                    db.deleteFromTickers(s);
                    displayStuff();
                }
            });
            tr.addView(bu);

            tab.addView(tr);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.home:
                setResult(RESULT_OK, getIntent());
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
