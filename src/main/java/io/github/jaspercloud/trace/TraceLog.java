package io.github.jaspercloud.trace;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class TraceLog {

    //trace
    private String traceId;
    private String spanId;

    //req
    private String remoteHost;
    private Integer remotePort;
    private String method;
    private String url;
    private final Map<String, String> params = new LinkedHashMap<>();
    private final Map<String, String> reqHeaders = new LinkedHashMap();
    private Object reqBody;

    //resp
    private int code;
    private final Map<String, String> respHeaders = new LinkedHashMap();
    private Object respBody;
    private Throwable exception;

    //time
    private Long startTime;
    private Long endTime;
    private Long execTime;

    private final List<ItemLog> itemLogList = new CopyOnWriteArrayList<>();

    public TraceLog() {
    }

    public TraceLog(String traceId, String spanId) {
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public void addItemLog(ItemLog itemLog) {
        itemLogList.add(itemLog);
    }

    //get/set

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getReqHeaders() {
        return reqHeaders;
    }

    public Object getReqBody() {
        return reqBody;
    }

    public void setReqBody(Object reqBody) {
        this.reqBody = reqBody;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, String> getRespHeaders() {
        return respHeaders;
    }

    public Object getRespBody() {
        return respBody;
    }

    public void setRespBody(Object respBody) {
        this.respBody = respBody;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public List<ItemLog> getItemLogList() {
        return itemLogList;
    }
}
