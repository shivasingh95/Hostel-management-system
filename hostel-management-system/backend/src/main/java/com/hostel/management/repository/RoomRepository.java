package com.hostel.management.repository;

import com.hostel.management.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // JPQL query: rooms that still have space. Demonstrates writing
    // custom queries beyond what method-name derivation can express.
    @Query("SELECT r FROM Room r WHERE r.occupiedCount < r.capacity")
    List<Room> findAvailableRooms();

    List<Room> findByHostelId(Long hostelId);
}
