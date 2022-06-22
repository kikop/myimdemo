//package com.kikop.handler.server.session.impl;
//
//import com.crazymakercircle.entity.ImNode;
//import com.crazymakercircle.imServer.distributed.PeerSender;
//import com.crazymakercircle.imServer.distributed.WorkerRouter;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//
//import java.io.Serializable;
//
///**
// * create by 尼恩 @ 疯狂创客圈
// **/
//@Data
//@Builder
//@AllArgsConstructor
//public class RemoteSessionImpl implements ServerSession, Serializable {
//    private static final long serialVersionUID = -400010884211394846L;
//
//    private String userId;
//    private String sessionId;
//    private ImNode imNode;
//    private boolean valid= true;
//
//    public RemoteSessionImpl() {
//        userId = "";
//        sessionId = "";
//        imNode = new ImNode("unKnown", 0);
//    }
//
//    public RemoteSessionImpl(
//            String sessionId, String userId, ImNode imNode) {
//        this.sessionId = sessionId;
//        this.userId = userId;
//        this.imNode = imNode;
//    }
//
//    @Override
//    public void writeAndFlush(Object pkg) {
//
//        long nodeId = imNode.getId();
//        PeerSender sender =
//                WorkerRouter.getInst().getPeerSender(nodeId);
//
//        sender.writeAndFlush(pkg);
//    }
//
//    @Override
//    public boolean isValid() {
//        return valid;
//    }
//
//    public void setValid(boolean valid) {
//        this.valid = valid;
//    }
//
//}
