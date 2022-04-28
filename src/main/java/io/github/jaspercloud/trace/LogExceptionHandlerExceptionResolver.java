package io.github.jaspercloud.trace;

import org.springframework.context.ApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;

import static io.github.jaspercloud.trace.LogServletInvocableHandlerMethod.ExInvoke;

public class LogExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {

    @Override
    protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
        ServletInvocableHandlerMethod srcMethod = super.getExceptionHandlerMethod(handlerMethod, exception);
        Object bean = srcMethod.getBean();
        Method method = srcMethod.getMethod();
        ApplicationContext applicationContext = getApplicationContext();
        LogServletInvocableHandlerMethod logMethod = new LogServletInvocableHandlerMethod(bean, method, applicationContext, ExInvoke);
        return logMethod;
    }
}
