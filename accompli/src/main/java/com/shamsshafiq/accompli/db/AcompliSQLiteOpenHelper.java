package com.shamsshafiq.accompli.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class AcompliSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "Acompli";
    private static final String TABLE_NAME = "webviewdata";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "inputfieldname";
    private static final String KEY_VALUE = "value";

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, " + KEY_VALUE + " TEXT )";

    public AcompliSQLiteOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS webviewdata");
        this.onCreate(db);
    }

    public void addEntry(Pair<String, String> entry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, entry.first);
        values.put(KEY_VALUE, entry.second);

        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public List<Pair<String, String>> getAllEntries() {
        List<Pair<String, String>> entries = new ArrayList<Pair<String, String>>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Pair<String, String> entry = null;
        if (cursor.moveToFirst()) {
            do {
                entry = new Pair<String, String>(cursor.getString(cursor.getColumnIndex(KEY_NAME)), cursor.getString(cursor.getColumnIndex(KEY_VALUE)));
                entries.add(entry);
            } while (cursor.moveToNext());
        }
        return entries;
    }

    public void deleteAllEntires() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS webviewdata");
        this.onCreate(db);
    }
}
