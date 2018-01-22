package com.nowcoder.service;

import com.nowcoder.dao.CommentDao;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDao commentDao;

    public List<Comment >getComment(int entityId , int entityType)
    {
       return   commentDao.selectByEntity(entityId,entityType);
    }

    public void addComment(Comment comment)
    {
        commentDao.addComment(comment);
    }


    public int getCommentCount(int entityId, int entityType) {
        return  commentDao.getCommentCount(entityId,entityType);
    }
}
