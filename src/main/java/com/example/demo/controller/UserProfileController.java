package com.example.demo.controller;

import com.example.demo.model.Admin;
import com.example.demo.model.School;
import com.example.demo.model.Student;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/profile")
//@CrossOrigin(origins = "http://localhost:5173")
public class UserProfileController {

    private final UserRepository userRepository;
    private final AuthenticationService authService;
    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final AdminRepository adminRepository;

    public UserProfileController(UserRepository userRepository, AuthenticationService authService, StudentRepository studentRepository, SchoolRepository schoolRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.adminRepository = adminRepository;
    }

    //pega a informação do perfil que está autenticado agr
    //desse modo n vai precisar pegar as informações no localStorage
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            User user = authService.getCurrentUserOrThrow();

            Map<String, Object> profile = new HashMap<>();
            profile.put("userId", user.getUser_id());
            profile.put("fullName", user.getFull_name());
            profile.put("socialName", user.getSocial_name());
            profile.put("email", user.getEmail());
            profile.put("userType", user.getUser_type().toString());
            profile.put("birthDate", user.getBirth_date());
            profile.put("addressState", user.getAddress_state());
            profile.put("addressCity", user.getAddress_city());
            profile.put("addressNeighborhood", user.getAddress_neighborhood());

            //informações específicas do tipo de usuario:
            switch (user.getUser_type()) {

                case student:
                    Student student = studentRepository.findByUserId(user.getUser_id()).orElse(null);
                    if (student != null) {
                        Map<String, Object> schoolInfo = new HashMap<>();
                        schoolInfo.put("schoolId", student.getSchool().getSchool_id());
                        schoolInfo.put("schoolName", student.getSchool().getSchoolName());
                        profile.put("school", schoolInfo);
                    }
                    break;

                case school:
                    School school = schoolRepository.findByUserId(user.getUser_id()).orElse(null);
                    if (school != null) {
                        profile.put("schoolId", school.getSchool_id());
                        profile.put("schoolName", school.getSchoolName());
                        profile.put("schoolType", school.getSchool_type());
                    }
                    break;

                case admin:
                    Admin admin = adminRepository.findByUserId(user.getUser_id())
                            .orElse(null);
                    break;
           }
           return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Failed to fetch user profile: "+e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody Map<String, Object> updates) {
        try {
            User user = authService.getCurrentUserOrThrow();

            //só pode mudar/atualizar os campos permitidos
            if(updates.containsKey("socialName")) {
                user.setSocial_name((String) updates.get("socialName"));
            }
            if (updates.containsKey("addressState")) {
                user.setAddress_state((String) updates.get("addressState"));
            }
            if (updates.containsKey("addressCity")) {
                user.setAddress_city((String) updates.get("addressCity"));
            }
            if (updates.containsKey("addressNeighborhood")) {
                user.setAddress_neighborhood((String) updates.get("addressNeighborhood"));
            }

            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Profile updated succesfully"));


        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update profile "+e.getMessage()));
        }
    }
}
