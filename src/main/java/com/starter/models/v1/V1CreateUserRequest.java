package com.starter.models.v1;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class V1CreateUserRequest {

    @NotNull
    @Size(min = 1, max = 100)
    private String email;

    @NotNull
    @Size(min = 1, max = 64)
    private String password;

}
