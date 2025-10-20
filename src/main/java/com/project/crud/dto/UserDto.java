package com.project.crud.dto;

import com.project.crud.entity.Role;
import lombok.Data;

import java.util.Set;
@Data
public class UserDto {
    public Long id;
    public String username;
    public String email;
    public String fullname;
    public Set<Role> roles;
}
