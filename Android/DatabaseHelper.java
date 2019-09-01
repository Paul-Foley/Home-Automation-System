package com.paulfoleyblogs.paul.homeseccontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by stephen on 16/04/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public  static final String DATABASE_NAME = "customer.db";
    public  static final String TABLE_NAME = "customer_table";
    public  static final String TABLE_NAME2 = "device_table";
    public  static final String COLUMN_NAME = "clientID";
    public  static final String COLUMN_NAME2 = "fName";
    public  static final String COLUMN_NAME3 = "sName";

    public  static final String COLUMN_NAME6 = "email";
    public  static final String COLUMN_NAME7 = "password";


    public  static final String DEV_COL1 = "deviceID";
    public  static final String DEV_COL2 = "deviceName";
    public  static final String DEV_COL3 = "lightFront";
    public  static final String DEV_COL4 = "lightKit";
    public  static final String DEV_COL5 = "lightBath";
    public  static final String DEV_COL6 = "gateStat";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {



        db.execSQL("create table " + TABLE_NAME + " (clientID INTEGER PRIMARY KEY AUTOINCREMENT,fName TEXT,sName TEXT,email TEXT, password TEXT)");
        db.execSQL("create table " + TABLE_NAME2 + " (deviceID INTEGER PRIMARY KEY AUTOINCREMENT,deviceName TEXT,lightBath INTEGER,lightFront INTEGER,lightKit INTEGER, gateStat INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData (String fName, String sName, String email, String password)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            ContentValues contentValues = new ContentValues();

            contentValues.put(COLUMN_NAME2, fName);
            contentValues.put(COLUMN_NAME3, sName);
            contentValues.put(COLUMN_NAME6, email);
            contentValues.put(COLUMN_NAME7, password);

            long result = db.insert(TABLE_NAME, null, contentValues);
            if(result==-1)
                return false;
            else
                return true;
        }
        finally {
            db.close();
        }

    }


    public Integer deleteRow(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"clientID = ?", new String[]{id});

    }



    public Cursor loginData()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        if(db.isOpen()) {
            Cursor res = db.query(TABLE_NAME, new String[]{COLUMN_NAME6,COLUMN_NAME7}, COLUMN_NAME + " = 1", null, null, null, null);
            // Cursor res = db.rawQuery("select email from "+TABLE_NAME,null);

            return res;
        }
        else
            return null;




    }
}
