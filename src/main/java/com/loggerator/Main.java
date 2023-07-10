package com.loggerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ServletComponentScan
@SpringBootApplication(scanBasePackages = "com.loggerator")
@EnableCassandraRepositories("com.loggerator.data.repo")
@EntityScan("com.loggerator.data.model")
@EnableWebMvc
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
