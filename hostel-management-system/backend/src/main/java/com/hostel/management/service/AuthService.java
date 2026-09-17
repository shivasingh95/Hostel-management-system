package com.hostel.management.service;

import com.hostel.management.dto.AuthResponse;
import com.hostel.management.dto.LoginRequest;
import com.hostel.management.dto.RegisterRequest;
import com.hostel.management.entity.Role;
import com.hostel.management.entity.Student;
import com.hostel.management.exception.DuplicateUserException;
import com.hostel.management.exception.InvalidCredentialsException;
import com.hostel.management.repository.StudentRepository;
import com.hostel.management.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("An account with this email already exists");
        }
        if (studentRepository.existsByRegNo(request.getRegNo())) {
            throw new DuplicateUserException("An account with this registration number already exists");
        }

        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setRegNo(request.getRegNo());
        student.setPhone(request.getPhone());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setRole(Role.STUDENT);

        Student saved = studentRepository.save(student);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getRole().name());

        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(student.getEmail(), student.getRole().name());
        return new AuthResponse(token, student.getId(), student.getName(), student.getRole());
    }
}
