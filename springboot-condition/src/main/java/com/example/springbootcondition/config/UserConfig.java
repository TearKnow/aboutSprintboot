package com.example.springbootcondition.config;

import com.example.springbootcondition.conditino.ClassCondition;
import com.example.springbootcondition.conditino.ConditionOnClass;
import com.example.springbootcondition.domain.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    //@Conditional(ClassCondition.class)

    //@ConditionalOnClass 是 Spring Boot 的亲儿子，开箱即用；@ConditionOnClass 大概率是别人自己写的“仿制品”，需要在你项目里找到它的定义才能用。
    @ConditionOnClass("redis.clients.jedis.Jedis")
    public User user() {
        return new User();
    }

    @Bean
    @ConditionalOnProperty(name = "wholearnjava", havingValue = "jack")
    public User user2(){
        return new User();
    }
}
