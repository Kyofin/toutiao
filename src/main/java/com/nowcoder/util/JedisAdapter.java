package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
public class JedisAdapter implements InitializingBean{

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool jedisPool = null;



    public static  void  print(int number ,  Object target)
    {
        System.out.println(String.format("%d:%s",number,target));
    }

    /*
    public static void main(String[] args) {
        //键值对
        Jedis jedis = new Jedis();
        jedis.set("oneKey","12");
        print(1,jedis.get("oneKey"));
        print(2,jedis.get("key"));

        //列表
       // print(3,jedis.lrange("list",0,10));
        jedis.del("list");
        jedis.lpush("list","ning");
        print(4,jedis.lrange("list",0,10));
        jedis.lpush("list","1","2","3","4");
        print(5,jedis.lrange("list",0,10));
        jedis.rpush("list","mongodb","mysql");
        print(6,jedis.lrange("list",0,10));
        print(7,jedis.lindex("list",0));

        //集合
        jedis.del("set");
        jedis.del("set2");
        jedis.sadd("set","peter","admin","coco");
        print(8,jedis.scard("set"));
        print(9,jedis.sismember("set","peter"));
        print(10,jedis.sismember("set","lolo"));
        print(11,jedis.smembers("set"));
        jedis.sadd("set2","peter","didi");
        print(12,jedis.smembers("set2"));
        print(13,jedis.sdiff("set","set2"));
        print(14,jedis.sunion("set","set2"));
        jedis.setnx("set","peter");
        print(15,jedis.smembers("set"));
        print(16,  jedis.sinter("set","set2"));

        //哈希
        jedis.del("hash");
        jedis.hset("hash","name","gogo");
        jedis.hset("hash","age","12");
        jedis.hset("hash","name","didi");
        print(17,jedis.hgetAll("hash"));
        print(18,jedis.hexists("hash","name"));
        print(19,jedis.hexists("hash","height"));
        print(20,jedis.hkeys("hash"));
        print(21,jedis.hvals("hash"));
        print(22,jedis.lrange("list",0,10));

        print(23,jedis.brpop(0,"list"));
        print(24,jedis.brpop(0,"list"));
        print(25,jedis.brpop(0,"list"));
        print(26,jedis.brpop(0,"list"));
        print(27,jedis.brpop(0,"list"));
        print(28,jedis.brpop(0,"list"));
        print(29,jedis.brpop(0,"list"));
        print(30,jedis.brpop(0,"list"));


        jedis.setex("coder",10,"1111");
        try {
            Thread.sleep(5000);
            print(22,jedis.get("coder"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        JedisPool pool = new JedisPool("localhost",6379);
        for (int i = 0; i < 100; ++i) {
            Jedis j = pool.getResource();
            j.get("a");
            j.close();
        }


    }
    */
    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool("localhost", 6379);
    }

    public Jedis getJedis()
    {
        return  jedisPool.getResource();
    }


    public String get(String key)
    {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis .get(key);
        }catch (Exception e)
        {
            logger.error("发生异常"+e.getMessage());
            return  null;
        }finally {
            if (jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public void set(String key ,String value)
    {
        Jedis jedis = null;
        try {
            jedis = getJedis();
             jedis .set(key,value);
        }catch (Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public long srem(String skey,String key)
    {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis .srem(skey,key);
        }catch (Exception e)
        {
            logger.error("发生异常"+e.getMessage());
            return  0;
        }finally {
            if (jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public void sadd(String key ,String value)
    {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis .sadd(key,value);
        }catch (Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis!=null)
            {
                jedis.close();
            }
        }
    }


    public boolean sismember(String skey ,String key)
    {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis .sismember(skey,key);
        }catch (Exception e)
        {
            logger.error("发生异常"+e.getMessage());
            return  false;
        }finally {
            if (jedis!=null)
            {
                jedis.close();
            }
        }
    }


    public long scard(String skey )
    {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis .scard(skey);
        }catch (Exception e)
        {
            logger.error("发生异常"+e.getMessage());
            return  0;
        }finally {
            if (jedis!=null)
            {
                jedis.close();
            }
        }
    }

    public void setex(String key, String value) {
        // 验证码, 防机器注册，记录上次注册时间，有效期3天
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.setex(key, 10, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        //阻塞右弹出
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    //序列化
    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));
    }

    //反序列化
    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }





}
