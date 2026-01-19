package com.lucasteixeira.bank.domain.repositories;

import com.lucasteixeira.bank.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByCpf(String cpf);

    @Transactional
    void deleteByCpf(String cpf);
    @Transactional
    void deleteByEmail(String email);
}
