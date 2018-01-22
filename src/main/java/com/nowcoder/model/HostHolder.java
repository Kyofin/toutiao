package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * 每个请求线程各自在自己的线程保存各自登录的用户状态
 */
@Component
public class HostHolder {
    private static  ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();

    public User getUser()
    {
       return   userThreadLocal.get();
    }

    public void  setUser(User savedUser)
    {
        userThreadLocal.set(savedUser);
    }

    public void  clear()
    {
        userThreadLocal.remove();
    }
}

