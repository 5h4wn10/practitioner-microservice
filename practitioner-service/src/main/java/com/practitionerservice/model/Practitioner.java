package com.practitionerservice.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Practitioner {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId; // Koppling till UserService via userId

    private String name;
    private String specialty;

    @Enumerated(EnumType.STRING) // Spara rollen direkt som en sträng
    private Role role;




    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
