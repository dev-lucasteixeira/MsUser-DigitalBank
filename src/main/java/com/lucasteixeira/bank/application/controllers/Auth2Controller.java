package com.lucasteixeira.bank.application.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class Auth2Controller {

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal OAuth2User oauth2User) {

        if (oauth2User == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuário não autenticado"));
        }

        return ResponseEntity.ok(oauth2User.getAttributes());
    }

    //http://localhost:8081/login/oauth2/code/github

    //http://localhost:8081/login/oauth2/code/google

    //http://localhost:8081/login/oauth2/code/facebook

    //http://localhost:8081/login
}
