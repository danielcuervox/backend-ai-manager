package com.cuervo.erp_personal.services;

import com.cuervo.erp_personal.models.AuthProvider;
import com.cuervo.erp_personal.models.User;
import com.cuervo.erp_personal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // valores por defecto cuano se crea un usuario
        user.setPoints(0);
        user.setLevel(1);
        user.setProvider(AuthProvider.LOCAL);

        return userRepository.save(user);
    }
}
