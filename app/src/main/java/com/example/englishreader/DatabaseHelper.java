package com.example.englishreader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // 数据库信息
    private static final String DATABASE_NAME = "ArticlesDatabase";
    private static final int DATABASE_VERSION = 1;

    // 文章表
    public static final String TABLE_ARTICLES = "articles";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CHINESE_CONTENT = "chinese_content";

    // 创建表的SQL语句
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_ARTICLES + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT NOT NULL UNIQUE, " + // 标题唯一
            COLUMN_CATEGORY + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_CONTENT + " TEXT, " +
            COLUMN_CHINESE_CONTENT + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时删除旧表并重建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        onCreate(db);
    }
}