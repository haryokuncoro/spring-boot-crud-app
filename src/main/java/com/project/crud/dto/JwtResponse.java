package com.project.crud.dto;

import lombok.Data;

@Data
public class JwtResponse {
    public String token;
    public String type = "Bearer";
    public Long id;
    public String username;
    public String email;
}
