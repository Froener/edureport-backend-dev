package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.StudentSignupRequest;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.School;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup/student")
    @Transactional
    public ResponseEntity<?> signupStudent(@RequestBody StudentSignupRequest request) {
        try {
            if(userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Email já cadastrado"));
            }
            Optional<School> schoolOpt = schoolRepository.findById(request.getSchoolId());
            if(schoolOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Escola não encontrada"));
            }
            if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Nome completo é obrigatório"));
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Email é obrigatório"));
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Senha deve ter no mínimo 6 caracteres"));
            }

            User user = new User();
            user.setFull_name(request.getFullName());
            user.setSocial_name(request.getSocialName());
            user.setEmail(request.getEmail());
            user.setPassword_hash(passwordEncoder.encode(request.getPassword()));
            user.setBirth_date(request.getBirthDate());
            user.setAddress_state(request.getAddressState());
            user.setAddress_city(request.getAddressCity());
            user.setAddress_neighborhood(request.getAddressNeighborhood());
            user.setUser_type(User.UserType.student);

            User savedUser = userRepository.save(user);

            Student student = new Student();
            student.setUser(savedUser);
            student.setSchool(schoolOpt.get());
            studentRepository.save(student);

            String accessToken = jwtUtil.generateToken(
                    savedUser.getUser_id(),
                    savedUser.getEmail(),
                    savedUser.getUser_type().toString()
            );
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

            LoginResponse response = new LoginResponse(
                    accessToken,
                    refreshToken.getToken(),
                    savedUser.getUser_id(),
                    savedUser.getEmail(),
                    savedUser.getUser_type().toString()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro ao criar conta: "+ e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Email os senha inválidos"));
            }
            User user = userOpt.get();

            boolean isPasswordvalid;
            if (user.getPassword_hash().startsWith("$2a$") || user.getPassword_hash().startsWith("$2b$")) {
                isPasswordvalid = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword_hash());
            } else {
                isPasswordvalid = user.getPassword_hash().equals(loginRequest.getPassword());
            }
            if (!isPasswordvalid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Email ou senha inválidos"));
            }

            String accessToken = jwtUtil.generateToken(
                    user.getUser_id(),
                    user.getEmail(),
                    user.getUser_type().toString()
            );
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            LoginResponse response = new LoginResponse(
                    accessToken,
                    refreshToken.getToken(),
                    user.getUser_id(),
                    user.getEmail(),
                    user.getUser_type().toString()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Um erro ocorrou durante o login"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshTokenStr = request.getRefreshToken();

            Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(refreshTokenStr);

            if (refreshTokenOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Refresh Token invalido"));
            }
            RefreshToken refreshToken = refreshTokenOpt.get();

            if (!refreshTokenService.verifyExpiration(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorResponse("Refresh Token expirou"));
            }

            User user = refreshToken.getUser();
            String newAccessToken = jwtUtil.generateToken(user.getUser_id(), user.getEmail(), user.getUser_type().toString());

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Ocorrou um erro durante o token refresh"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Long userId = jwtUtil.extractUserId(token);

                Optional<User> userOpt = userRepository.findById(userId);
                if (userOpt.isPresent()) {
                    refreshTokenService.deleteByUser(userOpt.get());
                }
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Ocorreu um erro durante o logout"));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

}
