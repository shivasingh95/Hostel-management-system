package com.hostel.management.repository;

import com.hostel.management.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelRepository extends JpaRepository<Hostel, Long> {
}
