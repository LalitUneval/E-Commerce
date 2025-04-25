package org.lalit.ecommercebackend.controller;

import jakarta.validation.Valid;
import org.lalit.ecommercebackend.dto.AuthRequest;
import org.lalit.ecommercebackend.dto.AuthResponse;
import org.lalit.ecommercebackend.dto.UserDTO;

import org.lalit.ecommercebackend.model.User;
import org.lalit.ecommercebackend.repository.UserRepository;

import org.lalit.ecommercebackend.security.JwtTokenProvider2;
import org.lalit.ecommercebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider2 tokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider2 tokenProvider,

                          UserService userService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;

        this.userService = userService;
        this.userRepository = userRepository;
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginRequest.getUsername(),
//                        loginRequest.getPassword()
//                )
//        );
//
//        System.out.println("Princinple : "+authentication.getPrincipal()+" Name of the user : "+authentication.getName());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        System.out.println("hello");
//        String jwt = tokenProvider.createToken(authentication);
//
//        User user = userRepository.findByUsername(loginRequest.getUsername())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
////        AuthResponse response = new AuthResponse(
////                jwt,
////                user.getId(),
////                user.getUsername(),
////                user.getEmail(),
////                user.getRoles()
////        );
//        AuthResponse response = AuthResponse.builder()
//                .token(jwt)
//                .type("Bearer")
//                .id(user.getId())
//                .username(user.getUsername())
//                .email(user.getEmail())
//                .roles(user.getRoles())
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
@PostMapping("/login")
public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
    try {
        System.out.println("Received login: " + loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);

        System.out.println("Token : "+jwt);
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AuthResponse response = AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        e.printStackTrace(); // log the error
        return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
    }
}

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDTO signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        UserDTO createdUser = userService.createUser(signUpRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
