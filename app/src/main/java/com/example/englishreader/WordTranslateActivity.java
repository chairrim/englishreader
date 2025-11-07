package com.example.englishreader;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WordTranslateActivity extends AppCompatActivity {
    public static final String EXTRA_WORD = "word";
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_translate);

        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Word Translation");
        }

        // 获取传递的单词
        String word = getIntent().getStringExtra(EXTRA_WORD);
        if (word == null || word.isEmpty()) {
            finish();
            return;
        }

        // 初始化WebView和进度条
        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);

        // 配置WebView
        webView.getSettings().setJavaScriptEnabled(true); // 启用JS（百度翻译需要）
        webView.getSettings().setDomStorageEnabled(true); // 启用DOM存储
        webView.getSettings().setSupportZoom(true); // 支持缩放
        webView.getSettings().setBuiltInZoomControls(true); // 内置缩放控件

        // 加载进度监听
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });

        // 防止跳转外部浏览器
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // 加载百度翻译页面（替换单词）
        String translateUrl = "https://fanyi.baidu.com/m/trans?aldtype=85&from=en&to=zh&query=" + word;
        webView.loadUrl(translateUrl);
    }

    // 工具栏返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // WebView返回键处理
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}