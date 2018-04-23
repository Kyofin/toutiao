package com.nowcoder.lucene;


import com.github.pagehelper.util.StringUtil;
import com.nowcoder.model.Message;
import com.nowcoder.model.News;
import com.nowcoder.service.NewsService;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * 博客索引类
 *
 * @author Administrator
 */
@Service
public class LuceneAdapter {

    @Autowired
    NewsService newsService;

    private Directory dir = FSDirectory.open(Paths.get("/data/lucene"));;

    public LuceneAdapter() throws IOException {
    }

    /**
     * 重建所有索引
     * @throws Exception
     */
    public void initNewsIndex() throws Exception {
        List<News> allNews = newsService.getAllNews();

        //先删除原有的索引再创建新的
        for (News news : allNews) {
            deleteIndex(news.getId() + "");
        }
        for (News news : allNews) {
            addIndex(news);
        }
    }


    /**
     * 获取IndexWriter实例
     *
     * @return
     * @throws Exception
     */
    private IndexWriter getWriter() throws Exception {
        /**
         * 可以根据自己的需要放在具体位置
         */
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }

    /**
     * 添加数据
     *
     * @param news
     */
    public void addIndex(News   news) throws Exception {
        IndexWriter writer = getWriter();
        Document doc = new Document();
        doc.add(new StringField("id", String.valueOf(news.getId()), Field.Store.YES));
        /**
         * yes是会将数据存进索引，如果查询结果中需要将记录显示出来就要存进去，如果查询结果
         * 只是显示标题之类的就可以不用存，而且内容过长不建议存进去
         * 使用TextField类是可以用于查询的。
         */
        doc.add(new TextField("title", news.getTitle(), Field.Store.YES));
        if(news.getContent()!=null) {
            doc.add(new TextField("content", news.getContent(), Field.Store.YES));
        }
        writer.addDocument(doc);
        writer.close();
    }

    /**
     * 更新博客索引
     *
     * @param news
     * @throws Exception
     */
    public void updateIndex(News news) throws Exception {
        IndexWriter writer = getWriter();
        Document doc = new Document();
        doc.add(new StringField("id", String.valueOf(news.getId()), Field.Store.YES));
        doc.add(new TextField("title", news.getTitle(), Field.Store.YES));
        doc.add(new TextField("content", news.getContent(), Field.Store.YES));
        writer.updateDocument(new Term("id", String.valueOf(news.getId())), doc);
        writer.close();
    }

    /**
     * 删除指定的索引
     *
     * @param newsId
     * @throws Exception
     */
    public void deleteIndex(String newsId) throws Exception {
        IndexWriter writer = getWriter();
        writer.deleteDocuments(new Term("id", newsId));
        writer.forceMergeDeletes(); // 强制删除
        writer.commit();
        writer.close();
    }

    /**
     * 查询
     *
     * @param q 查询关键字
     * @return
     * @throws Exception
     */
    public List<News> searchNewsIndex(String q) throws Exception {
        /**
         * 注意的是查询索引的位置得是存放索引的位置，不然会找不到。
         */

        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher is = new IndexSearcher(reader);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        /**
         * title和content就是我们需要进行查找的两个字段
         * 同时在存放索引的时候要使用TextField类进行存放。
         */
        QueryParser parser = new QueryParser("title", analyzer);
        Query query = parser.parse(q);
        QueryParser parser2 = new QueryParser("content", analyzer);
        Query query2 = parser2.parse(q);

        booleanQuery.add(query, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query2, BooleanClause.Occur.SHOULD);

        TopDocs hits = is.search(booleanQuery.build(), 100);
        QueryScorer scorer = new QueryScorer(query);
        Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
        /**
         * 这里可以根据自己的需要来自定义查找关键字高亮时的样式。
         */
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
        highlighter.setTextFragmenter(fragmenter);
        List<News> newsList = new LinkedList<News>();
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            News news = new News();
            news.setId(Integer.parseInt(doc.get(("id"))));
            String title = doc.get("title");
            String content = doc.get("content");
            if (title != null) {
                TokenStream tokenStream = analyzer.tokenStream("title", new StringReader(title));
                String hightLightTitle = highlighter.getBestFragment(tokenStream, title);
                if (StringUtil.isEmpty(hightLightTitle)) {
                    news.setTitle(title);
                } else {
                    news.setTitle(hightLightTitle);
                }
            }
            if (content != null) {
                TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content));
                String hightLightContent = highlighter.getBestFragment(tokenStream, content);
                if (StringUtil.isEmpty(hightLightContent)) {
                    if (content.length() <= 200) {
                        news.setContent(content);
                    } else {
                        news.setContent(content.substring(0, 200));
                    }
                } else {
                    news.setContent(hightLightContent);
                }
            }
            newsList.add(news);
        }
        reader.close();
        return newsList;
    }
}
