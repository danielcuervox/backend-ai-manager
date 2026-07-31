package com.cuervo.erp_personal.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                //configuración CORS
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
                    corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No usa sesiones de servidor
                .authorizeHttpRequests(auth -> auth
                        // Permite acceso público a estas rutas de autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        // Tod lo demás requiere estar autenticado
                        //.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/weekly-goal").permitAll()
                        //.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/analytics/daily-report").authenticated()
                        .requestMatchers("/api/weekly-goal/**").authenticated()
                        .requestMatchers("/api/activities/**").authenticated()
                        .requestMatchers("/api/analytics/**").authenticated()
                        .requestMatchers("/api/get-categories").authenticated()
                        .requestMatchers("/api/categories/**").authenticated()
                        .anyRequest().authenticated()

                )
                .addFilterBefore(jwtAuthenticationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
