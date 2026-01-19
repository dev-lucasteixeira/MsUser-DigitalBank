package com.lucasteixeira.bank.dtos;

import com.lucasteixeira.bank.constraints.annotations.Cpf;
import com.lucasteixeira.bank.enums.AccessEnum;
import com.lucasteixeira.bank.enums.ActivityEnum;
import com.lucasteixeira.bank.enums.MaritalStatusEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private UUID id;

    @Cpf(message = "O formato do CPF é inválido")
    private String cpf;

    @Size(min = 3, max = 255)
    private String name;

    @Email(message = "Insira um email válido")
    @Size(min = 3, max = 255)
    private String email;

    @Size(min = 3, max = 255)
    private String password;

    private Integer age;

    private MaritalStatusEnum maritalStatus;

    private AccessEnum access;

    private ActivityEnum active;


}
