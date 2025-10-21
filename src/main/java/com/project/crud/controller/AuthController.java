package com.project.crud.controller;

import com.project.crud.dto.JwtResponse;
import com.project.crud.dto.LoginRequest;
import com.project.crud.entity.Role;
import com.project.crud.entity.User;
import com.project.crud.repository.UserRepository;
import com.project.crud.security.JwtUtils;
import com.project.crud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepo;
    @Autowired
    private UserService userService;

    public AuthController(AuthenticationManager authManager, JwtUtils jwtUtils, UserRepository userRepo) {
        this.authManager = authManager; this.jwtUtils = jwtUtils; this.userRepo = userRepo;
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest rq) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(rq.username, rq.password));
        String token = jwtUtils.generateJwtToken(auth);
        var user = userRepo.findByUsername(rq.username).orElseThrow();
        var res = new JwtResponse();
        res.token = token; res.id = user.getId(); res.username = user.getUsername(); res.email = user.getEmail();
        return res;
    }

    @PostMapping("/register")
    public User register(@RequestBody User u) {
        // basic checks (improve in prod)
        if (userRepo.existsByUsername(u.getUsername())) throw new RuntimeException("username exists");
        if (userRepo.existsByEmail(u.getEmail())) throw new RuntimeException("email exists");
        if (u.getRoles() == null || u.getRoles().isEmpty()) u.setRoles(Set.of(Role.ROLE_USER));
        // password will be encoded by service on creation (or we can call service here)
        return userService.create(u);
    }
}