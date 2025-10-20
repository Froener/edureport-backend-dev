package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tags")
public class Tag {

    public enum TagType {positive, negative}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tag_id;

    @Column(nullable = false)
    private String tag_nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagType tag_positivo_negativo;

    @ManyToOne
    @JoinColumn(name= "school_id", nullable = false)
    private School school;

    public Long getTag_id() {
        return tag_id;
    }

    public void setTag_id(Long tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_nome() {
        return tag_nome;
    }

    public void setTag_nome(String tag_nome) {
        this.tag_nome = tag_nome;
    }

    public TagType getTag_positivo_negativo() {
        return tag_positivo_negativo;
    }

    public void setTag_positivo_negativo(TagType tag_positivo_negativo) {
        this.tag_positivo_negativo = tag_positivo_negativo;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }
}
