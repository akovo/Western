package com.starter.controllers;

import com.starter.models.v1.V1PersonRequest;
import com.starter.models.v1.V1PersonResponse;
import com.starter.repositories.PersonRespository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/people")
public class V1PersonController {

    private final PersonRespository personRespository;

    @Autowired
    public V1PersonController(PersonRespository personRespository) {
        this.personRespository = personRespository;
    }

    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public V1PersonResponse getPerson(@PathVariable("id") @NotNull Long id) {
        return Optional.ofNullable(personRespository.findPersonById(id))
                .map(V1PersonResponse::fromPerson)
                .orElseThrow(() -> new IllegalArgumentException(String.format("No person with id: %s", id)));
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT)
    public V1PersonResponse createPerson(@RequestBody @Valid @NotNull V1PersonRequest personRequest) {
        val person = personRequest.toPerson();
        return V1PersonResponse.fromPerson(personRespository.save(person));
    }

    @Transactional
    @RequestMapping(method = RequestMethod.GET)
    public List<V1PersonResponse> getPeople() {
        return personRespository.findAllBy().stream().map(V1PersonResponse::fromPerson).collect(toList());
    }

}
