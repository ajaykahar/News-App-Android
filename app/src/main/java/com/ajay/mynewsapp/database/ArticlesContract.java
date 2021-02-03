package com.ajay.mynewsapp.database;

import android.provider.BaseColumns;

import com.ajay.mynewsapp.model.Article;

public class ArticlesContract {
    private ArticlesContract() {
    }

    public static class ArticleEntry implements BaseColumns {
        public static final String TABLE_NAME = "ArticlesList";
        public static final String COLUMN_ARTICLE_SOURCE_NAME = "sourceName";
        public static final String COLUMN_ARTICLE_AUTHOR = "Author";
        public static final String COLUMN_ARTICLE_TITLE = "Title";
        public static final String COLUMN_ARTICLE_DESCRIPTION = "Description";
        public static final String COLUMN_ARTICLE_URL = "Url";
        public static final String COLUMN_ARTICLE_IMAGE_URL = "UrlToImage";
        public static final String COLUMN_ARTICLE_PUBLICATION_TIME = "PublicationTime";
    }
}
