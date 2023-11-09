package com.deer.agent.sandbox.matcher;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SpringControllerMatcher implements ClassAndMethodMatcher {
    public static final String CONTROLLER_ANNOTATION = "org.springframework.stereotype.Controller";
    public static final String REST_CONTROLLER_ANNOTATION = "org.springframework.web.bind.annotation.RestController";

    private static Map<String, Boolean> matchedClazzMap = new ConcurrentHashMap<>();

    @Override
    public boolean match(Class<?> clazz, String clazzName, String methodName, String methodDesc) {
        Boolean result = matchedClazzMap.get(clazzName);
        if (result != null) {
            return result;
        }
        //判断类是否有对应的注解
        Annotation[] annotations = clazz.getAnnotations();
        Optional<Annotation> controllerAnnotation = Arrays.stream(annotations).filter(annotation ->
        {
            String fullClazzName = annotation.getClass().getCanonicalName();
            return Objects.equals(CONTROLLER_ANNOTATION, fullClazzName) || Objects.equals(REST_CONTROLLER_ANNOTATION, fullClazzName);
        }).findFirst();
        boolean matched = controllerAnnotation.isPresent();
        if (matched) {
            matchedClazzMap.put(clazzName, Boolean.TRUE);
            System.out.println("filter matched :" + clazzName);
        } else {
            matchedClazzMap.put(clazzName, Boolean.FALSE);
        }
        return matched;
    }
}
