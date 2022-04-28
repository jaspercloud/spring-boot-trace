package io.github.jaspercloud.trace;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class LogBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (RequestMappingHandlerAdapter.class.isInstance(bean)) {
                return processBeforeRequestMappingHandlerAdapter(bean);
            } else if (HandlerExceptionResolverComposite.class.isInstance(bean)) {
                return processBeforeHandlerExceptionResolverComposite(bean);
            } else {
                return bean;
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (RequestMappingHandlerAdapter.class.isInstance(bean)) {
                return processAfterRequestMappingHandlerAdapter(bean);
            } else {
                return bean;
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Object processBeforeRequestMappingHandlerAdapter(Object bean) {
        LogRequestMappingHandlerAdapter adapter = new LogRequestMappingHandlerAdapter();
        copyFields(RequestMappingHandlerAdapter.class, bean, adapter);
        return adapter;
    }

    private Object processBeforeHandlerExceptionResolverComposite(Object bean) throws Exception {
        HandlerExceptionResolverComposite resolverComposite = (HandlerExceptionResolverComposite) bean;
        Field resolversField = HandlerExceptionResolverComposite.class.getDeclaredField("resolvers");
        resolversField.setAccessible(true);
        List<HandlerExceptionResolver> exceptionResolvers = (List<HandlerExceptionResolver>) resolversField.get(resolverComposite);
        for (int i = 0; i < exceptionResolvers.size(); i++) {
            HandlerExceptionResolver resolver = exceptionResolvers.get(i);
            if (ExceptionHandlerExceptionResolver.class.isInstance(resolver)) {
                LogExceptionHandlerExceptionResolver exceptionResolver = new LogExceptionHandlerExceptionResolver();
                copyFields(ExceptionHandlerExceptionResolver.class, resolver, exceptionResolver);
                exceptionResolvers.set(i, exceptionResolver);
            }
        }
        return resolverComposite;
    }

    private Object processAfterRequestMappingHandlerAdapter(Object bean) throws Exception {
        RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
        List<HandlerMethodReturnValueHandler> list = adapter.getReturnValueHandlers();
        HandlerMethodReturnValueHandlerComposite returnValueHandlers = new HandlerMethodReturnValueHandlerComposite() {
            @Override
            public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
                TraceLog traceLog = TraceContext.get();
                traceLog.setRespBody(returnValue);
                traceLog.setException(null);
                super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            }
        };
        returnValueHandlers.addHandlers(list);
        Field field = RequestMappingHandlerAdapter.class.getDeclaredField("returnValueHandlers");
        field.setAccessible(true);
        field.set(adapter, returnValueHandlers);
        return adapter;
    }

    private void copyFields(Class<?> clazz, Object src, Object dest) {
        ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                    return;
                }
                field.setAccessible(true);
                field.set(dest, field.get(src));
            }
        });
    }
}
