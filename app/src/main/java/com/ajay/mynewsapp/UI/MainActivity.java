package com.ajay.mynewsapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.ajay.mynewsapp.apis.NewsApi;
import com.ajay.mynewsapp.adapters.NewsRecyclerViewAdapter;
import com.ajay.mynewsapp.R;
import com.ajay.mynewsapp.database.ArticlesContract;
import com.ajay.mynewsapp.database.ArticlesDBHelper;
import com.ajay.mynewsapp.model.Article;
import com.ajay.mynewsapp.model.News;
import com.ajay.mynewsapp.model.Source;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.ajay.mynewsapp.utils.AppConstants.API_LANGUAGE_CODE;
import static com.ajay.mynewsapp.utils.AppConstants.NEWS_API_BASE_URL;
import static com.ajay.mynewsapp.utils.AppConstants.NEWS_API_KEY;
import static com.ajay.mynewsapp.utils.AppConstants.PAGE_SIZE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;

    private List<Article> articleList = new ArrayList<>();

    private NewsRecyclerViewAdapter mAdapter;

    private SQLiteDatabase mDatabase;
    private ArticlesDBHelper mHelper;

    LinearLayoutManager linearLayoutManager;
    NewsApi newsApi;

    FloatingActionButton floatingActionButton;

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;
    int pageNumber = 1;

    ProgressBar progressBar;

    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        constraintLayout = findViewById(R.id.main_parent);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchActivityIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchActivityIntent);
            }
        });
        mHelper = new ArticlesDBHelper(getApplicationContext());
        mDatabase = mHelper.getWritableDatabase();
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.news_recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new NewsRecyclerViewAdapter(articleList);
        recyclerView.setAdapter(mAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NEWS_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        newsApi = retrofit.create(NewsApi.class);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    getNewsDataFromApi();
                }
            }
        });

        getNewsDataFromApi();
    }

    private void getNewsDataFromApi() {
        Log.i(TAG, "getNewsDataFromApi: called");

        progressBar.setVisibility(View.VISIBLE);

        Call<News> call = newsApi.getNews(API_LANGUAGE_CODE, pageNumber, PAGE_SIZE, NEWS_API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful()) {
                    News news = response.body();
                    int positionStart = articleList.size();
                    articleList.addAll(news.getArticles());
                    mAdapter.notifyItemRangeInserted(positionStart, news.getArticles().size());
                    progressBar.setVisibility(View.GONE);
                    for (Article article : news.getArticles()) {
                        Log.d(TAG, "onResponse: Article Title : " + article.getTitle());
                        Log.d(TAG, "onResponse: Inserting this article into database. ");
                        try {
                            addArticleToDatabase(article);
                        } catch (SQLiteConstraintException exception) {
                            Log.e(TAG, "onResponse: ", exception);
                        }
                    }
                    pageNumber++;
                }

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.d(TAG, "onFailure: called");
                Log.e(TAG, "onFailure: ", t);
                showSnackBar();
                retrieveArticlesFromDatabase();
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void retrieveArticlesFromDatabase() {
        Cursor cursor = mDatabase.query(ArticlesContract.ArticleEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        articleList = new ArrayList<>();
        while (cursor.moveToNext()) {

            Source source = new Source();
            source.setId(null);
            source.setName(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_SOURCE_NAME)));

            Article article = new Article();
            article.setAuthor(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_AUTHOR)));
            article.setDescription(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_DESCRIPTION)));
            article.setTitle(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_TITLE)));
            article.setUrl(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_URL)));
            article.setUrlToImage(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_IMAGE_URL)));
            article.setContent(null);
            article.setPublishedAt(cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_PUBLICATION_TIME)));
            article.setSource(source);

            // Add this article to article list
            articleList.add(article);
        }
        cursor.close();
        mAdapter.swapList(articleList);
    }

    private void addArticleToDatabase(Article article) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_SOURCE_NAME, article.getSource().getName());
        contentValues.put(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_AUTHOR, article.getAuthor());
        contentValues.put(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_TITLE, article.getTitle());
        contentValues.put(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_DESCRIPTION, article.getDescription());
        contentValues.put(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_URL, article.getUrl());
        contentValues.put(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_IMAGE_URL, article.getUrlToImage());
        contentValues.put(ArticlesContract.ArticleEntry.COLUMN_ARTICLE_PUBLICATION_TIME, article.getPublishedAt());

        mDatabase.insert(ArticlesContract.ArticleEntry.TABLE_NAME, null, contentValues);
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Retrieving data from server failed, loading data from cached database.", BaseTransientBottomBar.LENGTH_SHORT);
        snackbar.show();
    }
}