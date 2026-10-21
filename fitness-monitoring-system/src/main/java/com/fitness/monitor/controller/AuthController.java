package com.fitness.monitor.controller;

import com.fitness.monitor.dto.ApiResponse;
import com.fitness.monitor.dto.LoginRequest;
import com.fitness.monitor.dto.RegisterRequest;
import com.fitness.monitor.model.User;
import com.fitness.monitor.security.JwtService;
import com.fitness.monitor.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Your account is ready", session(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Welcome back", session(user)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me(@RequestAttribute("userId") Long userId) {
        User user = userService.findById(userId);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fullName", user.getFullName());
        body.put("email", user.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Account loaded", body));
    }

    private Map<String, Object> session(User user) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("token", jwtService.issue(user.getId()));
        body.put("fullName", user.getFullName());
        body.put("email", user.getEmail());
        return body;
    }
}
