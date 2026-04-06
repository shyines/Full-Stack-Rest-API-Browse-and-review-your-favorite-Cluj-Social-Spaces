package com.studentreview.server.bootstrap;

import com.studentreview.server.model.Place;
import com.studentreview.server.model.User;
import com.studentreview.server.repository.PlaceRepository;
import com.studentreview.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin exists to decide if we need to seed standard data
        if (!userRepository.existsByEmail("admin@test.com")) {
            System.out.println("Standard data missing. Seeding database...");

            // 1. Create Admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@test.com");
            admin.setPasswordHash("admin123"); 
            admin.setRole(User.UserRole.ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);

            // 2. Create Owners
            User owner1 = new User();
            owner1.setUsername("piezisa_king");
            owner1.setEmail("owner1@test.com");
            owner1.setPasswordHash("pass123");
            owner1.setRole(User.UserRole.OWNER);
            owner1.setBusinessName("Piezisa Holdings");
            owner1.setIsActive(true);
            userRepository.save(owner1);

            User owner2 = new User();
            owner2.setUsername("coffee_master");
            owner2.setEmail("owner2@test.com");
            owner2.setPasswordHash("pass123");
            owner2.setRole(User.UserRole.OWNER);
            owner2.setBusinessName("Specialty Brews");
            owner2.setIsActive(true);
            userRepository.save(owner2);

            // 3. Create Student (only if not exists)
            if (!userRepository.existsByEmail("student@test.com")) {
                User student = new User();
                student.setUsername("student1");
                student.setEmail("student@test.com");
                student.setPasswordHash("pass123");
                student.setRole(User.UserRole.STUDENT);
                student.setIsActive(true);
                userRepository.save(student);
            }

            // 4. Create Places (Locals in Cluj)
            createPlace("Samsara Foodhouse", "Vegetarian and vegan restaurant with a relaxing atmosphere.", 
                       "Strada Cardinal Iuliu Hossu 3", owner1, true, Place.PlaceType.RESTAURANT);
            
            createPlace("Enigma Cafe", "Kinetic steampunk cafe with moving gears and unique decor.", 
                       "Strada Iuliu Maniu 12", owner1, true, Place.PlaceType.CAFE);

            createPlace("Meron Central", "Specialty coffee shop perfect for studying or quick meetings.", 
                       "Strada Napoca 3", owner2, true, Place.PlaceType.STUDY_HUB);

            createPlace("Insomnia Cafe", "Bohemian cafe and pub, popular with students and artists.", 
                       "Strada Universității 2", owner2, true, Place.PlaceType.PUB);

            createPlace("Shadow Bar", "A cozy place for students in the heart of the campus.", 
                       "Strada Piezișă 15", owner1, true, Place.PlaceType.CLUB); 

            System.out.println("Database seeded successfully!");
        }
    }

    private void createPlace(String name, String description, String address, User owner, boolean isActive, Place.PlaceType type) {
        Place place = new Place();
        place.setName(name);
        place.setDescription(description);
        place.setAddress(address);
        place.setOwner(owner);
        place.setIsActive(isActive);
        place.setType(type);
        placeRepository.save(place);
    }
}
