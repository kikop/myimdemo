package com.kikop.web;

import com.alibaba.fastjson.JSONObject;
import com.kikop.concurrent.MyFutureTaskSchedulerThread;
import com.kikop.constants.CacheConstants;
import com.kikop.constants.CommonConstants;
import com.kikop.constants.DataSerialType;
import com.kikop.constants.ReqType;
import com.kikop.core.datasturct.RpcProtocol;
import com.kikop.domain.R;
import com.kikop.dto.LoginUserConvert;
import com.kikop.dto.po.LoginUser;
import com.kikop.dto.vo.LoginUserVo;
import com.kikop.handler.server.model.MyChatServerNode;
import com.kikop.handler.server.model.UserSessions;
import com.kikop.routestategy.ILoadBalance;
import com.kikop.security.SecurityAuthUtils;
import com.kikop.security.SecurityUtils;
import com.kikop.serial.ISerializer;
import com.kikop.serial.SerializerManager;
import com.kikop.service.RedisService;
import com.kikop.service.RedisTokenService;
import com.kikop.servicediscovery.IServiceDiscovery;
import com.kikop.utils.JwtUtils;
import com.kikop.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author kikop
 * @version 1.0
 * @project mygatewaryserver
 * @file GatewayController
 * @desc
 * @date 2020/8/30
 * @time 10:16
 * @by IDE: IntelliJ IDEA
 */
@Slf4j
@RestController
@RequestMapping(value = "gatewayc")
public class GatewayController {

    private static final long TOKEN_USER_SECONDS_TIMEOUT = 30;

    private static final String LOCK_HOTKEY_CREATE_PREFIX = "lock:hotkey:create:"; // Hash
    private static final String TOKEN_USER_CREATE_PREFIX = "token:user:create:";


    // Redis 服务工具类
    @Autowired
    public RedisService redisService;

    // 单机分布式锁
    @Autowired
    public Redisson redisson;


    // RestTemplate
    // Restful短连接
    private RestTemplate authRestTemplate;


    @Value("${mygw.zk.childChatServiceNamePathPrefix}")
    private String childChatServiceNamePathPrefix;

    @Value("${mygw.auth.url}")
    private String authUrl;

    @Autowired
    private ILoadBalance iLoadBalance;

    @Autowired
    private RedisTokenService tokenService;

    @Autowired
    private IServiceDiscovery iServiceDiscovery;

    private static ISerializer gsonSerializer = SerializerManager.getSerializer(DataSerialType.GSON_SERIAL.code());

    @PostConstruct
    public void init() {
        authRestTemplate = new RestTemplate();
    }

    @RequestMapping(value = "userLogin", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject userLogin(@RequestBody JSONObject reqParam) {

        JSONObject result = new JSONObject();
        result.put("success", false);
        try {

            // 1.获取请求参数
            log.info("获取请求参数");
            String userId = reqParam.getString("userId");
            String accessToken = reqParam.getString("accessToken");

            // 2.用户认证
            // 根据 userToken 先验证Redis缓存中用户是否已经认证登录
            // key:myimdemo_auth:jwttoken:
            LoginUser loginUserCache = tokenService.getLoginUser(accessToken);
            if (null != loginUserCache) {
                // 3.1.走缓存
                log.info("用户认证,走缓存");

                LoginUserVo loginUserVo = LoginUserConvert.convert2LoginUserVo(loginUserCache);
                result.put("loginUserVo", loginUserVo);
            } else {
                log.info("获取用户信息及权限,得到 loginUser");

                // 3.2.获取用户信息及权限,得到 loginUser
                JSONObject userInfoResponse = authRestTemplate.postForObject(authUrl + "/authc/userLogin", reqParam, JSONObject.class);
                LoginUser loginUser = userInfoResponse.getObject("loginUser", LoginUser.class);

                if (null == userInfoResponse || userInfoResponse.getBoolean("success") == false) {
                    log.error("用户认证失败,用户信息不存在");
                    return result;
                }
                // 3.3.记录认证的登录的用户信息,生成 userJwtToken并缓存
                // by ds for String
                // key:myimdemo_auth:jwttoken: + jwtTokeen
                // Eg:myimdemo_auth:jwttoken:53b44661-93b6-4dc7-b197-c4524820b1f7
                // value:loginUser json序列化对象
                log.info("记录认证的登录的用户信息,生成 userJwtToken并缓存");

                Map<String, Object> tokenMap = tokenService.createToken(loginUser);
                LoginUserVo loginUserVo = LoginUserConvert.convert2LoginUserVo(loginUser);
                result.put("loginUserVo", loginUserVo);
            }

            // 4.与zk通信,轮询选择可选的 nettyServer
            log.info("与zk通信,轮询选择可选的 nettyServer");
            List<MyChatServerNode> avaliableChatServiceList = iServiceDiscovery.getAvaliableServiceNodeList(childChatServiceNamePathPrefix, MyChatServerNode.class);
            if (avaliableChatServiceList == null || avaliableChatServiceList.size() <= 0) {
                log.error("无可用的通信服务器,请检查!");
                return result;
            }
            // 5.清理 Redis 中无效的在线session
            log.info("清理 Redis 中无效的在线session");

            cleanRedisUnUsedClientSession(userId, avaliableChatServiceList);
            log.info("selectAavaliableServer");

            MyChatServerNode myChatServerNode = iLoadBalance.selectAavaliableServer(avaliableChatServiceList);
            // String selectedServerInfo = "127.0.0.1:9091:8081";
            result.put(CommonConstants.nettyServerServiceName, String.format("%s:%d:%d",
                    myChatServerNode.getHost(), myChatServerNode.getHttpport(), myChatServerNode.getPort()));

            result.put("success", true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 返回
        return result;
    }


    /**
     * 接收终端请求
     *
     * @param requestRpcProtocol
     * @return
     */
    @RequestMapping(value = "recvRequestFromClient", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONObject recvClientRequest(@RequestBody RpcProtocol requestRpcProtocol) {


        JSONObject result = new JSONObject();
        result.put("success", false);
        try {
            String toUserId = null;
            // 1.得到对端 userId
            ReqType chatType = ReqType.findByCode(requestRpcProtocol.getHeader().getReqType());
            if (chatType == ReqType.CHAT) {
                LinkedHashMap userChat = (LinkedHashMap) requestRpcProtocol.getContent();
                toUserId = (String) userChat.get("toUserId");
                log.info("转发往客户端 {} 的聊天消息:", toUserId);
            } else if (chatType == ReqType.CHAT_RESP) {
                LinkedHashMap userChat = (LinkedHashMap) requestRpcProtocol.getContent();
                toUserId = (String) userChat.get("toUserId");
                log.info("转发往客户端 {} 的聊天响应消息:", toUserId);
            }
            if (toUserId == null) {
                log.error("目的地址:toUserId is null");
                return result;
            }

            // 2.清理 Redis 中无效的在线session
            cleanRedisUnUsedClientSession(toUserId, null);

            // 3.根据 toUserID从Redis里面读取合适的 nettyServer
            // 3.1.clientId与 netty server的对应关系  by String ds
            // key:chatclient2servermapping:1000
            // value:127.0.0.1:9091:8081
            try {
                String jsonStrForUserSessions = redisService.getCacheObject(CacheConstants.USERSESSIONS_UID_PREFIX + toUserId);
                if (org.springframework.util.StringUtils.isEmpty(jsonStrForUserSessions)) {
                    log.info("发往 {} 的消息已经转离线存储:{}", toUserId);
                    return result;
                }
                // 消息异步发送
                UserSessions userSessions = gsonSerializer.convertToPojo(jsonStrForUserSessions, UserSessions.class);
                MyFutureTaskSchedulerThread.addTask(() -> userSessions.getCurrentSession2ServerNodeMmap().values().stream().forEach(myChatServerNode -> {
                    String serverUrl = String.format("http://%s:%d", myChatServerNode.getHost(), myChatServerNode.getHttpport());
                    // 耗时操作,rest调用 nettyServer
                    authRestTemplate.postForObject(serverUrl + "/mychatserver/chatserverc/recvGatewayDispatchRequest",
                            requestRpcProtocol, JSONObject.class);
                }));

            } catch (Exception ex) {
                ex.printStackTrace();
                log.error("recvRequestFromClient 运行异常", ex);
            }
            result.put("success", true);

        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("recvRequestFromClient 运行异常", ex);
        }
        return result;
    }


    /**
     * 清理 Redis 中无效的在线session
     *
     * @param userId
     * @param avaliableChatServiceList
     */
    private void cleanRedisUnUsedClientSession(String userId, List<MyChatServerNode> avaliableChatServiceList) {
        try {

            String userSessionKey = CacheConstants.USERSESSIONS_UID_PREFIX + userId;
            // 1.获取当前用户的在线会话列表
            String jsonStrForUserSessions = redisService.getCacheObject(userSessionKey);
            if (org.springframework.util.StringUtils.isEmpty(jsonStrForUserSessions)) {
                return;
            }
            // 2.与zk通信,选择可选的 nettyServer
            if (avaliableChatServiceList == null) {
                avaliableChatServiceList = iServiceDiscovery.getAvaliableServiceNodeList(childChatServiceNamePathPrefix, MyChatServerNode.class);
                if (avaliableChatServiceList == null || avaliableChatServiceList.size() <= 0) {
                    log.error("无可用的通信服务器,请检查!");
                    // 5.更新Redis无效的session
                    String jsonStrForUserSessionsNew = gsonSerializer.convertToJson(new UserSessions(userId));
                    redisService.setCacheObject(userSessionKey, jsonStrForUserSessionsNew, CacheConstants.USERSESSIONS_CASHE_LONG, TimeUnit.MINUTES);
                    return;
                }
            }

            // 3.管理无效的sessionId
            // 3.1.取取出无效的sessionID
            UserSessions userSessions = gsonSerializer.convertToPojo(jsonStrForUserSessions, UserSessions.class);
            Set<String> ununsedClientSessionId = new HashSet<String>();
            Iterator<String> iterator = userSessions.getCurrentSession2ServerNodeMmap().keySet().iterator();
            while (iterator.hasNext()) {
                String sessionId = iterator.next();
                MyChatServerNode myChatServerNode = userSessions.getCurrentSession2ServerNodeMmap().get(sessionId);
                boolean isValid = false;
                for (int i = 0; i < avaliableChatServiceList.size(); i++) {
                    if (myChatServerNode.equals(avaliableChatServiceList.get(i))) {
                        isValid = true;
                        break;
                    }
                }
                if (!isValid) {
                    ununsedClientSessionId.add(sessionId);
                }
            }
            // 3.2.删除无效的sessionID
            ununsedClientSessionId.stream().forEach(sessionId -> {
                userSessions.removeSession(sessionId);
            });

            // 4.更新Redis无效的session
            String jsonStrForUserSessionsNew = gsonSerializer.convertToJson(userSessions);
            redisService.setCacheObject(userSessionKey, jsonStrForUserSessionsNew, CacheConstants.USERSESSIONS_CASHE_LONG, TimeUnit.MINUTES);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("cleanRedisUnUsedClientSession 出错:{}", ex.getMessage());
        }
    }


    /**
     * http://localhost:7080/mygatewayserver/gatewayc/insertRedisKey?key=kikop&value=36
     *
     * @param key
     * @param valueXXX
     * @return
     */
    @GetMapping(value = "testInsertRedisKey")
    @ResponseBody
    // @RequestParam:进行参数名称转换,value转换为:valueXXX
    public JSONObject testInsertRedisKey(String key, @RequestParam(value = "value") String valueXXX) {
        RLock rLock = redisson.getLock(LOCK_HOTKEY_CREATE_PREFIX + key);
        JSONObject result = new JSONObject();
        result.put("success", true);
        try {

            // 1.加锁
            // 1.1.这里带时间,获取到锁后,到期锁自动释放,不会执行看门狗
            // long leaseTime = 5;
            // rLock.lock(leaseTime, TimeUnit.SECONDS);

            // 1.1.这里不带时间,获取到锁后,到期锁不会自动释放,会执行看门狗,会自动续命
            rLock.lock();
            // 模拟lock耗时
            TimeUnit.SECONDS.sleep(60);

            // 2.无限制等待
            // 获取到的 token写入 redis缓存
            redisService.setCacheObject(TOKEN_USER_CREATE_PREFIX + key, valueXXX,
                    TOKEN_USER_SECONDS_TIMEOUT, TimeUnit.SECONDS);

        } catch (Exception ex) {
            ex.printStackTrace();
            result.put("success", false);
        } finally {
            rLock.unlock();
        }
        return result;
    }


    @DeleteMapping("userLogout")
    public JSONObject userLogout(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.getUserName(token);

            // 1.删除用户缓存记录
            SecurityAuthUtils.logoutByToken(token); // delLoginUser

            // 2.记录用户退出日志
//            sysLoginService.logout(username);
        }
        return result;
    }

    @PostMapping("userRefresh")
    public R<?> refresh(HttpServletRequest request) {

//        LoginUser loginUser = tokenService.getLoginUser(request);
//        if (StringUtils.isNotNull(loginUser)) {
//            // 刷新令牌有效期
//            tokenService.refreshToken(loginUser);
//            // data:null;msg:null
//            return R.ok();
//        }
        return R.ok();
    }
}