package com.example.webtech4;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;


import com.squareup.picasso.Picasso;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_FIRST_ITEM = 0;
    private static final int VIEW_TYPE_OTHER_ITEMS = 1;

    private List<NewsModel> newsList;
    private Context context;

    public NewsAdapter(List<NewsModel> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_FIRST_ITEM) {
            View firstLayoutView = inflater.inflate(R.layout.news_first_item, parent, false);
            return new FirstViewHolder(firstLayoutView);
        } else {
            View otherLayoutView = inflater.inflate(R.layout.news_list_item, parent, false);
            return new NewsViewHolder(otherLayoutView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NewsModel currItem = newsList.get(position);

        if (position == 0) {
            FirstViewHolder firstHolder = (FirstViewHolder) holder;
            firstHolder.firstsrc.setText(currItem.getSource());
            firstHolder.firstdate.setText(currItem.getHours());
            firstHolder.firstheadline.setText(currItem.getTitle());
            Picasso.get().load(currItem.getImage()).resize(2048, 2048).onlyScaleDown().into(firstHolder.firstnewsImage);
            firstHolder.firstNewsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomNewsDialog dialog = new CustomNewsDialog(context, currItem.getSource(), currItem.formatDate(), currItem.getTitle(), currItem.getDescription(), currItem.getUrl());
                    Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });
        } else {
            NewsViewHolder newsHolder = (NewsViewHolder) holder;
            newsHolder.source.setText(currItem.getSource());
            newsHolder.date.setText(currItem.getHours());
            newsHolder.headline.setText(currItem.getTitle());
            Picasso.get().load(currItem.getImage()).resize(2048, 2048).onlyScaleDown().into(newsHolder.newsImage);
//            Picasso.get().load(currItem.getImage()).into(newsHolder.newsImage);
            newsHolder.newsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomNewsDialog dialog = new CustomNewsDialog(context, currItem.getSource(), currItem.formatDate(), currItem.getTitle(), currItem.getDescription(), currItem.getUrl());
                    Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FIRST_ITEM : VIEW_TYPE_OTHER_ITEMS;
    }

    private static class FirstViewHolder extends RecyclerView.ViewHolder {
        private TextView firstsrc;
        private TextView firstdate;
        private TextView firstheadline;
        private ImageView firstnewsImage;
        private CardView firstNewsCard;

        FirstViewHolder(View itemView) {
            super(itemView);
            firstsrc = itemView.findViewById(R.id.firstsrc);
            firstdate = itemView.findViewById(R.id.firstNewsDate);
            firstheadline = itemView.findViewById(R.id.firstHeadline);
            firstnewsImage = itemView.findViewById(R.id.firstNewsImg);
            firstNewsCard = itemView.findViewById(R.id.firstNewsItem);
        }
    }

    private static class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView source;
        private TextView date;
        private TextView headline;
        private ImageView newsImage;
        private CardView newsCard;
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.source);
            date = itemView.findViewById(R.id.newsDate);
            headline = itemView.findViewById(R.id.headline);
            newsImage = itemView.findViewById(R.id.newsImg);
            newsCard = itemView.findViewById(R.id.newsCard);
        }
    }
}
