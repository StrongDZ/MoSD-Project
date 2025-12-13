package com.travel_agent.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AccountEntity {
    @Column
    private String username;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String role;
}
