package com.todo.auth.security;


import com.todo.auth.email.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        AuthenticationResponse authenticate =  service.authenticate(request);
        System.out.println(authenticate.getToken());
        return ResponseEntity.ok(authenticate);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) {
        System.out.println(request.getEmail());
        service.forgotPassword(request);
        return ResponseEntity.ok("Ссылка для сброса пароля отправлена на вашу электронную почту");
    }

    @PostMapping("reset-password/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable String email, @RequestBody ResetPasswordRequest request){
        service.resetPassword(email, request);
        return ResponseEntity.ok("Восстановление пароля успешно");
    }

}
