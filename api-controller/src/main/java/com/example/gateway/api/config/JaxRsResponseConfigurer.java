package com.example.gateway.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class JaxRsResponseConfigurer implements WebMvcConfigurer {

    private final JaxRsResponseReturnValueHandler handler;

    public JaxRsResponseConfigurer(JaxRsResponseReturnValueHandler handler) {
        this.handler = handler;
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        handlers.add(0, handler);
    }
}
