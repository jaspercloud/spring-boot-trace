package io.github.jaspercloud.trace;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class TraceContext {

    /**
     * slf4j透传参数
     */
    private static final String MDCKey = "traceId";

    private static final String TraceAttr = "traceAttr";
    private static final String TraceHeader = "X-Trace-Id";
    private static final ThreadLocal<TraceLog> trace = new InheritableThreadLocal<>();

    public static void init(HttpServletRequest request) {
        TraceLog traceLog = (TraceLog) request.getAttribute(TraceAttr);
        if (null == traceLog) {
            String traceId = request.getHeader(TraceHeader);
            if (StringUtils.isEmpty(traceId)) {
                traceId = ShortUUID.getUUID();
            }
            String spanId = ShortUUID.getUUID();
            traceLog = new TraceLog(traceId, spanId);

            traceLog.setStartTime(System.currentTimeMillis());

            traceLog.setRemoteHost(request.getRemoteHost());
            traceLog.setRemotePort(request.getRemotePort());

            traceLog.setMethod(request.getMethod());
            traceLog.setUrl(request.getRequestURI());
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String key = paramNames.nextElement();
                traceLog.getParams().put(key, request.getParameter(key));
            }
            request.setAttribute(TraceAttr, traceLog);
        }
        trace.set(traceLog);
        MDC.put(MDCKey, traceLog.getTraceId());
    }

    public static TraceLog get() {
        return trace.get();
    }

    public static void remove() {
        trace.remove();
    }
}
