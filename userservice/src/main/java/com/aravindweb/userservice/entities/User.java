package com.aravindweb.userservice.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="t_users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)",name = "user_id",updatable = false)
    private UUID id;

    @Column(name="email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone")
    private String phone;

    @Column(name = "country")
    private String country;

    @CreationTimestamp
    @Column(name="created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name="modified_date")
    private LocalDateTime modifiedDate;
}