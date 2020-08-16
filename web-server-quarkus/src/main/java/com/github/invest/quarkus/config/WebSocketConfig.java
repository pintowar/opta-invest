//package com.github.invest.quarkus.config;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.websocket.*;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@ServerEndpoint("/chat/{username}")
//@ApplicationScoped
//public class WebSocketConfig {
//
//    Map<String, Session> sessions = new ConcurrentHashMap<>();
//
//    @OnOpen
//    public void onOpen(Session session, @PathParam("username") String username) {
//        sessions.put(username, session);
//        broadcast("User " + username + " joined");
//    }
//
//    @OnClose
//    public void onClose(Session session, @PathParam("username") String username) {
//        sessions.remove(username);
//        broadcast("User " + username + " left");
//    }
//
//    @OnError
//    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
//        sessions.remove(username);
//        broadcast("User " + username + " left on error: " + throwable);
//    }
//
//    @OnMessage
//    public void onMessage(String message, @PathParam("username") String username) {
//        broadcast(">> " + username + ": " + message);
//    }
//
//    private void broadcast(String message) {
//        sessions.values().forEach(s -> {
//            s.getAsyncRemote().sendObject(message, result -> {
//                if (result.getException() != null) {
//                    System.out.println("Unable to send message: " + result.getException());
//                }
//            });
//        });
//    }
//}
