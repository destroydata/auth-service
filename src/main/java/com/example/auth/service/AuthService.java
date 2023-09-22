package com.example.auth.service;

import com.example.auth.client.api.CustomerClient;
import com.example.auth.client.api.OwnerClient;
import com.example.auth.client.request.OwnerRequest;
import com.example.auth.config.JwtService;
import com.example.auth.domain.entity.Role;
import com.example.auth.domain.entity.User;
import com.example.auth.domain.request.LoginRequest;
import com.example.auth.domain.request.SignupRequest;
import com.example.auth.domain.response.LoginResponse;
import com.example.auth.kafka.CustomerProducer;
import com.example.auth.kafka.OwnerProducer;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OwnerProducer ownerProducer;
    private final CustomerProducer customerProducer;
    @Transactional
    public void signUp(SignupRequest request){
        User build = User.builder()
                .name(request.getName())
                .number(request.getNumber())
                .email(request.getEmail())
                .role(Role.valueOf(request.getRole()))
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        User save = userRepository.save(build);
        OwnerRequest ownerRequest = new OwnerRequest(
                save.getId(), save.getName(), save.getNumber());
        if(save.getRole() == Role.OWNER){
            ownerProducer.send(ownerRequest);
//            ResponseEntity<Void> response = ownerClient.saveOwner(ownerRequest);
//            if(response.getStatusCode() != HttpStatus.CREATED) {
//                String err = save.getRole().name() + "-SERVICE DEAD";
//                throw new RuntimeException(err);
//            }
        }else{
            customerProducer.send(ownerRequest);
//            ResponseEntity<Void> response = customerClient.saveCustomer(ownerRequest);
//            if(response.getStatusCode() != HttpStatus.CREATED) {
//                String err = save.getRole().name() + "-SERVICE DEAD";
//                throw new RuntimeException(err);
//            }
        }

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
