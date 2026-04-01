package com.project.gamegrimoire.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamegrimoire.Repository.UserRepository;
import com.project.gamegrimoire.model.User;


@RestController
@RequestMapping("/api/test")
public class TestController {
    private final UserRepository userRepository;
    // Constructor injection of UserRepository
    public TestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //Hello World endpoint
    @GetMapping("/hello")
    public String hello() {
        return "Gamegrimoire API is working!";
    }

    //Create a new user
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody String email,
                                             @RequestParam(required = false) String displayname) {
          if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().build();
          }
          // Create the new user
          User user = new User();
          user.setEmail(email);
          user.setDisplayname(displayname != null ? displayname : email.split("@")[0]);
          // Save the user to the database
          User savedUser = userRepository.save(user);
          
          return ResponseEntity.ok(savedUser);
     }
    //Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    //Get user by email
    @GetMapping("/user")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Delete all Users (for testing only) 
    @DeleteMapping("/users")
    public ResponseEntity<String> deleteAllUsers() {
        userRepository.deleteAll();
        return ResponseEntity.ok("All users deleted.");
    }
    
}
