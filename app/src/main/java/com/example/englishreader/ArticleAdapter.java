package com.example.englishreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class ArticleAdapter extends ListAdapter<Article, ArticleAdapter.ArticleViewHolder> {

    public ArticleAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Article> DIFF_CALLBACK = new DiffUtil.ItemCallback<Article>() {
        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getTitle().equals(newItem.getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getDescription().equals(newItem.getDescription());
        }
    };

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView categoryText;
        private final TextView descriptionText;
        private final MaterialCardView cardView;
        private Article currentArticle;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            categoryText = itemView.findViewById(R.id.categoryText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            cardView = (MaterialCardView) itemView;

            // 卡片点击事件：传递完整数据到详情页
            cardView.setOnClickListener(v -> {
                if (currentArticle != null) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ArticleDetailActivity.class);
                    intent.putExtra(ArticleDetailActivity.EXTRA_TITLE, currentArticle.getTitle());
                    intent.putExtra(ArticleDetailActivity.EXTRA_CATEGORY, currentArticle.getCategory());
                    intent.putExtra(ArticleDetailActivity.EXTRA_CONTENT, currentArticle.getContent());
                    intent.putExtra(ArticleDetailActivity.EXTRA_CHINESE_CONTENT, currentArticle.getChineseContent()); // 新增：传递中文翻译
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Article article) {
            currentArticle = article;
            titleText.setText(article.getTitle());
            categoryText.setText(article.getCategory());
            descriptionText.setText(article.getDescription());
        }
    }
}