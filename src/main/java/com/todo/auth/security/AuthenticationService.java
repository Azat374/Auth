package com.todo.auth.security;

import com.todo.auth.config.JwtService;
import com.todo.auth.email.EmailRequest;
import com.todo.auth.email.EmailService;
import com.todo.auth.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Query("")
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        RoleEntity roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));
        var jwtToken = jwtService.generateToken(user);
        userRepository.save(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()

                )
        );
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    public ResponseEntity<String> forgotPassword(EmailRequest request) {
        String email = request.getEmail();
        // Получить пользователя по электронной почте
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            // Пользователь с указанной электронной почтой не найден
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь с указанной электронной почтой не найден");
        }

        String link = "http://localhost:8080/api/v1/auth/reset-password/" + email;

        // Отправить новый пароль пользователю (например, по электронной почте)
        emailService.sendMail(email, "Ссылка для сброса пароля", "Привет,\n\nВот ваша ссылка: " + link);

        return ResponseEntity.ok("Ссылка успешно отправлен");
    }

    public ResponseEntity<String> resetPassword(String email, ResetPasswordRequest request) {
        if (Objects.equals(request.getNewPassword(), request.getConfirmPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пароль не совпадает");
        }
        User user = userRepository.findByEmail(email).get();
        user.setPassword(request.getNewPassword());
        return ResponseEntity.ok("Новый пароль успешно установлен");

    }
}
