package com.cottagecoders.marketmogul;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseCode extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseCode";
    private static final String DB_NAME = "MarketMogul.sqlite";
    private static final int VERSION = 1;

    private static final String T_TICKERS = "tickers";

    private static SQLiteDatabase db = null;

    public DatabaseCode(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * create all the tables... this will be called when the database does not
     * exist.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String stmt;

        // books...
        stmt = "CREATE TABLE " + T_TICKERS
                + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + " ticker VARCHAR(50), "
                + " is_on_status INT) ";


        myExecSQL(db, "onCreate()", stmt);

        stmt = "CREATE UNIQUE INDEX " + T_TICKERS + "_ix1  ON " + T_TICKERS + "(ticker) ";
        myExecSQL(db, "onCreate()", stmt);

    }

    /**
     * simple routine to execute an SQL statement and handle errors. this little
     * routine makes the code a bit more concise.
     * <p/>
     * obviously, this will only work for statements without host variables.
     *
     * @param db   database object.
     * @param rtn  calling routine name for the Log statement in case of trouble.
     * @param stmt the SQL statement to execute.
     */
    public void myExecSQL(SQLiteDatabase db, String rtn, String stmt) {
        try {
            db.execSQL(stmt);
        } catch (Exception e) {
            Log.i(TAG, rtn + " myExecSQL(): " + stmt + " failed " + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // implement migration code here.
        Log.i(TAG, "onUpgrade() -- get here.");

    }

    public int insertIntoTickerTable(Security sec) {
        if (db == null) {
            db = getWritableDatabase();
        }

        int stat  = sec.isOnStatus() == true ? 1 : 0;
        String stmt = "INSERT INTO "
                + T_TICKERS
                + " (ticker, is_on_status)"
                + " VALUES "
                + "(" + "\"" + sec.getTicker() + "\", " + stat
                + " )";

        try {
            db.execSQL(stmt);
        } catch (Exception e) {
            Log.i(TAG, "insertIntoTickerTable(): " + stmt + " failed " + e);
            return -1 ;
        }

        return 0;

    }

    public void deleteFromTickers(Security sec) {
        if (db == null) {
            db = getWritableDatabase();
        }
        String stmt = "DELETE FROM tickers WHERE ticker = \"" + sec.getTicker() + "\"";

        myExecSQL(db, "deleteFromTickers:", stmt);

    }

    public ArrayList<Security> getAllSecurities() {
        if (db == null) {
            db = getWritableDatabase();
        }

        String stmt = "SELECT ticker, is_on_status "
                + " FROM "
                + T_TICKERS
                + " ORDER BY ticker ";

        ArrayList<Security> securities = new ArrayList<>();
        Cursor tt;
        try {
            tt = db.rawQuery(stmt, null);
        } catch (Exception e) {
            Log.e(TAG, "getAllTickers() stmt " + stmt + " failed " + e);
            return securities;
        }

        if (tt == null) {
            return securities;
        }
        while (tt.moveToNext()) {
            Log.d(MarketMogul.TAG, "getAllSecurities() \"" + tt.getString(0) + "\" " + tt.getInt(1));
            boolean b = tt.getInt(1) == 1 ? true : false;
            Security s = new Security(
                    tt.getString(0),
                    "--",
                    0.0, 0.0, 0.0, 0.0, 0.0,
                    true, b);

            securities.add(s);
        }

        tt.close();
        return securities;
    }

}