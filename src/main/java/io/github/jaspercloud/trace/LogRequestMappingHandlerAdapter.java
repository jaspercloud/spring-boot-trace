package io.github.jaspercloud.trace;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import static io.github.jaspercloud.trace.LogServletInvocableHandlerMethod.ReqInvoke;

public class LogRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected ServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        return new LogServletInvocableHandlerMethod(handlerMethod, ReqInvoke);
    }
}
