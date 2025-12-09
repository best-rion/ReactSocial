package com.hossainrion.ReactSocial.configuration;

import com.hossainrion.ReactSocial.utils.CustomHandler;
import com.hossainrion.ReactSocial.utils.JwtHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CustomHandler(), "/ws-chat")
                .setAllowedOrigins("http://localhost:5173")
                .addInterceptors(new JwtHandshakeInterceptor());
    }
}

