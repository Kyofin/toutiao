package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    NewsService newsService;

    public int getLikeStatus(int entityType, int entityId, int userId)
    {
       String likeKey =  RedisKeyUtil.getLikeKey(entityType,entityId);
       boolean pos = jedisAdapter.sismember(likeKey,String.valueOf(userId));
       if (pos)
       {
           return  1;
       }
        String disLikeKey =  RedisKeyUtil.getDisLikeKey(entityType,entityId);
        return jedisAdapter.sismember(disLikeKey,String.valueOf(userId))  ?   -1 :  0;
    }

    public long  addLike(int entityType, int entityId , int userId)
    {
        //把该用户id添加到该对象的喜欢集合
        String likeKey =  RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        //把该用户id从该对象的不喜欢集合中移除
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.srem(disLikeKey,String.valueOf(userId));
        //数据持久化

        return  jedisAdapter.scard(likeKey);
    }

    public long  addDisLike(int entityType, int entityId , int userId)
    {
        //把该用户id添加到该对象的不喜欢集合
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType,entityId);
        jedisAdapter.sadd(disLikeKey,String.valueOf(userId));
        //把该用户id从该对象的喜欢集合中移除
        String likeKey =  RedisKeyUtil.getLikeKey(entityType,entityId);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        return  jedisAdapter.scard(likeKey);
    }

}
