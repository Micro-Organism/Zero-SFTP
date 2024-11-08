package com.zero.sftp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@EnableIntegration
@SpringBootApplication
public class ZeroSftpBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZeroSftpBootApplication.class, args);
    }

}
