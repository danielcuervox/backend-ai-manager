package com.cuervo.erp_personal.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastname;
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    private String password; //si hay registro manual
    private String googleId; //si ingresa con google

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;
    private Integer points;
    private Integer level;
    private String timezone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Activity> activities;
}

