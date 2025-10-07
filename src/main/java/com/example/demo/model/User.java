package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    public enum UserType {admin, student, school}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

    private String full_name;
    private String social_name;
    private String email;
    private String password_hash;

    @Column(nullable = true)
    private LocalDate birth_date;

    private String address_state;
    private String address_city;
    private String address_neighborhood;

    @Enumerated(EnumType.STRING)
    private UserType user_type;

    public Long getUser_id() {
        return user_id;
    }
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getSocial_name() {
        return social_name;
    }
    public void setSocial_name(String social_name) {
        this.social_name = social_name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }
    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public LocalDate getBirth_date() {
        return birth_date;
    }
    public void setBirth_date(LocalDate birth_date) {
        this.birth_date = birth_date;
    }

    public String getAddress_state() {
        return address_state;
    }
    public void setAddress_state(String address_state) {
        this.address_state = address_state;
    }

    public String getAddress_city() {
        return address_city;
    }
    public void setAddress_city(String address_city) {
        this.address_city = address_city;
    }

    public String getAddress_neighborhood() {
        return address_neighborhood;
    }
    public void setAddress_neighborhood(String address_neighborhood) {
        this.address_neighborhood = address_neighborhood;
    }

    public UserType getUser_type() {
        return user_type;
    }
    public void setUser_type(UserType user_type) {
        this.user_type = user_type;
    }
}
