package com.coffeehouse.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "userName"),
                @UniqueConstraint(columnNames = "email")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    private String role;
}
