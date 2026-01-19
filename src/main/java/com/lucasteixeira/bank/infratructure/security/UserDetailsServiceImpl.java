package com.lucasteixeira.bank.infratructure.security;

import com.lucasteixeira.bank.domain.entities.UserEntity;
import com.lucasteixeira.bank.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : "+ email));

        return User.withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())
                .build();
    }
}
