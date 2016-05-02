package com.cottagecoders.marketmogul;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Edit extends AppCompatActivity {
    DatabaseCode db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ActionBar a = getSupportActionBar();
        assert a != null;
        a.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseCode(getApplicationContext());

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayStuff();
    }


    private void displayStuff() {
        ArrayList<Security> securities = db.getAllSecurities();

        TableLayout tab = (TableLayout) findViewById(R.id.tab);
        assert tab != null;
        tab.removeAllViews();
        tab.setPadding(10, 10, 10, 10);
        // empty for a new one.
        TableRow tr = new TableRow(getApplicationContext());

        EditText et = new EditText(getApplicationContext());
        et.setBackgroundResource(R.drawable.custom_border);
        et.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Black));
        et.setPadding(10, 10, 10, 10);

        et.setWidth(200);

        CheckBox cb = new CheckBox(getApplicationContext());
        cb.setBackgroundResource(R.drawable.custom_border);
        cb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue));

        final int ICON_SIZE = 22;
        String saveIcon = "\uf206";
        String trashCanIcon = "\uf1f8";
        Typeface tf = Typeface.createFromAsset(
                getApplicationContext().getAssets(), "fontawesome-webfont.ttf");

        Button bu = new Button(getApplicationContext());
        bu.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));

        bu.setBackgroundResource(R.drawable.custom_border);
        bu.setTag(et);
        bu.setText("save");
        bu.setTextSize(ICON_SIZE);
        bu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) v.getTag();
                Security s = new Security();
                s.setTicker(et.getText().toString());

                try {
                    db.insertIntoTickerTable(s);
                    Toast.makeText(getApplicationContext(), "Added " + s.getTicker(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage() + " " + s.getTicker(), Toast.LENGTH_SHORT).show();
                }
                displayStuff();
            }
        });

     //   tr.addView(et, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
     //   tr.addView(cb, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
     //   tr.addView(bu, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1));
        tr.addView(et);
        tr.addView(cb);
        tr.addView(bu);
        tab.addView(tr);

        for (Security s : securities) {
            tr = new TableRow(getApplicationContext());

//            et = new EditText(getApplicationContext());
            TextView tv = new TextView(getApplicationContext());
            tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Black));
            tv.setBackgroundResource(R.drawable.custom_border);
            tv.setPadding(10, 10, 10, 10);
            tv.setWidth(200);
            tv.setText(s.getTicker());
            tr.addView(tv);

            cb = new CheckBox(getApplicationContext());
            cb.setBackgroundResource(R.drawable.custom_border);
            //       cb.setTextColor(getResources().getColor(R.color.Blue));
            cb.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Blue));
            cb.setPadding(10, 10, 10, 10);
            cb.setChecked(s.isOnStatus());
            tr.addView(cb);

            bu = new Button(getApplicationContext());
            bu.setTypeface(tf);
            bu.setTextSize(ICON_SIZE);
            bu.setText(trashCanIcon);

            bu.setTag(s);
            bu.setBackgroundResource(R.drawable.custom_border);
            bu.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));

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
        Log.d(MarketMogul.TAG, " onOptionsItemSelected: " + item.getItemId());
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
