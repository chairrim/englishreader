package com.example.englishreader;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleDetailActivity extends AppCompatActivity {
    public static final String EXTRA_CHINESE_CONTENT = "chinese_content";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_CONTENT = "content";

    private TextView contentView;
    private String englishContent;
    private String chineseContent;
    private boolean isShowingEnglish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        // 初始化工具栏并设置为ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 显示返回按钮
            getSupportActionBar().setTitle("文章详情"); // 设置标题
        }

        // 获取传递的数据
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        englishContent = getIntent().getStringExtra(EXTRA_CONTENT);
        chineseContent = getIntent().getStringExtra(EXTRA_CHINESE_CONTENT);

        // 绑定视图
        TextView titleView = findViewById(R.id.detailTitle);
        TextView categoryView = findViewById(R.id.detailCategory);
        contentView = findViewById(R.id.detailContent);

        // 设置初始数据
        titleView.setText(title);
        categoryView.setText(category);
        showEnglishContent(); // 初始显示英文
    }

    // 加载Toolbar菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_detail_menu, menu);

        // 获取菜单按钮并设置为国旗文本
        MenuItem translateItem = menu.findItem(R.id.menu_translate);
        TextView textView = (TextView) translateItem.getActionView();
        if (textView != null) {
            textView.setText("🇨🇳 / 🇺🇸");  // 直接显示国旗文本
            textView.setTextSize(18);
            textView.setPadding(16, 0, 16, 0);
            textView.setGravity(Gravity.CENTER);
            // 设置点击事件
            textView.setOnClickListener(v -> switchLanguage());
        }
        return true;
    }

    // 菜单点击事件（处理翻译按钮）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_translate) {
            // 点击翻译按钮切换语言
            switchLanguage();
            return true;
        } else if (id == android.R.id.home) {
            // 点击返回按钮关闭页面
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 切换语言逻辑
    private void switchLanguage() {
        if (isShowingEnglish) {
            // 切换到中文
            contentView.setText(chineseContent);
            contentView.setTextIsSelectable(true);
            //contentView.setTextHighlightColor(getResources().getColor(android.R.color.holo_blue_light));
        } else {
            // 切换到英文
            showEnglishContent();
        }
        isShowingEnglish = !isShowingEnglish;
    }

    // 显示英文内容（带单词点击查词）
    private void showEnglishContent() {
        SpannableString spannable = new SpannableString(englishContent);
        Pattern pattern = Pattern.compile("[a-zA-Z]+(?:['-][a-zA-Z]+)*");
        Matcher matcher = pattern.matcher(englishContent);

        while (matcher.find()) {
            final String word = matcher.group();
            if (word.length() < 2) continue;

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
                    ds.bgColor = getResources().getColor(android.R.color.transparent);
                    ds.setColor(contentView.getCurrentTextColor());
                }
            };

            spannable.setSpan(clickableSpan, matcher.start(), matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        contentView.setText(spannable);
        contentView.setTextIsSelectable(true);
        contentView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        //contentView.setTextHighlightColor(getResources().getColor(android.R.color.holo_blue_light));
    }
}