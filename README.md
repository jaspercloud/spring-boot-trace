# spring-boot-trace
spring-boot-trace

## demo
```java
@RestController
@RequestMapping("/api")
@SpringBootApplication
public class TraceAppTest {

    public static void main(String[] args) {
        SpringApplication.run(TraceAppTest.class, args);
    }

    @PostMapping("/test")
    public String test(@RequestBody String text) {
        return "test";
    }

    @Configuration
    public static class Config {

        @Bean
        public TraceLogCollector traceLogCollector() {
            return new TraceLogCollector() {
                @Override
                public void collect(TraceLog traceLog) {
                    System.out.println();
                }
            };
        }
    }
}
```
```java
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
```
