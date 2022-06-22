package com.kikop.handler.server.model;

import com.kikop.handler.server.session.impl.LocalSessionImpl;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
@Data
public class UserSessions {

    private String userId;

    // key:sessionId
    // value:MyChatServerNode
    private Map<String, MyChatServerNode> currentSession2ServerNodeMmap = new LinkedHashMap<>(10);

    public UserSessions(String userId) {
        this.userId = userId;
    }

    public void addSession(String sessionId, MyChatServerNode node) {
        currentSession2ServerNodeMmap.put(sessionId, node);
    }

    public void removeSession(String sessionId) {
        currentSession2ServerNodeMmap.remove(sessionId);
    }


    public void addLocalSession(LocalSessionImpl session) {
        currentSession2ServerNodeMmap.put(session.getSessionId(), MyChatServerManager.getInst().getMyChatServerNode());
    }
}
