package org.example.springanno.config;

import org.example.springanno.pojo.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

public class MyConfig {
    @Bean
    public User getUser() {
        return new User();
    }
}
