package com.studentreview.server.service;

import com.studentreview.server.dto.PlaceRequest;
import com.studentreview.server.dto.PlaceResponse;
import com.studentreview.server.model.Place;
import com.studentreview.server.model.User;
import com.studentreview.server.repository.PlaceRepository;
import com.studentreview.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OwnerService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PlaceResponse> getOwnerPlaces(String email) {
        return placeRepository.findByOwnerEmail(email).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PlaceResponse createPlace(PlaceRequest request) {
        User owner = userRepository.findByEmail(request.getOwnerEmail())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        if (owner.getRole() != User.UserRole.OWNER) {
            throw new IllegalArgumentException("User is not an owner");
        }

        Place place = new Place();
        place.setName(request.getName());
        place.setDescription(request.getDescription());
        place.setAddress(request.getAddress());
        try {
            place.setType(Place.PlaceType.valueOf(request.getType()));
        } catch (Exception e) {
            place.setType(Place.PlaceType.OTHER);
        }
        place.setOwner(owner);
        place.setIsActive(true); // Auto-approve for demo purposes

        place = placeRepository.save(place);
        return convertToDto(place);
    }

    @Transactional
    public PlaceResponse updatePlace(Long id, PlaceRequest request) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        if (!place.getOwner().getEmail().equals(request.getOwnerEmail())) {
            throw new IllegalArgumentException("You do not own this place");
        }

        place.setName(request.getName());
        place.setDescription(request.getDescription());
        place.setAddress(request.getAddress());
        try {
            place.setType(Place.PlaceType.valueOf(request.getType()));
        } catch (Exception e) {
            // Keep existing type or default to OTHER? Let's default to OTHER if invalid
            place.setType(Place.PlaceType.OTHER);
        }

        place = placeRepository.save(place);
        return convertToDto(place);
    }

    private PlaceResponse convertToDto(Place place) {
        String ownerName = "Unknown";
        if (place.getOwner() != null) {
            ownerName = place.getOwner().getBusinessName();
            if (ownerName == null || ownerName.isEmpty()) {
                ownerName = place.getOwner().getUsername();
            }
        }

        String placeType = place.getType() != null ? place.getType().toString() : "OTHER";

        return new PlaceResponse(
                place.getId(),
                place.getName(),
                place.getDescription(),
                place.getAddress(),
                placeType,
                ownerName,
                0.0, // Default rating
                0    // Default count
        );
    }
}
