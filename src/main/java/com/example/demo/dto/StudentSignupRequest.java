package com.example.demo.dto;

import java.time.LocalDate;

public class StudentSignupRequest {
    private String fullName;
    private String socialName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String addressState;
    private String addressCity;
    private String addressNeighborhood;
    private Long schoolId;

    public StudentSignupRequest(String fullName, String socialName, String email, String password, LocalDate birthDate, String addressState, String addressCity, String addressNeighborhood, Long schoolId) {
        this.fullName = fullName;
        this.socialName = socialName;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.addressState = addressState;
        this.addressCity = addressCity;
        this.addressNeighborhood = addressNeighborhood;
        this.schoolId = schoolId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSocialName() {
        return socialName;
    }

    public void setSocialName(String socialName) {
        this.socialName = socialName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddressNeighborhood() {
        return addressNeighborhood;
    }

    public void setAddressNeighborhood(String addressNeighborhood) {
        this.addressNeighborhood = addressNeighborhood;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }
}
