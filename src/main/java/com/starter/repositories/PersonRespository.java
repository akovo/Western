package com.starter.repositories;

import com.starter.models.entities.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRespository extends CrudRepository<Person, Long> {

    Person findPersonById(Long id);

    List<Person> findAllBy();

}
