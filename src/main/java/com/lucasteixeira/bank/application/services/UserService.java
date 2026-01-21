package com.lucasteixeira.bank.application.services;

import com.lucasteixeira.bank.application.dtos.UserDTO;
import com.lucasteixeira.bank.domain.entities.UserEntity;
import com.lucasteixeira.bank.infratructure.exceptions.ConflitException;
import com.lucasteixeira.bank.application.mappers.UserMapper;
import com.lucasteixeira.bank.domain.repositories.UserRepository;
import com.lucasteixeira.bank.infratructure.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
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
    private final JwtUtil jwtUtil;

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

    public String loginUser(UserDTO userDTO){
        if (!userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflitException("Email não cadastrado: " + userDTO.getEmail());
        }

        if (!passwordEncoder.matches(userDTO.getPassword(), userRepository.findByEmail(userDTO.getEmail()).get().getPassword())) {
            throw new ConflitException("Senha incorreta: " + userDTO.getEmail());
        }
        return "Bearer "+ jwtUtil.generateToken(userDTO.getEmail());
    }

    public UserDTO getUserInfoByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toDTO(user);
    }
}