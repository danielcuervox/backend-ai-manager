package com.cuervo.erp_personal.services;

import com.cuervo.erp_personal.models.Activity;
import com.cuervo.erp_personal.models.AuthProvider;
import com.cuervo.erp_personal.models.User;
import com.cuervo.erp_personal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // valores por defecto cuano se crea un usuario
        user.setAvatar("/assets/avatars/default-caveman.png");
        user.setCoins(0);
        user.setPoints(0);
        user.setLevel(1);
        user.setProvider(AuthProvider.LOCAL);

        return userRepository.save(user);
    }

    @Transactional
    public void addPointsUser(User user, Integer points) {
        int currentPoints = user.getPoints() != null ? user.getPoints() : 0;
        user.setPoints(currentPoints + points);

        userRepository.save(user);
    }

    public int calculateMessageLength(String message) {
        if (message == null || message.trim().isEmpty()) {
            return 0;
        }

        int wordCount = message.trim().split("\\s+").length;

        if (wordCount >= 300) {

            return 30;
        } else if (wordCount >= 200) {

            return 10;
        } else if (wordCount >= 100) {
            return 5;
        }

        return 0;
    }

    public void addCoinsUser(User user, int coins){
        int currentCoins = user.getCoins() != null ? user.getCoins() : 0;
        user.setCoins(currentCoins + coins);

        userRepository.save(user);
    }

    public int calculateActivityPoints(Activity activity) {
        int percentagePoints = activity.getResult() != null ? activity.getResult() : 0;
        int basicPoints = 1;

        switch (percentagePoints) {
            case 50 -> basicPoints += 1;
            case 75 -> basicPoints += 2;
            case 100 -> basicPoints += 3;

            default -> { }
        }

        if (Boolean.FALSE.equals(activity.getDone())) {
            basicPoints += -1;
            System.out.println("***** the ACTIVITY ISN'T DONE");
        } else {
            System.out.println("***** the ACTIVITY IS DONE");
        }

        return basicPoints;
    }

    public int calculateMessageCoins(String message) {
        if (message == null || message.trim().isEmpty()) return 0;
        int wordCount = message.trim().split("\\s+").length;

        if (wordCount >= 300) return 3;
        else if (wordCount >= 200) return 2;
        else if (wordCount >= 100) return 1;

        return 0;
    }
}
