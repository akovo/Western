package com.starter.controllers;

import com.starter.models.v1.V1CreateUserRequest;
import com.starter.services.UserCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/user/create")
public class V1CreateUserController {

    private final UserCreationService userCreationService;

    @Autowired
    private V1CreateUserController(UserCreationService userCreationService) {
        this.userCreationService = userCreationService;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT)
    public void createUser(@RequestBody @NotNull @Valid V1CreateUserRequest createUserRequest) {
        userCreationService.createUser(createUserRequest);
    }

}
