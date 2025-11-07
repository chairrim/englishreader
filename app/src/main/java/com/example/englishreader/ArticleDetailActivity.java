package com.example.englishreader;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleDetailActivity extends AppCompatActivity {
    // 新增：中文内容传递键
    public static final String EXTRA_CHINESE_CONTENT = "chinese_content";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_CONTENT = "content";

    private TextView contentView;
    private MaterialButton translateButton;
    private String englishContent; // 英文正文
    private String chineseContent; // 中文翻译
    private boolean isShowingEnglish = true; // 标记当前显示的语言

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Article");
        }

        // 获取传递的所有数据
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        englishContent = getIntent().getStringExtra(EXTRA_CONTENT);
        chineseContent = getIntent().getStringExtra(EXTRA_CHINESE_CONTENT);

        // 绑定视图
        TextView titleView = findViewById(R.id.detailTitle);
        TextView categoryView = findViewById(R.id.detailCategory);
        contentView = findViewById(R.id.detailContent);
        translateButton = findViewById(R.id.translateButton);

        // 设置初始数据
        titleView.setText(title);
        categoryView.setText(category);
        showEnglishContent(); // 初始显示英文

        // 翻译按钮点击事件
        translateButton.setOnClickListener(v -> switchLanguage());
    }

    // 切换中英文显示
    private void switchLanguage() {
        if (isShowingEnglish) {
            // 切换到中文
            contentView.setText(chineseContent);
            contentView.setTextIsSelectable(true); // 中文也支持复制
            translateButton.setText("Switch to English");
        } else {
            // 切换回英文（重新设置点击查词功能）
            showEnglishContent();
            translateButton.setText("Switch to Chinese");
        }
        isShowingEnglish = !isShowingEnglish;
    }

    // 显示英文内容并设置单词点击查词
    private void showEnglishContent() {
        SpannableString spannable = new SpannableString(englishContent);
        Pattern pattern = Pattern.compile("[a-zA-Z]+(?:['-][a-zA-Z]+)*");
        Matcher matcher = pattern.matcher(englishContent);

        while (matcher.find()) {
            final String word = matcher.group();
//            if (word.length() < 2) continue;

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(ArticleDetailActivity.this, WordTranslateActivity.class);
                    intent.putExtra(WordTranslateActivity.EXTRA_WORD, word);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(contentView.getCurrentTextColor()); // 使用TextView的当前文本色
                }
            };

            spannable.setSpan(clickableSpan, matcher.start(), matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        contentView.setText(spannable);
        contentView.setTextIsSelectable(true);
        contentView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        contentView.setHighlightColor(getResources().getColor(android.R.color.holo_blue_light));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}