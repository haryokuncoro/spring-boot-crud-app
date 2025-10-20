package com.project.crud;

import com.project.crud.entity.User;
import com.project.crud.repository.UserRepository;
import com.project.crud.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository repo;
    private UserServiceImpl svc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        svc = new UserServiceImpl(repo, new BCryptPasswordEncoder());
    }

    @Test
    void createUser_encodesPasswordAndSaves() {
        User u = new User();
        u.setUsername("alice"); u.setPassword("pass"); u.setEmail("a@a.com");
        when(repo.save(any())).thenAnswer(i -> {
            User saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User saved = svc.create(u);
        assertNotNull(saved.getId());
        assertNotEquals("pass", saved.getPassword());
        verify(repo, times(1)).save(any());
    }

}
