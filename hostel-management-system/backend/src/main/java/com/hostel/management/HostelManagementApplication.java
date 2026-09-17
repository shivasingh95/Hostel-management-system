package com.hostel.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Entry point for the Hostel Room Booking & Management System.
 *
 * @EnableAsync turns on Spring's async task execution, which we use later
 * for the complaint-notification service (this is our multithreading
 * requirement from Unit 3 of the syllabus, done the Spring way instead of
 * raw Thread/Runnable).
 */
@SpringBootApplication
@EnableAsync
public class HostelManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(HostelManagementApplication.class, args);
    }
}
