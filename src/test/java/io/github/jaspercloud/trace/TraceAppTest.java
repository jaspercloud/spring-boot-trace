package io.github.jaspercloud.trace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
