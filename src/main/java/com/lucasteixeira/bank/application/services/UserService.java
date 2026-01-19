package com.lucasteixeira.bank.application.services;

import com.lucasteixeira.bank.application.dtos.UserDTO;
import com.lucasteixeira.bank.domain.entities.UserEntity;
import com.lucasteixeira.bank.infratructure.exceptions.ConflitException;
import com.lucasteixeira.bank.application.mappers.UserMapper;
import com.lucasteixeira.bank.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {

        String cpfLimpo = userDTO.getCpf().replaceAll("\\D", "");
        userDTO.setCpf(cpfLimpo);

        if (userRepository.existsByCpf(userDTO.getCpf())) {
            throw new ConflitException("Cpf já cadastrado: " + userDTO.getCpf());
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflitException("Email já cadastrado: " + userDTO.getEmail());
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        UserEntity userEntity = userMapper.toEntity(userDTO);
        userEntity =userRepository.save(userEntity);
        return userMapper.toDTO(userEntity);
    }
}