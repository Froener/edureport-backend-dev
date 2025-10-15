package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

//serviço pra extrair informações de um usuario autenticado do Security context
@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null  || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        return authentication.getName(); //retorna o email (set como principal no JwtAuthenticationFilter)
    }

    public Optional<User> getCurrentUser() {
        String email = getCurrentUserEmail();
        if(email == null) {
            throw new RuntimeException("No authenticated user found");
        }
        return userRepository.findByEmail(email);
    }

    public User getCurrentUserOrThrow() {
        return getCurrentUser().orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String getCurrentUserType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.getAuthorities().isEmpty()) {
            return null;
        }
        //extrai a role sem o prefixo "ROLE_"
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        return role.replace("ROLE_", "").toLowerCase();
    }

    public boolean hasRole(String userType) {
        String currentType = getCurrentUserType();
        return currentType != null && currentType.equalsIgnoreCase(userType);
    }
    public Long getCurrentUserId() {
        return getCurrentUser()
                .map(User::getUser_id)
                .orElse(null);
    }

}
