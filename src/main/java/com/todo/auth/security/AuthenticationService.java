package com.todo.auth.security;

import com.todo.auth.config.JwtService;
import com.todo.auth.email.EmailRequest;
import com.todo.auth.email.EmailService;
import com.todo.auth.exception.BadRequestException;
import com.todo.auth.exception.NotFoundException;
import com.todo.auth.exception.RestExceptionHandler;
import com.todo.auth.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RestExceptionHandler restExceptionHandler;


    public AuthenticationResponse register(RegisterRequest request) {
        log.debug("Trying to authorization {}", request);
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        var jwtToken = jwtService.generateToken(user);
        userRepository.save(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.debug("Trying to authentication {}", request);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()

                )
        );

        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new NotFoundException("User not found")
        );

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public ResponseEntity<String> forgotPassword(EmailRequest request) {
        log.debug("Trying to send mail for reset password {}", request);
        String email = request.getEmail();
        // Получить пользователя по электронной почте
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found")
        );
//        if (userRepository.findByEmail(email).isEmpty()) {
//            throw new NotFoundException("User not found");
//            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь с указанной электронной почтой не найден");
//        }


        String link = "http://localhost:8080/api/v1/auth/reset-password/" + email;

        // Отправить ссылку пользователю (например, по электронной почте)
        emailService.sendMail(email, "Ссылка для сброса пароля", "Привет,\n\nВот ваша ссылка: " + link);

        return ResponseEntity.ok("Ссылка успешно отправлен");
    }

    public ResponseEntity<String> resetPassword(String email, ResetPasswordRequest request) {
        log.debug("Trying to reset password {}", request);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        if (Objects.equals(request.getNewPassword(), request.getConfirmPassword())){
            throw new BadRequestException("Password doesn't match");
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пароль не совпадает");
        }
        user.setPassword(request.getNewPassword());
        return ResponseEntity.ok("Новый пароль успешно установлен");

    }
}
