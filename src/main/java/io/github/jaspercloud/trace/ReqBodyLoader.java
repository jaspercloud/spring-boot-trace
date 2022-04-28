package io.github.jaspercloud.trace;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReqBodyLoader implements ApplicationContextAware {

    private static Map<Method, ReqBodyLoadTemplate> reqBodyLoadMap = new HashMap<>();

    public static Object load(Method method, Object[] args) {
        ReqBodyLoadTemplate template = reqBodyLoadMap.get(method);
        if (null == template) {
            return null;
        }
        Object arg = template.load(args);
        return arg;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Collection<Object> values = applicationContext.getBeansOfType(Object.class).values();
        for (Object bean : values) {
            Class<?> userClass = ClassUtils.getUserClass(bean);
            boolean isHandler = AnnotatedElementUtils.hasAnnotation(userClass, Controller.class) ||
                    AnnotatedElementUtils.hasAnnotation(userClass, RequestMapping.class);
            if (!isHandler) {
                continue;
            }
            Method[] methods = ReflectionUtils.getAllDeclaredMethods(userClass);
            for (Method method : methods) {
                boolean isMapping = AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class);
                if (!isMapping) {
                    continue;
                }
                Integer index = findReqBodyIndex(method);
                if (null == index) {
                    continue;
                }
                reqBodyLoadMap.put(method, new ReqBodyLoadTemplate(index));
            }
        }
    }

    private Integer findReqBodyIndex(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] argAnnotations = parameterAnnotations[i];
            for (int j = 0; j < argAnnotations.length; j++) {
                Annotation annotation = argAnnotations[j];
                if (RequestBody.class.isInstance(annotation)) {
                    return i;
                }
            }
        }
        return null;
    }

    private static class ReqBodyLoadTemplate {

        private int index;

        public ReqBodyLoadTemplate(int index) {
            this.index = index;
        }

        public Object load(Object[] args) {
            return args[index];
        }
    }
}
