package com.vqms.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="vendors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="vendor_name", nullable = false, length = 150)
    private String name;
    @Column(nullable = false,unique = true,length = 150)
    private  String email;
    @Column(length = 20)
    private  String phone;
    @Column(length = 250)
    private  String address;

    @Column(nullable = false, length = 30)
    private  String status =  "ACTIVE";
    @Column(name = "created_At",nullable = false,updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_At", nullable = false)
    private  LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
