package com.mapnaom.foodreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FoodReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodReservationApplication.class, args);
    }

}
