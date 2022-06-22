package com.kikop.handler.server.model;

public interface IChatServerSessionCallback {

    void addLocalSessionImpl(String userId, String sessionId);

    void removeLocalSessionImpl(String userId, String sessionId);

    UserSessions getAllSession(String userId);
}
