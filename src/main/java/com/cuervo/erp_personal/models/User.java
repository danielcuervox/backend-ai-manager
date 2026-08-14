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
    private Integer points = 0;
    private Integer level = 1;
    private Integer coins = 0;
    private String timezone;
    private String avatar;
    private Integer streak = 0;
    private LocalDate lastLoginDate;

    @PrePersist
    protected void onCreate() {
        if (this.avatar == null || this.avatar.isEmpty()) {
            this.avatar = "/assets/avatars/default_avatar.jpg";
        }
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Activity> activities;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserItem> inventory;


    public void updateStreak() {
        LocalDate today = LocalDate.now();

        // Si lastLoginDate es null, es la primera vez que entra
        if (this.lastLoginDate == null) {
            this.streak = 1;
            this.lastLoginDate = today;
        }
        // si la conexión fue ayer
        else if (this.lastLoginDate.equals(today.minusDays(1))) {
            this.streak += 1;
            this.lastLoginDate = today;
        }
        // Si la última conexión no se hace nada
        else if (this.lastLoginDate.equals(today)) {
            return;
        }
        // Si han pasan más de dos días se rompe la racha
        else {
            this.streak = 1;
            this.lastLoginDate = today;
        }
    }
}



