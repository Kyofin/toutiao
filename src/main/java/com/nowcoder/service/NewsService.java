package com.nowcoder.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
        private static final Logger LOGGER = LoggerFactory.getLogger(NewsService.class);

        @Autowired
        HostHolder hostHolder;

        @Autowired
        private NewsDAO newsDao;




        public PageInfo<News> getNewsByPage(int userId , Integer currentPage )
        {
                List <News> newsList = null;
                if (currentPage == null)
                {
                        PageHelper.startPage(1,10);
                        newsList =newsDao.selectByUserId(userId);
                }else
                {
                        PageHelper.startPage(currentPage,10);
                        newsList =newsDao.selectByUserId(userId);
                }
                //包装内容
                PageInfo<News> pageInfo = new PageInfo<>(newsList);
                return  pageInfo;
        }

        /**
         * 保存图片到服务器
         * @param file
         * @return
         */
        public String saveImage(MultipartFile file) {
                //检验是否为空
                if (file == null) {
                        return  null;
                }

                //检验文件类型是否图片
                boolean pos = false;
                String fileExtensionName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1 );
                LOGGER.info("fileExtensionName = "+fileExtensionName);

                for(String extension : ToutiaoUtil.IMAGE_FILE_EXTD)
                {
                        if (fileExtensionName.equals(extension))
                        {
                            pos = true;
                        }
                }

                if ( pos==false)
                {
                        return  null;
                }

                //保存图片到本地
                String fileName  = UUID.randomUUID().toString().replace("-","")+"."+fileExtensionName;
                try {
                        //利用Files的类方法
                        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR+fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                        e.printStackTrace();
                        LOGGER.error("保存图片失败");
                }

                //返回get图片的url
                String fileUrl = ToutiaoUtil.TOUTIAO_DOMAIN+"image/"+fileName+"/";

                return  fileUrl;
        }

        /**
         * 添加新的咨询
         * (没有判断各字段是否为空，格式是否对)
         * @param image
         * @param title
         * @param link
         */
        public void addNews(String image, String title, String link) {
                News insertNews = new News();
                insertNews.setCreatedDate(new Date());
                insertNews.setLink(link);
                insertNews.setImage(image);
                insertNews.setTitle(title);
                if (hostHolder.getUser() == null)
                {
                        insertNews.setUserId(3);  //id= 3 为匿名用户
                }
                insertNews.setUserId(hostHolder.getUser().getId());
                //插入数据库
                newsDao.addNews(insertNews);
        }

        public News getNewsById(int newsId) {
               return   newsDao.selectById(newsId);
        }

    public void updateCommentCount(int newsId, int count) {
                newsDao.updateCommentCount(newsId,count);
    }

        public void updateLikeCount(int newsId, int likeCount) {
                newsDao.updateLikeCount(newsId,likeCount);
        }
}
