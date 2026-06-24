package com.pradeep.bankingapp.service;

import com.pradeep.bankingapp.exception.InvalidTransactionException;
import com.pradeep.bankingapp.model.User;
import com.pradeep.bankingapp.repository.UserRepository;
import com.pradeep.bankingapp.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new InvalidTransactionException("Username already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidTransactionException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidTransactionException("Invalid username or password");
        }

        return jwtUtil.generateToken(username);
    }
}