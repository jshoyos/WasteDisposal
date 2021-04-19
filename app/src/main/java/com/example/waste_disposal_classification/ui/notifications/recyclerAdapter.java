package com.example.waste_disposal_classification.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.waste_disposal_classification.R;
import com.kwabenaberko.newsapilib.models.Article;
import com.squareup.picasso.Picasso;


import java.util.List;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {

    private List<Article> articleList;

    public recyclerAdapter(List<Article> articleList){
        this.articleList = articleList;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView titleText;
        private TextView descriptionText;
        private ImageView image;
        public MyViewHolder(final View view){
            super(view);
            titleText = (TextView) view.findViewById(R.id.news_title_text);
            descriptionText = (TextView) view.findViewById(R.id.news_Description_text);
            image = (ImageView) view.findViewById(R.id.news_image);
        }
    }

    @NonNull
    @Override
    public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, int position) {
        String title = articleList.get(position).getTitle();
        String description = articleList.get(position).getDescription();
        holder.titleText.setText(title);
        holder.descriptionText.setText(description);

        Picasso.get().load(articleList.get(position).getUrlToImage()).resize(500,500).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
