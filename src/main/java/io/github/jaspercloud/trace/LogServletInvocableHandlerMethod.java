package io.github.jaspercloud.trace;

import org.springframework.context.MessageSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.Objects;

public class LogServletInvocableHandlerMethod extends ServletInvocableHandlerMethod {

    //请求调用
    public static final int ReqInvoke = 1;
    //异常处理调用
    public static final int ExInvoke = 2;

    private Integer type;

    public LogServletInvocableHandlerMethod(Object handler, Method method, Integer type) {
        super(handler, method);
        this.type = type;
    }

    public LogServletInvocableHandlerMethod(Object handler, Method method, MessageSource messageSource, Integer type) {
        super(handler, method, messageSource);
        this.type = type;
    }

    public LogServletInvocableHandlerMethod(HandlerMethod handlerMethod, Integer type) {
        super(handlerMethod);
        this.type = type;
    }

    @Override
    protected Object doInvoke(Object... args) throws Exception {
        TraceLog traceLog = TraceContext.get();
        Method bridgedMethod = getBridgedMethod();
        if (Objects.equals(ReqInvoke, type)) {
            Object arg = ReqBodyLoader.load(bridgedMethod, args);
            traceLog.setReqBody(arg);
        }
        try {
            Object ret = super.doInvoke(args);
            traceLog.setRespBody(ret);
            traceLog.setException(null);
            return ret;
        } catch (Throwable e) {
            traceLog.setRespBody(null);
            traceLog.setException(e);
            throw e;
        }
    }
}
