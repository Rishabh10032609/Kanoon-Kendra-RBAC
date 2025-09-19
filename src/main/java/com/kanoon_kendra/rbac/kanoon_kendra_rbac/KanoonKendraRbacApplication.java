package com.kanoon_kendra.rbac.kanoon_kendra_rbac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KanoonKendraRbacApplication {

    public static void main(String[] args) {
        SpringApplication.run(KanoonKendraRbacApplication.class, args);
    }

}
