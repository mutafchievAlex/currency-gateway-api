package com.example.gateway.api.config;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

@Component
public class JaxRsResponseReturnValueHandler implements HandlerMethodReturnValueHandler, Ordered {

    private final List<HttpMessageConverter<?>> messageConverters;

    public JaxRsResponseReturnValueHandler(List<HttpMessageConverter<?>> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Response.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        Response response = (Response) returnValue;
        HttpServletResponse servletResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        if (servletResponse == null) {
            return;
        }

        servletResponse.setStatus(response.getStatus());
        response.getHeaders().forEach((name, values) -> values.forEach(value -> servletResponse.addHeader(name, value)));

        Object entity = response.getEntity();
        if (entity == null) {
            return;
        }

        MediaType mediaType = null;
        if (response.getMediaType() != null) {
            mediaType = MediaType.parseMediaType(response.getMediaType().toString());
            if (servletResponse.getContentType() == null) {
                servletResponse.setContentType(mediaType.toString());
            }
        }

        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(servletResponse);
        for (HttpMessageConverter<?> converter : messageConverters) {
            if (converter.canWrite(entity.getClass(), mediaType)) {
                ((HttpMessageConverter<Object>) converter).write(entity, mediaType, outputMessage);
                return;
            }
        }
        throw new HttpMessageNotWritableException("No HttpMessageConverter for " + entity.getClass());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
