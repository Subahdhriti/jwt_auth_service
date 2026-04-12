package com.authentication.auth_service.service;

import com.authentication.auth_service.dto.AuthResponse;
import com.authentication.auth_service.dto.LoginRequest;
import com.authentication.auth_service.dto.RefreshRequest;
import com.authentication.auth_service.dto.RegisterRequest;
import com.authentication.auth_service.exception.AlreadyExistException;
import com.authentication.auth_service.exception.InvalidCredentialsException;
import com.authentication.auth_service.model.User;
import com.authentication.auth_service.repository.RedisRepository;
import com.authentication.auth_service.repository.UserRepository;
import com.authentication.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final RedisRepository redisRepository;
    //@Value("${jwt.refreshExpirationDays}")
    private static final long refreshExp = 7;

    @Override
    public void register(RegisterRequest request) {
        repository.findByUsername(request.getUsername()).ifPresent(
                user -> {
                    throw new AlreadyExistException("User already exist");
                }
        );

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        repository.save(user);

    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String role = auth.getAuthorities().iterator().next().getAuthority();

            String accessToken = jwtUtil.generateAccessToken(request.getUsername(), role);
            String refreshToken = jwtUtil.generateRefreshToken(request.getUsername(), role);

            redisRepository.save(
                    refreshToken,
                    request.getUsername(),
                    refreshExp*24*3600*1000
            );

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }catch (BadCredentialsException ex){
            throw new InvalidCredentialsException("Invalid Credentials!!!");
        }
    }

    @Override
    public AuthResponse refresh(RefreshRequest request) {
        String username = redisRepository.validate(request.getRefreshToken());
        String accessToken = jwtUtil.generateAccessToken(username, "ROLE_USER");

        return  AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .build();
    }
}
