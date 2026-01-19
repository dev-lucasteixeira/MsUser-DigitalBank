package com.lucasteixeira.bank.controllers;


import com.lucasteixeira.bank.dtos.UserDTO;
import com.lucasteixeira.bank.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

        System.out.println(Thread.currentThread());
        return ResponseEntity.created(uri).body(createdUser);
    }

}
