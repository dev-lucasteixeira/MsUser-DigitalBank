package com.lucasteixeira.bank.application.services;

import com.lucasteixeira.bank.application.dtos.UserDTO;
import com.lucasteixeira.bank.domain.enums.AccessEnum;
import com.lucasteixeira.bank.domain.enums.ActivityEnum;
import com.lucasteixeira.bank.domain.enums.MaritalStatusEnum;
import com.lucasteixeira.bank.application.mappers.UserMapper;
import com.lucasteixeira.bank.domain.entities.UserEntity;
import com.lucasteixeira.bank.domain.repositories.UserRepository;
import com.lucasteixeira.bank.infratructure.exceptions.ConflitException;
import com.lucasteixeira.bank.infratructure.exceptions.ResourceNotFoundException;
import com.lucasteixeira.bank.infratructure.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;


    @Test
    @DisplayName("Deve lançar erro quando CPF já existe")
    void deveLancarErroQuandoCpfExiste() {
        UserDTO inputUserDTO = new UserDTO(null, "123.456.789-10" ,"teste", "teste@teste.com", "1234",
                10, MaritalStatusEnum.SINGLE, AccessEnum.USER, ActivityEnum.ACTIVE );

        doReturn(true).when(userRepository).existsByCpf(any());
        assertThrows(ConflitException.class, () -> userService.createUser(inputUserDTO));
    }

    @Test
    @DisplayName("Deve lançar erro quando Email já existe")
    void deveLancarErroQuandoEmailExiste() {
        UserDTO inputUserDTO = new UserDTO(null, "123.456.789-10" ,"teste", "teste@teste.com", "1234",
                10, MaritalStatusEnum.SINGLE, AccessEnum.USER, ActivityEnum.ACTIVE );

        doReturn(true).when(userRepository).existsByEmail(any());
        assertThrows(ConflitException.class, () -> userService.createUser(inputUserDTO));
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void userCriadoComSucesso() {

        UUID id = UUID.randomUUID();

        UserDTO inputUserDTO = new UserDTO(null, "123.456.789-10" ,"teste", "teste@teste.com", "1234",
                10, MaritalStatusEnum.SINGLE, AccessEnum.USER, ActivityEnum.ACTIVE );

        UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setEmail("teste@teste.com");

        UserDTO outputUserDTO = new UserDTO();
        outputUserDTO.setId(id);
        outputUserDTO.setEmail("teste@teste.com");
        outputUserDTO.setAccess(AccessEnum.USER);
        outputUserDTO.setActive(ActivityEnum.ACTIVE);

        doReturn(false).when(userRepository).existsByCpf(any());
        doReturn(false).when(userRepository).existsByEmail(any());

        doReturn("senhaHash").when(passwordEncoder).encode(any());
        doReturn(new UserEntity()).when(userMapper).toEntity(any());
        doReturn(userEntity).when(userRepository).save(any());
        doReturn(inputUserDTO).when(userMapper).toDTO(any());

        UserDTO resultado = userService.createUser(inputUserDTO);
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve fazer o login do usuário com sucesso")
    void loginUser() {
        UserDTO loginDTO = new UserDTO();
        loginDTO.setEmail("teste@teste.com");
        loginDTO.setPassword("1234");

        UserEntity userNoBanco = new UserEntity();
        userNoBanco.setId(UUID.randomUUID());
        userNoBanco.setEmail("teste@teste.com");
        userNoBanco.setPassword("senhaHashNoBanco");

        doReturn(Optional.of(userNoBanco)).when(userRepository).findByEmail("teste@teste.com");

        doReturn(true).when(passwordEncoder).matches("1234", "senhaHashNoBanco");

        doReturn("token.jwt.falso").when(jwtUtil).generateToken("teste@teste.com");

        String resultadoToken = userService.loginUser(loginDTO);

        assertNotNull(resultadoToken);

        assertEquals("Bearer token.jwt.falso", resultadoToken);

        verify(userRepository, times(1)).findByEmail(any());
        verify(passwordEncoder, times(1)).matches(any(), any());
        verify(jwtUtil, times(1)).generateToken(any());

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar logar com senha incorreta")
    void loginUserWithWrongPassword() {

        UserDTO loginDTO = new UserDTO();
        loginDTO.setEmail("teste@teste.com");
        loginDTO.setPassword("senhaErrada");

        UserEntity userNoBanco = new UserEntity();
        userNoBanco.setId(UUID.randomUUID());
        userNoBanco.setEmail("teste@teste.com");
        userNoBanco.setPassword("senhaCertaHash");

        doReturn(Optional.of(userNoBanco)).when(userRepository).findByEmail("teste@teste.com");

        doReturn(false).when(passwordEncoder).matches("senhaErrada", "senhaCertaHash");

        assertThrows(BadCredentialsException.class, () -> {
            userService.loginUser(loginDTO);
        });

        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar logar com email incorreto")
    void loginUserWithWrongEmail() {

        UserDTO loginDTO = new UserDTO();
        loginDTO.setEmail("teste@teste.com");
        loginDTO.setPassword("senhaErrada");

        UserEntity userNoBanco = new UserEntity();
        userNoBanco.setId(UUID.randomUUID());
        userNoBanco.setEmail("teste@teste.com");
        userNoBanco.setPassword("senhaCertaHash");

        doReturn(Optional.empty()).when(userRepository).findByEmail("teste@teste.com");

        assertThrows(BadCredentialsException.class, () -> {
            userService.loginUser(loginDTO);
        });

        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve retornar os dados do banco com sucesso")
    void getUserInfoByEmail() {
        String email = "teste@teste.com";

        UserEntity userNoBanco = new UserEntity();
        userNoBanco.setId(UUID.randomUUID());
        userNoBanco.setEmail(email);
        userNoBanco.setPassword("senhaCertaHash");

        UserDTO dtoEsperado = new UserDTO();
        dtoEsperado.setEmail(email);

        doReturn(Optional.of(userNoBanco)).when(userRepository).findByEmail("teste@teste.com");
        doReturn(dtoEsperado).when(userMapper).toDTO(userNoBanco);

        UserDTO resultado = userService.getUserInfoByEmail(email);

        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());
        assertEquals(dtoEsperado, resultado);

        verify(userRepository, times(1)).findByEmail(any());

    }

    @Test
    @DisplayName("Deve retornar erro, pois o email não existe no banco")
    void getUserInfoByEmailWrongEmail() {

        String email = "teste@teste.com";

        doReturn(Optional.empty()).when(userRepository).findByEmail("teste@teste.com");

        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserInfoByEmail(email);
        });

        verify(userMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Deve retorna o status da conta com sucesso")
    void updateActivity() {

        String email = "teste@teste.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("teste@teste.com");
        userEntity.setActive(ActivityEnum.ACTIVE);

        UserDTO inputDTO = new UserDTO();
        inputDTO.setEmail(email);
        inputDTO.setActive(ActivityEnum.DEACTIVATED);

        UserDTO outputDTO = new UserDTO();
        outputDTO.setActive(ActivityEnum.DEACTIVATED);

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(email);

        doReturn(userEntity).when(userRepository).save(any());

        doReturn(outputDTO).when(userMapper).toDTO(any());

        UserDTO resultado = userService.updateActivity(inputDTO, email);

        assertNotNull(resultado);
        assertEquals(ActivityEnum.DEACTIVATED, resultado.getActive());

        verify(userRepository).save(argThat(user ->
                user.getActive() == ActivityEnum.DEACTIVATED
        ));

    }

    @Test
    @DisplayName("Deve retorna erro, pois o email esta errado ou não existe no banco")
    void updateActivityWrongEmail() {

        String email = "teste@teste.com";

        doReturn(Optional.empty()).when(userRepository).findByEmail(email);

        assertThrows(ResourceNotFoundException.class, () -> userService.updateActivity(new UserDTO(), email));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retorna erro, pois não houve alteração de status")
    void updateActivityWrongActivityStatus() {

        String email = "teste@teste.com";
        UserDTO inputDTO = new UserDTO();
        inputDTO.setActive(ActivityEnum.ACTIVE);

        UserEntity userEntity = new UserEntity();
        userEntity.setActive(ActivityEnum.ACTIVE);

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(email);

        assertThrows(ConflitException.class, () -> {
            userService.updateActivity(inputDTO, email);
        });

        verify(userRepository, never()).save(any());

    }

    @Test
    @DisplayName("Deve deletar a conta com sucesso")
    void deleteAccountSuccessful() {
        String email = "teste@teste.com";

        doReturn(true).when(userRepository).existsByEmail(email);

        String resultado = userService.deleteAccount(email);

        assertEquals("Conta deletada com sucesso: " + email, resultado);

        verify(userRepository, times(1)).deleteByEmail(any());
    }

    @Test
    @DisplayName("Deve retornar erro, pois o email não existe ou não encontrado")
    void deleteAccountUnseccessful() {
        String email = "teste@teste.com";

        doReturn(false).when(userRepository).existsByEmail(email);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteAccount(email));

        verify(userRepository, never()).deleteByEmail(any());
    }

    @Test
    @DisplayName("Deve alterar os dados com sucesso")
    void updateUser() {

        UUID id = UUID.randomUUID();
        UserDTO inputUserDTO = new UserDTO(null, "123.456.789-10" ,"teste", "teste@teste.com", "1234",
                10, MaritalStatusEnum.SINGLE, AccessEnum.USER, ActivityEnum.ACTIVE );

        UserEntity userEntity = new UserEntity(
                id,
                "123.456.789-10",
                "Mariana Souza",
                "teste@teste.com",
                "1324",
                28,
                MaritalStatusEnum.MARRIED,
                AccessEnum.USER,
                ActivityEnum.ACTIVE
        );

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(inputUserDTO.getEmail());
        doReturn(userEntity).when(userRepository).save(any());
        doReturn(inputUserDTO).when(userMapper).toDTO(any());

        UserDTO result = userService.updateUser(inputUserDTO, inputUserDTO.getEmail());

        assertNotNull(result);
        verify(userRepository, times(1)).save(any());
        verify(userMapper, times(1)).toDTO(any());
    }

    @Test
    @DisplayName("Deve retornar erro, pois o email não existe ou esta errado")
    void updateUserWrongEmail() {

        UserDTO inputUserDTO = new UserDTO(null, "123.456.789-10" ,"teste", "teste@teste.com", "1234",
                10, MaritalStatusEnum.SINGLE, AccessEnum.USER, ActivityEnum.ACTIVE );


        doReturn(Optional.empty()).when(userRepository).findByEmail(inputUserDTO.getEmail());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(inputUserDTO, inputUserDTO.getEmail()));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro, pois o nome está igual ao atual")
    void updateUserWrongName() {
        String email = "teste@teste.com";

        UserDTO inputDTO = new UserDTO();
        inputDTO.setName("Lucas");
        inputDTO.setAge(null);
        inputDTO.setMaritalStatus(null);

        UserEntity userEntity = new UserEntity();
        userEntity.setName("Lucas");
        userEntity.setAge(25);

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(email);

        ConflitException exception = assertThrows(ConflitException.class, () -> {
            userService.updateUser(inputDTO, email);
        });

        assertEquals("Nenhum dado foi alterado. Os valores enviados são idênticos aos atuais.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro, pois a idade está igual à atual")
    void updateUserWrongAge() {
        String email = "teste@teste.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setAge(30);
        userEntity.setName("Lucas");

        UserDTO inputDTO = new UserDTO();
        inputDTO.setAge(30);
        inputDTO.setName("Lucas");

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(email);

        ConflitException exception = assertThrows(ConflitException.class, () -> {
            userService.updateUser(inputDTO, email);
        });

        assertEquals("Nenhum dado foi alterado. Os valores enviados são idênticos aos atuais.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro, pois o estado civil está igual ao atual")
    void updateUserWrongMaritalStatus() {
        String email = "teste@teste.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setMaritalStatus(MaritalStatusEnum.SINGLE);
        userEntity.setName("Lucas");

        UserDTO inputDTO = new UserDTO();
        inputDTO.setMaritalStatus(MaritalStatusEnum.SINGLE);
        inputDTO.setName("Lucas");

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(email);

        ConflitException exception = assertThrows(ConflitException.class, () -> {
            userService.updateUser(inputDTO, email);
        });

        assertEquals("Nenhum dado foi alterado. Os valores enviados são idênticos aos atuais.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar a senha com sucesso")
    void updatePassword() {
        String email = "teste@teste.com";

        UserDTO inputUserDTO = new UserDTO();
        inputUserDTO.setPassword("12345");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword("senhaHash");

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(email);

        doReturn(userEntity).when(userRepository).save(any());

        userService.updatePassword(inputUserDTO, email);

        assertNotEquals("senhaHash", userEntity.getPassword());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro, pois a senha está igual à atual")
    void updatePasswordWrongPassword() {
        String email = "teste@teste.com";
        UserDTO userDTO = new UserDTO();
        userDTO.setPassword("123456");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword("hashAntigo");

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(email);

        doReturn(true).when(passwordEncoder).matches(userDTO.getPassword(), userEntity.getPassword());

        assertThrows(ConflitException.class, () -> {
            userService.updatePassword(userDTO, email);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar alterar senha de usuário inexistente")
    void updatePasswordUserNotFound() {
        String email = "naoexiste@teste.com";
        UserDTO inputUserDTO = new UserDTO();
        inputUserDTO.setPassword("12345");

        doReturn(Optional.empty()).when(userRepository).findByEmail(email);

        assertThrows(ResourceNotFoundException.class, () -> userService.updatePassword(inputUserDTO, email));

        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Deve atualizar a conta parcialmente com sucesso")
    void updateAccountPartialSuccess() {
        String email = "teste@teste.com";
        UserDTO inputUserDTO = new UserDTO();
        inputUserDTO.setName("Novo Nome");

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setName("Nome Antigo");

        doReturn(Optional.of(userEntity)).when(userRepository).findByEmail(email);
        doAnswer(invocation -> {
            UserDTO dto = invocation.getArgument(0);
            UserEntity entity = invocation.getArgument(1);
            entity.setName(dto.getName());
            return null;
        }).when(userMapper).updateUsuario(any(UserDTO.class), any(UserEntity.class));
        doReturn(userEntity).when(userRepository).save(any(UserEntity.class));
        doReturn(inputUserDTO).when(userMapper).toDTO(any(UserEntity.class));

        UserDTO result = userService.updateAccountPartial(inputUserDTO, email);

        assertNotNull(result);
        assertEquals("Novo Nome", userEntity.getName());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(userMapper, times(1)).updateUsuario(any(), any());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar atualizar conta parcial de usuário inexistente")
    void updateAccountPartialUserNotFound() {
        String email = "naoexiste@teste.com";
        UserDTO inputUserDTO = new UserDTO();

        doReturn(Optional.empty()).when(userRepository).findByEmail(email);

        assertThrows(ResourceNotFoundException.class, () -> userService.updateAccountPartial(inputUserDTO, email));

        verify(userRepository, times(0)).save(any());
        verify(userMapper, times(0)).updateUsuario(any(), any());
    }
}