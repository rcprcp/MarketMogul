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
    private static final String T_INFO = "info";

    private static SQLiteDatabase db = null;

    public DatabaseCode(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * create both tables... this will be called when the database does not
     * exist.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        String stmt = "CREATE TABLE " + T_TICKERS + " (ticker VARCHAR(25))";

        myExecSQL(db, "onCreate()", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\"aapl\")";
        myExecSQL(db, "onCreate", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\".dji\")";
        myExecSQL(db, "onCreate", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\"googl\")";
        myExecSQL(db, "onCreate", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\"goog\")";
        myExecSQL(db, "onCreate", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\"dis\")";
        myExecSQL(db, "onCreate", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\"ibm\")";
        myExecSQL(db, "onCreate", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\"f\")";
        myExecSQL(db, "onCreate", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\"aa\")";
        myExecSQL(db, "onCreate", stmt);

        stmt = "INSERT INTO " + T_TICKERS + "(ticker) VALUES (\"cat\")";
        myExecSQL(db, "onCreate", stmt);

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

    /**
     * routine to add a new security to the database.
     *
     * @param sec - Secutiry object.
     * @throws DuplicateDataException
     */
    public void insertIntoTickerTable(Security sec) throws DuplicateDataException {
        if (db == null) {
            db = getWritableDatabase();
        }

        String stmt = "INSERT INTO "
                + T_TICKERS
                + " (ticker)"
                + " VALUES "
                + "(" + "\"" + sec.getTicker().toLowerCase().trim() + "\")";

        try {
            db.execSQL(stmt);
        } catch (Exception e) {
            Log.i(TAG, "insertIntoTickerTable(): " + stmt + " failed " + e);
            throw new DuplicateDataException("Duplicate ticker");
        }

    }

    /**
     * delete a specific security from the database.
     *
     * @param sec
     */
    public void deleteFromTickers(Security sec) {
        if (db == null) {
            db = getWritableDatabase();
        }
        String stmt = "DELETE FROM tickers WHERE ticker = \"" + sec.getTicker() + "\"";

        myExecSQL(db, "deleteFromTickers:", stmt);

    }

    /**
     * get all the securities from the database.
     *
     * @return ArryaList of Security objects.
     */
    public ArrayList<Security> getAllSecurities() {
        if (db == null) {
            db = getWritableDatabase();
        }

        String stmt = "SELECT ticker"
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
            Security s = new Security(
                    tt.getString(0),
                    "--",
                    0.0, 0.0, 0.0, 0.0, 0.0);

            securities.add(s);
        }

        tt.close();
        return securities;
    }

    public class DuplicateDataException extends Exception {
        public DuplicateDataException(String message) {
            super(message);
        }
    }
}