package com.ajay.mynewsapp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.ajay.mynewsapp.R;
import com.ajay.mynewsapp.adapters.NewsRecyclerViewAdapter;
import com.ajay.mynewsapp.apis.NewsApi;
import com.ajay.mynewsapp.model.Article;
import com.ajay.mynewsapp.model.News;

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

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "SearchActivity";

    Spinner spinner;
    Button searchButton;
    EditText editTextSearch;

    String mSortOption = "relevancy";
    String searchString = "";

    private RecyclerView recyclerView;
    ProgressBar progressBar2;

    private List<Article> articleList = new ArrayList<>();

    private NewsRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        spinner = findViewById(R.id.sortSpinner);
        searchButton = findViewById(R.id.buttonSearch);
        editTextSearch = findViewById(R.id.editTextSearch);
        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setVisibility(View.GONE);

        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.GONE);

        // array adapter for spinner(drop down menu)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.drop_down_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NewsRecyclerViewAdapter(articleList);
        recyclerView.setAdapter(mAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar2.setVisibility(View.VISIBLE);
                searchArticles();
            }
        });

    }

    private void searchArticles() {

        searchString = editTextSearch.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NEWS_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApi newsApi = retrofit.create(NewsApi.class);
        Call<News> call = newsApi.getSearchedNews(searchString, mSortOption, NEWS_API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful()) {
                    News news = response.body();
                    articleList = news.getArticles();
                    mAdapter.swapList(articleList);
                    progressBar2.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
//                    for (Article article : news.getArticles()) {
//                        Log.d(TAG, "onResponse: Article Title : " + article.getTitle());
//                    }
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.d(TAG, "onFailure: called");
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        mSortOption = adapterView.getItemAtPosition(position).toString();
        Log.d(TAG, "onItemSelected: clicked on " + mSortOption);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}