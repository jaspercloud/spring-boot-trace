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
