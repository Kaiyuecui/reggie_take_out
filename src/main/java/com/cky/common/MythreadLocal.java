package com.cky.common;

/**
 * @ClassName MythreadLocal
 * @Description TODO
 * @Author lukcy
 * @Date 2024/6/25 9:21
 * @Version 1.0
 */

/**
 * 线程工具类
 */
public class MythreadLocal {
    //因为存放的是id 所以是lONG
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrendId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrendId(){
        return threadLocal.get();
    }
}
