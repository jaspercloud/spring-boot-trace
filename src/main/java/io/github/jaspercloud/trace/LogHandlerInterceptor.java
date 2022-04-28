package io.github.jaspercloud.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LogHandlerInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private List<TraceLogCollector> logCollectors;

    public LogHandlerInterceptor(List<TraceLogCollector> logCollectors) {
        this.logCollectors = logCollectors;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TraceContext.init(request);
        TraceLog traceLog = TraceContext.get();
        Map<String, String> headers = parseHeaders(request.getHeaderNames(), key -> request.getHeader(key));
        traceLog.getReqHeaders().putAll(headers);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            TraceLog traceLog = TraceContext.get();
            if (null != ex) {
                return;
            }
            traceLog.setCode(response.getStatus());
            Enumeration enumeration = new IteratorEnumeration(response.getHeaderNames().iterator());
            Map<String, String> headers = parseHeaders(enumeration, key -> response.getHeader(key));
            traceLog.getRespHeaders().putAll(headers);
            Object respBody = traceLog.getRespBody();
            if (ResponseEntity.class.isInstance(respBody)) {
                ResponseEntity entity = (ResponseEntity) respBody;
                traceLog.setCode(entity.getStatusCodeValue());
                traceLog.getRespHeaders().putAll(entity.getHeaders().toSingleValueMap());
                traceLog.setRespBody(entity.getBody());
            }
            traceLog.setEndTime(System.currentTimeMillis());
            traceLog.setExecTime(traceLog.getEndTime() - traceLog.getStartTime());
            for (TraceLogCollector collector : logCollectors) {
                try {
                    collector.collect(traceLog);
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } finally {
            TraceContext.remove();
        }
    }

    private Map<String, String> parseHeaders(Enumeration<String> headerNames, Function<String, String> func) {
        Map<String, String> headers = new LinkedHashMap<>();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            headers.put(key, func.apply(key));
        }
        return headers;
    }

    private static class IteratorEnumeration<E> implements Enumeration<E> {

        private Iterator<E> iterator;

        public IteratorEnumeration(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public E nextElement() {
            return iterator.next();
        }
    }

}
