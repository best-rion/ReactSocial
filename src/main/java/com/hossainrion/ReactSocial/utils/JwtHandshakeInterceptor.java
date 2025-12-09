package com.hossainrion.ReactSocial.utils;

import com.hossainrion.ReactSocial.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
                                   WebSocketHandler handler, Map<String, Object> attributes) {

        ServletServerHttpRequest servlet = (ServletServerHttpRequest) req;
        String cookie = servlet.getServletRequest().getHeader("Cookie");

        String token = null;
        for (String c : cookie.split(";")) {
            if (c.trim().startsWith("auth-token="))
                token = c.trim().substring("auth-token=".length());
        }

        if (token == null) return false;

        // VALIDATE YOUR JWT HERE
        String username = JwtUtil.getusernameFromToken(token);

        attributes.put("username", username);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
