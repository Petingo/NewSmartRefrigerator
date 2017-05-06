package com.ntust.smartrefrigerator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

/**
 * Created by petingo on 2017/5/6.
 */

public class DBHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME="DB";
    private static final int VERSION=1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + "REF" + " ("
                +_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "PushTime INTEGER,"
                + "cardID CHAR,"
                + "QRCode CHAR,"
                + "PopTime INTEGER"
                + ")");

        db.execSQL("CREATE TABLE " + "INFORMATION" + " ("
                +_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "cardID CHAR,"
                + "Name CHAR,"
                + "StudentID CHAR"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
