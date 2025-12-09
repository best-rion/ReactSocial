package com.hossainrion.ReactSocial.configuration;

import com.hossainrion.ReactSocial.utils.CustomHandler;
import com.hossainrion.ReactSocial.utils.JwtHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final CustomHandler customHandler;
    WebSocketConfiguration(CustomHandler customHandler) {
        this.customHandler = customHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customHandler, "/ws-chat")
                .setAllowedOrigins("http://localhost:5173")
                .addInterceptors(new JwtHandshakeInterceptor());
    }
}

