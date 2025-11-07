package com.example.englishreader;

import static com.example.englishreader.DatabaseHelper.TABLE_ARTICLES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ArticleDao {
    private final DatabaseHelper dbHelper;

    public ArticleDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 插入单篇文章（如果标题已存在则更新）
    public long insertOrUpdate(Article article) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, article.getTitle());
        values.put(DatabaseHelper.COLUMN_CATEGORY, article.getCategory());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, article.getDescription());
        values.put(DatabaseHelper.COLUMN_CONTENT, article.getContent());
        values.put(DatabaseHelper.COLUMN_CHINESE_CONTENT, article.getChineseContent());

        // 如果标题已存在则更新，否则插入
        long id = db.replace(TABLE_ARTICLES, null, values);
        db.close();
        return id;
    }

    // 批量插入文章
    public int bulkInsert(List<Article> articles) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // 开启事务，提高批量插入效率
        int count = 0;

        try {
            for (Article article : articles) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_TITLE, article.getTitle());
                values.put(DatabaseHelper.COLUMN_CATEGORY, article.getCategory());
                values.put(DatabaseHelper.COLUMN_DESCRIPTION, article.getDescription());
                values.put(DatabaseHelper.COLUMN_CONTENT, article.getContent());
                values.put(DatabaseHelper.COLUMN_CHINESE_CONTENT, article.getChineseContent());

                long id = db.replace(TABLE_ARTICLES, null, values);
                if (id != -1) {
                    count++;
                }
            }
            db.setTransactionSuccessful(); // 标记事务成功
        } finally {
            db.endTransaction();
            db.close();
        }
        return count;
    }

    // 查询所有文章
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_ARTICLES,
                null,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_TITLE + " ASC" // 按标题升序排列
        );

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT));
                String chineseContent = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CHINESE_CONTENT));

                articles.add(new Article(title, category, description, content, chineseContent));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return articles;
    }
}