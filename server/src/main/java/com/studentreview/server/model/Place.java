package com.studentreview.server.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private PlaceType type = PlaceType.OTHER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "favoritePlaces", fetch = FetchType.LAZY)
    private java.util.Set<User> favoritedBy = new java.util.HashSet<>();

    @Column(nullable = false)
    private Boolean isActive = false; // Places might need approval

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum PlaceType {
        CAFE,
        STUDY_HUB,
        CLUB,
        RESTAURANT,
        PUB,
        OTHER
    }
}
