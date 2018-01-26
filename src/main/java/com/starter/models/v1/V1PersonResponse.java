package com.starter.models.v1;

import com.starter.models.entities.Person;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class V1PersonResponse {

    private Long id;

    private String firstName;

    private String lastName;

    public static V1PersonResponse fromPerson(Person person) {
        return V1PersonResponse.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build();
    }

}
