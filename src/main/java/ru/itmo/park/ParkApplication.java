package ru.itmo.park;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParkApplication {

    public static void main(String[] args){
        SpringApplication.run(ParkApplication.class, args);
//        System.in.close();
//        System.out.close();
    }

}
