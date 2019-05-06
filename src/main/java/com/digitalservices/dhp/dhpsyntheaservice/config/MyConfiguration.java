/*
        Licensed to the Apache Software Foundation (ASF) under one
        or more contributor license agreements.  See the NOTICE file
        distributed with this work for additional information
        regarding copyright ownership.  The ASF licenses this file
        to you under the Apache License, Version 2.0 (the
        "License"); you may not use this file except in compliance
        with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing,
        software distributed under the License is distributed on an
        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
        KIND, either express or implied.  See the License for the
        specific language governing permissions and limitations
        under the License.
*/

package com.digitalservices.dhp.dhpsyntheaservice.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class MyConfiguration {
    @Value("${origins.url}")
    private String[] url;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/synthea/**").allowedOrigins(url).allowedMethods("GET", "OPTIONS", "POST", "HEAD");
                System.out.println("********************************************************************");
                for (int i = 0; i < url.length; i++){
                    System.out.println(url[i]);
                }
            }
        };
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("Synthea Service API",
                "This API allows you to run Synthea thorough an API.  It also allows you to access the created files and send them to vista.",
                "1.0", null, new Contact("Rob Caruso", "www.perspecta.com", "rob.caruso@persepecta.com"),
                "Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    }

}