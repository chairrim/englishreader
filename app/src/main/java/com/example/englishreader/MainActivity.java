package com.example.englishreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMPORT_XLSX = 1001; // 文件选择请求码
    private RecyclerView articlesRecyclerView;
    private ArticleAdapter adapter;
    private ProgressBar progressBar;
    private ArticleDao articleDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 新增：配置 Toolbar 作为 ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar); // 确保布局中有 id 为 toolbar 的控件
        setSupportActionBar(toolbar);

        // 初始化数据库
        articleDao = new ArticleDao(this);

        // 初始化视图
        articlesRecyclerView = findViewById(R.id.articlesRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // 配置RecyclerView
        adapter = new ArticleAdapter();
        articlesRecyclerView.setAdapter(adapter);
        articlesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        articlesRecyclerView.setHasFixedSize(true);

        // 加载文章数据（优先从数据库加载）
        loadArticlesFromDatabase();
    }

    // 从数据库加载文章
// 从数据库加载文章
    private void loadArticlesFromDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        articlesRecyclerView.setVisibility(View.GONE);

        new Thread(() -> {
            // 声明为 final，确保 lambda 中可访问
            final List<Article> articles;

            // 从数据库查询
            List<Article> dbArticles = articleDao.getAllArticles();

            // 如果数据库为空，使用默认数据
            if (dbArticles.isEmpty()) {
                articles = getDefaultArticles();
            } else {
                articles = dbArticles;
            }

            // 在主线程更新 UI
            runOnUiThread(() -> {
                adapter.submitList(articles);
                progressBar.setVisibility(View.GONE);
                articlesRecyclerView.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    // 默认文章数据（数据库为空时使用）
    private List<Article> getDefaultArticles() {
        List<Article> articles = new ArrayList<>();
        // 添加之前定义的默认文章（包含中文翻译）
        articles.add(new Article(
                "The Evolution of Machine Learning",
                "Technology",
                "How machine learning algorithms have advanced over the past decade...",
                "Machine learning has undergone remarkable transformations...",
                "机器学习在过去十年中经历了显著的变革..."
        ));
        articles.add(new Article(
                "Sustainable Living in Urban Areas",
                "Environment",
                "Practical ways to reduce your carbon footprint...",
                "Urban sustainability has become increasingly important...",
                "随着全球超过一半的人口居住在城市，城市可持续发展变得越来越重要..."
        ));
        return articles;
    }

    // 初始化菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // 菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_import) {
            // 打开文件选择器，选择XLSX文件
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // XLSX文件类型
            startActivityForResult(intent, REQUEST_IMPORT_XLSX);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 处理文件选择结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMPORT_XLSX && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                importXLSXFile(uri);
            }
        }
    }

    // 导入XLSX文件并解析
    private void importXLSXFile(Uri uri) {
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Importing file...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            List<Article> importedArticles = new ArrayList<>();
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 Workbook workbook = new XSSFWorkbook(inputStream)) {

                // 读取第一个工作表
                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();

                // 跳过表头行（第一行）
                if (rowIterator.hasNext()) {
                    rowIterator.next(); // 表头：标题、简介、分类、英文正文、中文翻译
                }

                // 解析每行数据
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Article article = parseRowToArticle(row);
                    if (article != null) {
                        importedArticles.add(article);
                    }
                }

                // 保存到数据库
                int count = articleDao.bulkInsert(importedArticles);

                // 刷新列表
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Imported " + count + " articles", Toast.LENGTH_SHORT).show();
                    loadArticlesFromDatabase(); // 重新加载数据
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Import failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // 将Excel行解析为Article对象
    private Article parseRowToArticle(Row row) {
        try {
            // 按表头顺序获取单元格数据（标题、简介、分类、英文正文、中文翻译）
            String title = getCellValue(row.getCell(0));
            String description = getCellValue(row.getCell(1));
            String category = getCellValue(row.getCell(2));
            String content = getCellValue(row.getCell(3));
            String chineseContent = getCellValue(row.getCell(4));

            // 验证必填字段
            if (title == null || title.trim().isEmpty()) {
                return null;
            }

            return new Article(title, category, description, content, chineseContent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 获取单元格内容（处理不同数据类型）
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}