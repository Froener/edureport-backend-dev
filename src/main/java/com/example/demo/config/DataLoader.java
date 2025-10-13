package com.example.demo.config;

import com.example.demo.model.Admin;
import com.example.demo.model.School;
import com.example.demo.model.User;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only create test data if no users exist (for local development)
        if (userRepository.count() == 0) {
            createTestUsers();
        }
    }

    private void createTestUsers() {
        // Create test admin
        User adminUser = new User();
        adminUser.setFull_name("Administrator");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword_hash(passwordEncoder.encode("admin123"));
        adminUser.setUser_type(User.UserType.admin);
        adminUser.setAddress_state("SP");
        adminUser.setAddress_city("São Paulo");
        adminUser.setAddress_neighborhood("Centro");
        adminUser = userRepository.save(adminUser);

        Admin admin = new Admin();
        admin.setUser(adminUser);
        adminRepository.save(admin);

        // Create test school
        User schoolUser = new User();
        schoolUser.setFull_name("Escola Teste");
        schoolUser.setEmail("escola@test.com");
        schoolUser.setPassword_hash(passwordEncoder.encode("school123"));
        schoolUser.setUser_type(User.UserType.school);
        schoolUser.setAddress_state("SP");
        schoolUser.setAddress_city("São Paulo");
        schoolUser.setAddress_neighborhood("Jardins");
        schoolUser = userRepository.save(schoolUser);

        School school = new School();
        school.setSchoolName("Escola Municipal Teste");
        school.setSchool_type(School.SchoolType.municipal);
        school.setUser(schoolUser);
        schoolRepository.save(school);

        System.out.println("Test users created:");
        System.out.println("Admin: admin@test.com / admin123");
        System.out.println("School: escola@test.com / school123");
    }
}