package com.imgprocessor.controller;

import com.imgprocessor.dao.User;
import com.imgprocessor.repository.UserRepository;
import com.imgprocessor.request.SignUpRequest;
import com.imgprocessor.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not availbele for Id :" + userId));

        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/{userID}")
    public ResponseEntity<List<User>> getUserByUserName(@PathVariable(value = "userID") String userId) {

        List<User> user = userRepository.findByuserName(userId);

        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> createUser(@Valid @RequestBody SignUpRequest signUpRequest) {


        if (userRepository.existsByuserName(signUpRequest.getUserName())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        User user = new User(signUpRequest.getUserName(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getUserType(),
                signUpRequest.getDateOfBirth(),
                new Date(), signUpRequest.getGender());
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok()
                .body(new MessageResponse("You've been signed out!"));
    }

}
