package com.studentreview.server.repository;

import com.studentreview.server.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByOwnerEmail(String email);
    List<Place> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}
