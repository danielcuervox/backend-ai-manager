package com.cuervo.erp_personal.security;
import com.cuervo.erp_personal.models.User;
import com.cuervo.erp_personal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Intentando loguear usuario: " + email); // -----
        // búsqueda por email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        System.out.println("Usuario encontrado en BD: " + user.getEmail()); // -----
        // construye el objeto que Spring Security necesita
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(email);
        builder.password(user.getPassword());

        // aquí para añadir roles (ej: "USER", "ADMIN") POR ESO EMPTY LIST
        builder.authorities(Collections.emptyList());

        return builder.build();
    }
}
