package ru.itmo.park;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class ParkApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ParkApplication.class, args);
//        System.in.close();
//        System.out.close();
    }

}
