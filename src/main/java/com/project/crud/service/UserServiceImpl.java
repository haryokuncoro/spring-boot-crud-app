package com.project.crud.service;

import com.project.crud.entity.User;
import com.project.crud.repository.UserRepository;
import com.project.crud.spec.UserSpecification;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<User> findAll(String username, String email, String fullname, Pageable pageable) {
        return repo.findAll(UserSpecification.filterBy(username, email, fullname), pageable);
    }

    @Override
    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repo.save(user);
    }

    @Override
    public User update(Long id, User user) {
        User exist = repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getFullname() != null) exist.setFullname(user.getFullname());
        if (user.getEmail() != null) exist.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isBlank())
            exist.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() != null) exist.setRoles(user.getRoles());
        return repo.save(exist);
    }

    @Override
    public void delete(Long id) { repo.deleteById(id); }

    @Override
    public User findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}