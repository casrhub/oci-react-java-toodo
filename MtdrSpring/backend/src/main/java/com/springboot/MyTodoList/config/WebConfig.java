package com.springboot.MyTodoList.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
                // Forward everything except static files and API endpoints to index.html
                registry.addViewController("/{spring:[a-zA-Z0-9\\-_]+}")
                                .setViewName("forward:/index.html");
                registry.addViewController("/**/{spring:[a-zA-Z0-9\\-_]+}")
                                .setViewName("forward:/index.html");
                registry.addViewController("/{spring:[a-zA-Z0-9\\-_]+}/**/{spring:[a-zA-Z0-9\\-_]+}")
                                .setViewName("forward:/index.html");
        }
}
