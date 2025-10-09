package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "schools")
public class School {

    public enum SchoolType {municipal, estadual, federal, privada}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long school_id;

    @Column(name = "school_name")
    private String schoolName;

    @Enumerated(EnumType.STRING)
    private SchoolType school_type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Long getSchool_id() {
        return school_id;
    }

    public void setSchool_id(Long school_id) {
        this.school_id = school_id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public SchoolType getSchool_type() {
        return school_type;
    }

    public void setSchool_type(SchoolType school_type) {
        this.school_type = school_type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
