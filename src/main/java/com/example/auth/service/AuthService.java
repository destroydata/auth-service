package com.example.auth.service;

import com.example.auth.config.JwtService;
import com.example.auth.domain.entity.Role;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.request.LoginRequest;
import com.example.auth.domain.request.SignupRequest;
import com.example.auth.domain.response.LoginResponse;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void signUp(SignupRequest request){
        User build = User.builder()
                .name(request.getName())
                .number(request.getNumber())
                .email(request.getEmail())
                .role(Role.valueOf(request.getRole()))
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(build);
    }

    public LoginResponse login(LoginRequest request){
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        User user = byEmail.orElseThrow(
                () -> new IllegalArgumentException("USER NOT FOUND"));
        if(!passwordEncoder
                .matches(request.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("USER NOT FOUND");
        }
        String token = jwtService.makeToken(user);
        return new LoginResponse(token, user.getRole().name());
    }
}
