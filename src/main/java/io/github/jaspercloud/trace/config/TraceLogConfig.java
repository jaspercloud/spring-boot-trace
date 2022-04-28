package io.github.jaspercloud.trace.config;

import io.github.jaspercloud.trace.LogBeanPostProcessor;
import io.github.jaspercloud.trace.LogHandlerInterceptor;
import io.github.jaspercloud.trace.ReqBodyLoader;
import io.github.jaspercloud.trace.TraceLogCollector;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collections;
import java.util.List;

@Configuration
public class TraceLogConfig {

    @Bean
    public BeanPostProcessor logBeanPostProcessor() {
        return new LogBeanPostProcessor();
    }

    @Bean
    public WebMvcConfigurerAdapter logWebMvcConfigurerAdapter(ObjectProvider<List<TraceLogCollector>> provider) {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                List<TraceLogCollector> collectorList = provider.getIfAvailable();
                if (null == collectorList) {
                    collectorList = Collections.EMPTY_LIST;
                }
                registry.addInterceptor(new LogHandlerInterceptor(collectorList));
            }
        };
    }

    @Order(Ordered.LOWEST_PRECEDENCE)
    @Bean
    public ReqBodyLoader reqBodyLoader() {
        return new ReqBodyLoader();
    }
}
