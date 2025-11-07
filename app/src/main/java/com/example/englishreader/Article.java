package com.example.englishreader;

public class Article {
    private final String title;
    private final String category;
    private final String description;
    private final String content; // 英文正文
    private final String chineseContent; // 新增：中文翻译

    // 构造方法添加中文翻译参数
    public Article(String title, String category, String description,
                   String content, String chineseContent) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.content = content;
        this.chineseContent = chineseContent;
    }

    // 原有getter
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getContent() { return content; }

    // 新增：获取中文翻译
    public String getChineseContent() { return chineseContent; }
}