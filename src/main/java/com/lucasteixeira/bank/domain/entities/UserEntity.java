package com.lucasteixeira.bank.domain.entities;


import com.lucasteixeira.bank.domain.enums.AccessEnum;
import com.lucasteixeira.bank.domain.enums.ActivityEnum;
import com.lucasteixeira.bank.domain.enums.MaritalStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "Cpf", unique = true, nullable = false, length = 11)
    @Size(min = 11, max = 11)
    private String cpf;

    @Column(name = "Name", nullable = false, length = 255)
    @Size(min = 3, max = 255)
    private String name;

    @Column(name = "Email", unique = true, nullable = false, length = 255)
    @Size(min = 5, max = 255)
    private String email;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Age", nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "Marital_Status", nullable = false)
    private MaritalStatusEnum maritalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "Access", nullable = false)
    private AccessEnum access;

    @Enumerated(EnumType.STRING)
    @Column(name = "Active", nullable = false)
    private ActivityEnum active;
}
