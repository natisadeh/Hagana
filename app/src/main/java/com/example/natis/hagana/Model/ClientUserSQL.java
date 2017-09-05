package com.example.natis.hagana.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

public class ClientUserSQL {
    static final String USER_TABLE = "users";
    static final String USER_ID = "userId";
    static final String USER_FNAME = "firstName";
    static final String USER_LNAME = "lastName";
    static final String USER_GENDER = "gender";

    static List<ClientUser> getAllUsers(SQLiteDatabase db) {
        Cursor cursor = db.query(USER_TABLE, null, null, null, null, null, null);
        List<ClientUser> list = new LinkedList<ClientUser>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(USER_ID);
            int fnameIndex = cursor.getColumnIndex(USER_FNAME);
            int lnameIndex = cursor.getColumnIndex(USER_LNAME);
            int genderIndex =cursor.getColumnIndex(USER_GENDER);
            do {
                ClientUser user = new ClientUser();
                user.setUserId(cursor.getString(idIndex));
                user.setfirstName(cursor.getString(fnameIndex));
                user.setlastName(cursor.getString(lnameIndex));
                user.setGender(cursor.getString(genderIndex));
                list.add(user);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public static void addUser(SQLiteDatabase db, ClientUser user) {
        ContentValues values = new ContentValues();
        values.put(USER_ID, user.getUserId());
        values.put(USER_FNAME, user.getfirstName());
        values.put(USER_LNAME, user.getlastName());
        values.put(USER_GENDER, user.getGender());
        db.insert(USER_TABLE, USER_ID, values);
    }

    public static void addNote(SQLiteDatabase db, ClientUser user, String note) {
        ClientUser currentUser = getUser(db, user.getUserId());
        currentUser.setNotes(note);
        updateUser(db, currentUser);
    }

    static ClientUser getUser(SQLiteDatabase db, String stId) {
        String[] whereArgs = new String[] {
                stId
        };
        Cursor cursor = db.query(USER_TABLE, null,USER_ID+"=?",whereArgs,null,null,null,"1");
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(USER_ID);
            int fnameIndex = cursor.getColumnIndex(USER_FNAME);
            int lnameIndex = cursor.getColumnIndex(USER_LNAME);
            int genderIndex =cursor.getColumnIndex(USER_GENDER);
            ClientUser user = new ClientUser();
            user.setUserId(cursor.getString(idIndex));
            user.setfirstName(cursor.getString(fnameIndex));
            user.setlastName(cursor.getString(lnameIndex));
            user.setGender(cursor.getString(genderIndex));
            return user;
        }
        return null;
    }

    public static void updateUser(SQLiteDatabase db,ClientUser user){
        ContentValues values = new ContentValues();
        values.put(USER_ID, user.getUserId());
        values.put(USER_FNAME, user.getfirstName());
        values.put(USER_LNAME, user.getlastName());
        values.put(USER_GENDER,user.getGender());
        String[] whereArgs = new String[] {
                user.getUserId()
        };
        db.update(USER_TABLE, values,USER_ID+"=?",whereArgs);
    }

    static public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + USER_TABLE +
                " (" +
                USER_ID + " TEXT PRIMARY KEY, " +
                USER_FNAME + " TEXT, " +
                USER_LNAME + " TEXT, " +
                USER_GENDER + " TEXT NOT NULL); ");
    }

    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }
}
