package com.lucasteixeira.bank.application.mappers;


import com.lucasteixeira.bank.application.dtos.UserDTO;
import com.lucasteixeira.bank.domain.entities.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {


    UserDTO toDTO(UserEntity userEntity);

    UserEntity toEntity(UserDTO userDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateUsuario(UserDTO userDTO, @MappingTarget UserEntity userEntity);

}
