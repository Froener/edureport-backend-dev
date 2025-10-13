package com.example.demo.dto;

public class SchoolSignupRequest {
    private UserInfo user;
    private String school_name;
    private String school_type;

    // Constructors, getters, and setters
    public SchoolSignupRequest() {}

    public SchoolSignupRequest(UserInfo user, String school_name, String school_type) {
        this.user = user;
        this.school_name = school_name;
        this.school_type = school_type;
    }

    // Getters and setters
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    public String getSchool_name() { return school_name; }
    public void setSchool_name(String school_name) { this.school_name = school_name; }
    public String getSchool_type() { return school_type; }
    public void setSchool_type(String school_type) { this.school_type = school_type; }

    public static class UserInfo {
        private String full_name;
        private String email;
        private String password_hash;
        private String address_state;
        private String address_city;
        private String address_neighborhood;

        // Constructors, getters, and setters
        public UserInfo() {}

        public UserInfo(String full_name, String email, String password_hash, String address_state, String address_city, String address_neighborhood) {
            this.full_name = full_name;
            this.email = email;
            this.password_hash = password_hash;
            this.address_state = address_state;
            this.address_city = address_city;
            this.address_neighborhood = address_neighborhood;
        }

        // Getters and setters
        public String getFull_name() { return full_name; }
        public void setFull_name(String full_name) { this.full_name = full_name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword_hash() { return password_hash; }
        public void setPassword_hash(String password_hash) { this.password_hash = password_hash; }
        public String getAddress_state() { return address_state; }
        public void setAddress_state(String address_state) { this.address_state = address_state; }
        public String getAddress_city() { return address_city; }
        public void setAddress_city(String address_city) { this.address_city = address_city; }
        public String getAddress_neighborhood() { return address_neighborhood; }
        public void setAddress_neighborhood(String address_neighborhood) { this.address_neighborhood = address_neighborhood; }
    }
}