package com.ajay.mynewsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ajay.mynewsapp.R;
import com.ajay.mynewsapp.UI.MainActivity;
import com.ajay.mynewsapp.UI.NewsWebViewActivity;
import com.ajay.mynewsapp.model.Article;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.NewsViewHolder> {

    private List<Article> mArticleList;

    public NewsRecyclerViewAdapter(List<Article> articleList) {
        mArticleList = articleList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        final Article article = mArticleList.get(position);
        holder.nli_heading.setText(article.getTitle());
        holder.nli_source_name.setText(String.format("Source : %s", article.getSource().getName()));

        if (article.getAuthor() == null || article.getAuthor().equals("null")) {
            holder.nli_author.setVisibility(View.GONE);
        } else {
            holder.nli_author.setVisibility(View.VISIBLE);
            holder.nli_author.setText(String.format("Author : %s", article.getAuthor()));
        }

        holder.nli_description.setText(article.getDescription());

        holder.nli_date.setText(String.format("Publish Date : %s", article.getFormattedDate()));

        String imageUrlString = article.getUrlToImage();
        if (imageUrlString == null || imageUrlString.equals("")) {
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            Context context = holder.imageView.getContext();
            Glide.with(context).load(imageUrlString).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        public TextView nli_heading;
        public TextView nli_source_name;
        public TextView nli_author;
        public TextView nli_description;
        public TextView nli_date;
        public ImageView imageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            nli_heading = itemView.findViewById(R.id.nli_heading);
            nli_source_name = itemView.findViewById(R.id.nli_source_name);
            nli_author = itemView.findViewById(R.id.nli_author);
            nli_description = itemView.findViewById(R.id.nli_description);
            nli_date = itemView.findViewById(R.id.nli_date);
            imageView = itemView.findViewById(R.id.imageView);

            nli_heading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Article article = mArticleList.get(position);
                    Context context = view.getContext();
                    Intent newsWebIntent = new Intent(context, NewsWebViewActivity.class);
                    newsWebIntent.putExtra("ARTICLE", article);

                    context.startActivity(newsWebIntent);
                }
            });
        }
    }

    public void swapList(List<Article> newList) {
        if (mArticleList != null) {
            mArticleList.clear();
        }
        mArticleList = newList;
        if (newList != null) {
            notifyDataSetChanged();
        }
    }
}
