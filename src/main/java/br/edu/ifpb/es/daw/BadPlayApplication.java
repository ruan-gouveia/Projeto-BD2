package br.edu.ifpb.es.daw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BadPlayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BadPlayApplication.class, args);
    }
}