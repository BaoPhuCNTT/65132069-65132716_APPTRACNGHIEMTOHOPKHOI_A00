package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppLuyenThi.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_HISTORY = "lich_su_lam_bai";

    private static final String COL_ID = "id";
    private static final String COL_MON = "mon_hoc";
    private static final String COL_TIME = "ngay_gio";
    private static final String COL_SCORE = "diem_so";
    private static final String COL_DETAIL = "chi_tiet";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MON + " TEXT, " +
                COL_TIME + " TEXT, " +
                COL_SCORE + " TEXT, " +
                COL_DETAIL + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void addHistory(String monHoc, String ngayGio, String diemSo, String chiTiet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MON, monHoc);
        cv.put(COL_TIME, ngayGio);
        cv.put(COL_SCORE, diemSo);
        cv.put(COL_DETAIL, chiTiet);
        db.insert(TABLE_HISTORY, null, cv);
        db.close();
    }

    public ArrayList<HistoryItem> getAllHistory() {
        ArrayList<HistoryItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HISTORY + " ORDER BY id DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String mon = cursor.getString(cursor.getColumnIndexOrThrow(COL_MON));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));
                String score = cursor.getString(cursor.getColumnIndexOrThrow(COL_SCORE));
                String detail = cursor.getString(cursor.getColumnIndexOrThrow(COL_DETAIL));

                list.add(new HistoryItem(id, mon, time, score, detail));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}