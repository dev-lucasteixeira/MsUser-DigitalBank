package com.lucasteixeira.bank.infratructure.handlers;

import com.lucasteixeira.bank.domain.entities.UserEntity;
import com.lucasteixeira.bank.application.enums.AccessEnum;
import com.lucasteixeira.bank.application.enums.ActivityEnum;
import com.lucasteixeira.bank.application.enums.MaritalStatusEnum;
import com.lucasteixeira.bank.domain.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String providerId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        String email = oauthUser.getAttribute("email");

        String name = oauthUser.getAttribute("name");
        if (name == null) {
            name = oauthUser.getAttribute("login");
        }
        if (name == null) {
            name = email;
        }

        if (email != null) {
            if (!userRepository.existsByEmail(email)) {
                UserEntity newUser = new UserEntity();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setPassword(UUID.randomUUID().toString());
                newUser.setAccess(AccessEnum.USER);
                newUser.setActive(ActivityEnum.ACTIVE);
                newUser.setAge(0);
                newUser.setMaritalStatus(MaritalStatusEnum.SINGLE);
                newUser.setCpf("70839953046");

                userRepository.save(newUser);
                System.out.println("Usuário criado via " + providerId + ": " + email);
            } else {
                System.out.println("Login efetuado via " + providerId + ": " + email);
            }
        }

        response.sendRedirect("/auth/user-info");
    }
}
