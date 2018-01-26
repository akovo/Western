package com.starter.models.v1;

import com.starter.models.entities.Person;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class V1PersonRequest {

    @NotNull
    @Size(min = 1, max = 100)
    private String firstName;

    @NotNull
    @Size(min = 1, max = 100)
    private String lastName;

    public Person toPerson() {
        return Person.builder()
                .firstName(getFirstName())
                .lastName(getLastName())
                .build();
    }

}
