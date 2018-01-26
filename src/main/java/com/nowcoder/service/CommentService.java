package com.nowcoder.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcoder.dao.CommentDao;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDao commentDao;

    public PageInfo<Comment> getComments(int entityId , int entityType , Integer currentPage)
    {
        List <Comment> commentLsit = null;
        int pageSize = 40;

        if (currentPage == null)
        {
            PageHelper.startPage(1,pageSize);
            commentLsit =commentDao.selectByEntity(entityId,entityType);
        }else
        {
            PageHelper.startPage(currentPage,pageSize);
            commentLsit =commentDao.selectByEntity(entityId,entityType);
        }
        //包装内容
        PageInfo<Comment> pageInfo = new PageInfo<>(commentLsit);
        return  pageInfo;
    }

    public void addComment(Comment comment)
    {
        commentDao.addComment(comment);
    }


    public int getCommentCount(int entityId, int entityType) {
        return  commentDao.getCommentCount(entityId,entityType);
    }
}
