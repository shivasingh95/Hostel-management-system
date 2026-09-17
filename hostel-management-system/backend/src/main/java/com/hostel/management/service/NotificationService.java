package com.hostel.management.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Fire-and-forget notifications. Because these methods are @Async and
 * run on the "notificationExecutor" thread pool (see AsyncConfig), the
 * calling thread (the HTTP request thread) returns to the client
 * immediately instead of blocking while a "notification" is sent.
 *
 * In a real deployment this would call an email/SMS provider; here it
 * simulates the delay so you can see the async behavior in the logs.
 */
@Service
public class NotificationService {

    @Async("notificationExecutor")
    public void notifyComplaintStatusChange(String studentEmail, String roomNumber, String newStatus) {
        try {
            System.out.printf("[%s] Sending notification to %s: complaint for room %s is now %s%n",
                    Thread.currentThread().getName(), studentEmail, roomNumber, newStatus);
            Thread.sleep(1500); // simulate network latency to an email/SMS provider
            System.out.printf("[%s] Notification sent to %s%n", Thread.currentThread().getName(), studentEmail);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
