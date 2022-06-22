package com.kikop.service;

import com.kikop.constants.CacheConstants;
import com.kikop.constants.DataSerialType;
import com.kikop.handler.server.model.MyChatServerManager;
import com.kikop.handler.server.model.UserSessions;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author kikop
 * @version 1.0
 * @project mychatserver
 * @file TokenService
 * @desc 基于Redis的 token管理服务
 * @date 2021/3/30
 * @time 8:00
 * @by IDE IntelliJ IDEA
 */
@Component
public class ChatServerSessionDao {


    @Autowired
    public RedisService redisService;


    //    private static ISerializer fastJsonSerializer = SerializerManager.getSerializer(DataSerialType.JSON_SERIAL.code());
    private static ISerializer gsonSerializer = SerializerManager.getSerializer(DataSerialType.GSON_SERIAL.code());

//    private static final long CASHE_LONG = 60 * 4;// 用户session缓存时间,4小时


    // cacheUser--> addUserSession
    public void addCacheUserSession(String userId, String sessionId) {
        UserSessions us = get(userId);
        if (null == us) {
            us = new UserSessions(userId);
        }
        // 在原有的session列表中追加一个
        // userId-->userSessions
        // key:sessinId,value:MyChatServerNode
        us.addSession(sessionId, MyChatServerManager.getInst().getMyChatServerNode());
        save(us);
    }

    /**
     * 更新Redis当前用户的会话列表
     *
     * @param uid
     * @param sessionId
     */
    public void removeUserSession(String uid, String sessionId) {
        UserSessions us = get(uid); // fetch data from redis
        if (null == us) {
            us = new UserSessions(uid);
        }
        us.removeSession(sessionId);
        save(us); // 仅仅更新 Redis中 userSessions中的 map
    }

    /**
     * getAllSession
     *
     * @param userId
     * @return
     */
    public UserSessions getAllSession(String userId) {
        UserSessions us = get(userId);
        if (null != us) {
            return us;
        }
        return null;
    }

    /**
     * 获取缓存中的所有clientservermappings
     *
     * @param userId
     * @return
     */
    private UserSessions get(final String userId) {

        String key = CacheConstants.USERSESSIONS_UID_PREFIX + userId;

        String jsonStrForUserSessions = redisService.getCacheObject(key);

        if (!org.springframework.util.StringUtils.isEmpty(jsonStrForUserSessions)) {
            UserSessions userSessions = gsonSerializer.convertToPojo(jsonStrForUserSessions, UserSessions.class);
            return userSessions;
        }
        return null;
    }


    private void save(final UserSessions uss) {
        String key = CacheConstants.USERSESSIONS_UID_PREFIX + uss.getUserId();
        String jsonStrForUserSessions = gsonSerializer.convertToJson(uss);
        redisService.setCacheObject(key, jsonStrForUserSessions, CacheConstants.USERSESSIONS_CASHE_LONG, TimeUnit.MINUTES);
    }

}
