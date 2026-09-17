package com.hostel.management.service;

import com.hostel.management.dto.RoomRequest;
import com.hostel.management.dto.RoomResponse;
import com.hostel.management.entity.Hostel;
import com.hostel.management.entity.Room;
import com.hostel.management.exception.ResourceNotFoundException;
import com.hostel.management.repository.HostelRepository;
import com.hostel.management.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Room CRUD + a quick-lookup cache.
 *
 * The `roomCache` below is a direct use of the Java Collections
 * Framework (Unit 4): rather than hitting the DB every time we need
 * to check "is this specific room full right now", we keep a
 * ConcurrentHashMap in memory that mirrors the occupancy count and
 * refresh it on every write. ConcurrentHashMap (not a plain HashMap)
 * because this cache is read/written from multiple request threads
 * concurrently — ties back into the multithreading/concurrency unit.
 */
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final HostelRepository hostelRepository;

    private final Map<Long, Integer> roomCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void warmCache() {
        roomRepository.findAll().forEach(r -> roomCache.put(r.getId(), r.getOccupiedCount()));
    }

    public RoomResponse createRoom(RoomRequest request) {
        Hostel hostel = hostelRepository.findById(request.getHostelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found: " + request.getHostelId()));

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        room.setCapacity(request.getCapacity());
        room.setHostel(hostel);

        Room saved = roomRepository.save(room);
        roomCache.put(saved.getId(), saved.getOccupiedCount());
        return toResponse(saved);
    }

    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RoomResponse> getAvailableRooms() {
        return roomRepository.findAvailableRooms().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long id) {
        return toResponse(findRoomOrThrow(id));
    }

    Room findRoomOrThrow(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found: " + id));
    }

    /** Called by AllocationService whenever occupancy changes, to keep the cache in sync. */
    void syncCache(Room room) {
        roomCache.put(room.getId(), room.getOccupiedCount());
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getFloor(),
                room.getCapacity(),
                room.getOccupiedCount(),
                room.isFull(),
                room.getHostel().getName()
        );
    }
}
