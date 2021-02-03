package com.ajay.mynewsapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ArticlesDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "News.db";
    public static final int DATABASE_VERSION = 1;

    public ArticlesDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sSQL = " CREATE TABLE "
                + ArticlesContract.ArticleEntry.TABLE_NAME + " ( "
                + ArticlesContract.ArticleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + ArticlesContract.ArticleEntry.COLUMN_ARTICLE_TITLE + " TEXT UNIQUE, "
                + ArticlesContract.ArticleEntry.COLUMN_ARTICLE_SOURCE_NAME + " TEXT, "
                + ArticlesContract.ArticleEntry.COLUMN_ARTICLE_AUTHOR + " TEXT, "
                + ArticlesContract.ArticleEntry.COLUMN_ARTICLE_PUBLICATION_TIME + " TEXT, "
                + ArticlesContract.ArticleEntry.COLUMN_ARTICLE_DESCRIPTION + " TEXT, "
                + ArticlesContract.ArticleEntry.COLUMN_ARTICLE_URL + " TEXT, "
                + ArticlesContract.ArticleEntry.COLUMN_ARTICLE_IMAGE_URL + " TEXT "
                + ");";

        db.execSQL(sSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArticlesContract.ArticleEntry.TABLE_NAME);
        onCreate(db);
    }
}
