package com.lsh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: Joey
 * @Date: 2023/8/8 15:30
 * @Desc:
 */
@Slf4j
@SpringBootApplication
public class ElasticsearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class,args);
        log.info("Elasticsearch Demo StartUp!");
    }
}
