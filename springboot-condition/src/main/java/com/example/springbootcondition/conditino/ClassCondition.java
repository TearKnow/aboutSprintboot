package com.example.springbootcondition.conditino;


import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;


public class ClassCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        //需求1. 导入Jedis坐标后 创建bean
//        boolean flag = true;
//        try {
//            Class<?> cls = Class.forName("redis.clients.jedis.Jedis");
//        } catch (ClassNotFoundException e) {
//            flag = false;
//        }
//        return flag;

        //需求2. 通过注解属性值value指定坐标后创建bean
        Map<String, @Nullable Object> map = metadata.getAnnotationAttributes(ConditionOnClass.class.getName());
        String[] value = (String[]) map.get("value");

        boolean flag = true;
        try {
            for (String className : value){
                Class<?> cls = Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            flag = false;
        }
        return flag;
    }
}
