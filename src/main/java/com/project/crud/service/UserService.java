package com.project.crud.service;

import com.project.crud.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    Page<User> findAll(String username, String email, String fullname, Pageable pageable);
    User create(User user);
    User update(Long id, User user);
    void delete(Long id);
    User findById(Long id);
}
