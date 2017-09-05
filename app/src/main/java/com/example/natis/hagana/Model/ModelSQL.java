package com.example.natis.hagana.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ModelSQL  extends SQLiteOpenHelper {
    public ModelSQL(Context context) {

        super(context, "database.db", null, 3);
        Log.d("TAG","context =="+context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ClientUserSQL.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ClientUserSQL.onUpgrade(db, oldVersion, newVersion);
    }


}
