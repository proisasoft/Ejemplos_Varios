package com.mobile.proisa.fotosvehiculoslibrary.SqliteDb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectionSqlOpenHelper extends SQLiteOpenHelper implements ContractConnection.ContractDb {
    public static final String DBNAME = "sqlconnection.db";
    public static final int VERSION = 1;

    private static final String CREATE_TABLE_CONNECTION
            = "CREATE TABLE "+ TABLE_NAME
            + "("
            +  _ID      + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            +  _NAME    + " TEXT NOT NULL,"
            +  _DB      + " TEXT NOT NULL,"
            +  _SERVER  + " TEXT NOT NULL,"
            +  _USER    + " TEXT NOT NULL,"
            +  _PORT    + " INTEGER NOT NULL DEFAULT 0"
            + ");";

    /**
     * instancia unica para el OpenHelper
     */
    private static ConnectionSqlOpenHelper instance;

    protected ConnectionSqlOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_CONNECTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if(newVersion > oldVersion){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "   + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }


    public static ConnectionSqlOpenHelper getInstance(Context context){
        if(instance == null){
            instance = new ConnectionSqlOpenHelper(context, ConnectionSqlOpenHelper.DBNAME, null, ConnectionSqlOpenHelper.VERSION);
        }

        return instance;
    }

}
