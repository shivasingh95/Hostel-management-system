package com.hostel.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNumber;

    private int floor;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int occupiedCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel;

    /**
     * Simple domain method — keeps business logic close to the data it
     * operates on rather than scattering "is this room full" checks
     * across services. This is basic encapsulation (Unit 2).
     */
    public boolean isFull() {
        return occupiedCount >= capacity;
    }
}
