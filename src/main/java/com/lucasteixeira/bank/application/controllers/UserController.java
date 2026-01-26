package com.lucasteixeira.bank.application.controllers;


import com.lucasteixeira.bank.application.dtos.UserDTO;
import com.lucasteixeira.bank.application.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO,
                                              UriComponentsBuilder uriBuilder){

        UserDTO createdUser = userService.createUser(userDTO);
        var uri = uriBuilder.path("/user/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();

        return ResponseEntity.created(uri).body(createdUser);
    }

    @PostMapping("/signin")
    public ResponseEntity<String> loginWithEmail(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.loginUser(userDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfoByEmail() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.getUserInfoByEmail(emailLogado));
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserDTO userDTO){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(userService.updateUser(userDTO, email));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<UserDTO> updatePassword(@RequestBody UserDTO userDTO){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.updatePassword(userDTO, email));
    }

    @PatchMapping("/update-user")
    public ResponseEntity<UserDTO> updateAccountPartial(@RequestBody UserDTO userDTO){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.updateAccountPartial(userDTO, email));
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteAccount(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(userService.deleteAccount(email));
    }

}
