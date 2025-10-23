package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.StudentSignupRequest;
import com.example.demo.model.*;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final AdminRepository adminRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationService authenticationService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(
            UserRepository userRepository,
            StudentRepository studentRepository,
            SchoolRepository schoolRepository,
            AdminRepository adminRepository,
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService,
            AuthenticationService authenticationService) {

        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.adminRepository = adminRepository;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.authenticationService = authenticationService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

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
                    savedUser.getUser_type().toString(),
                    savedUser.getFull_name()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro ao criar conta: "+ e.getMessage()));
        }
    }

    @PostMapping("/signup/school")
    @Transactional
    public ResponseEntity<?> signupSchool(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, String> userMap = (Map<String, String>) payload.get("user");
            String email = userMap.get("email");

            if(userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Email já cadastrado"));
            }

            // Validate required fields
            if (userMap.get("full_name") == null || userMap.get("full_name").trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Nome completo é obrigatório"));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Email é obrigatório"));
            }
            if (userMap.get("password_hash") == null || userMap.get("password_hash").length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Senha deve ter no mínimo 6 caracteres"));
            }

            User user = new User();
            user.setFull_name(userMap.get("full_name"));
            user.setEmail(email);
            user.setPassword_hash(passwordEncoder.encode(userMap.get("password_hash")));
            user.setUser_type(User.UserType.school);
            user.setAddress_state(userMap.get("address_state"));
            user.setAddress_city(userMap.get("address_city"));
            user.setAddress_neighborhood(userMap.get("address_neighborhood"));
            user = userRepository.save(user);

            School school = new School();
            school.setSchoolName((String) payload.get("school_name"));
            school.setSchool_type(School.SchoolType.valueOf(((String) payload.get("school_type")).toLowerCase()));
            school.setUser(user);
            school = schoolRepository.save(school);

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
                    user.getUser_type().toString(),
                    user.getFull_name()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro ao criar conta da escola: " + e.getMessage()));
        }
    }

    @PostMapping("/signup/admin")
    @Transactional
    public ResponseEntity<?> signupAdmin(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> userMap = (Map<String, Object>) payload.get("user");
            String email = (String) userMap.get("email");

            if(userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Email já cadastrado"));
            }

            // Validate required fields
            if (userMap.get("full_name") == null || ((String) userMap.get("full_name")).trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Nome completo é obrigatório"));
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Email é obrigatório"));
            }
            if (userMap.get("password_hash") == null || ((String) userMap.get("password_hash")).length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Senha deve ter no mínimo 6 caracteres"));
            }

            User user = new User();
            user.setFull_name((String) userMap.get("full_name"));
            user.setSocial_name((String) userMap.get("social_name"));
            user.setEmail(email);
            user.setPassword_hash(passwordEncoder.encode((String) userMap.get("password_hash")));
            user.setUser_type(User.UserType.admin);
            user.setAddress_state((String) userMap.get("address_state"));
            user.setAddress_city((String) userMap.get("address_city"));
            user.setAddress_neighborhood((String) userMap.get("address_neighborhood"));

            // Handle birth date if provided
            if (userMap.get("birth_date") != null) {
                try {
                    user.setBirth_date(LocalDate.parse((String) userMap.get("birth_date")));
                } catch (Exception e) {
                    // Log but don't fail if birth date parsing fails
                    System.out.println("Warning: Could not parse birth date: " + userMap.get("birth_date"));
                }
            }

            User savedUser = userRepository.save(user);

            Admin admin = new Admin();
            admin.setUser(savedUser);
            adminRepository.save(admin);

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
                    savedUser.getUser_type().toString(),
                    savedUser.getFull_name()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erro ao criar conta de administrador: " + e.getMessage()));
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
                    user.getUser_type().toString(),
                    user.getFull_name()
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
            User user = authenticationService.getCurrentUserOrThrow();
            refreshTokenService.deleteByUser(user);

            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
        }
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }

}
