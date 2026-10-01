package com.example.springbootcondition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringbootConditionApplication {

    public static void main(String[] args) {
        //返回IOC容器
        ConfigurableApplicationContext context = SpringApplication.run(SpringbootConditionApplication.class, args);
//        Object redisTemplate = context.getBean("redisTemplate");
//        System.out.println(redisTemplate);

        //需求1. 根据是否有jedis坐标来决定是否加载user bean
        //需求2. 将类的判断定义为动态的

        Object user = context.getBean("user2");
        System.out.println(user);

    }

}
