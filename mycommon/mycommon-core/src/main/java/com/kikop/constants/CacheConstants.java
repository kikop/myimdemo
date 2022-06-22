package com.kikop.constants;

/**
 * 缓存的key 常量
 *
 * @author ruoyi
 */
public class CacheConstants {

    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION_TIME = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;


    public static final long MILLIS_SECOND = 1000;

    public static final long MILLIS_MINUTE = 60 * CacheConstants.MILLIS_SECOND;

    public final static Long MILLIS_MINUTE_TEN = CacheConstants.REFRESH_TIME * CacheConstants.MILLIS_MINUTE;


    // 用户session缓存时间,4小时
    public static final long USERSESSIONS_CASHE_LONG = 60 * 4;
    // ClientID-->ServerId的映射关系前缀(数据结构:String)
    public static final String USERSESSIONS_UID_PREFIX = "myimdemo_usersessions:userid:";

    /**
     * 权限缓存前缀,缓存时间:12小时
     */
    public final static String LOGIN_TOKEN_KEY = "myimdemo_auth:jwttoken:";


}
