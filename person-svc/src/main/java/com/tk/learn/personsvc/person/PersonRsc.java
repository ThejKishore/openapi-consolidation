package com.tk.learn.personsvc.person;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping({"/v1/api/persons","/v1/web/persons"})
public class PersonRsc {

    static List<Person> persons = new ArrayList<>();

    static {
        persons.add(Person.builder().personId(1).firstName("test1").lastName("lastname").age(14).build());
        persons.add(Person.builder().personId(2).firstName("test2").lastName("lastname").age(13).build());
        persons.add(Person.builder().personId(3).firstName("test3").lastName("lastname").age(12).build());
        persons.add(Person.builder().personId(4).firstName("test4").lastName("lastname").age(11).build());
        persons.add(Person.builder().personId(5).firstName("test5").lastName("lastname").age(10).build());
    }

    @GetMapping
    public List<Person> getPersons() {
        return persons;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public Person addPerson(@RequestBody Person person) {
        person.setPersonId(persons.size());
        persons.add(person);
        return person;
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable int id) {
        if(id > persons.size()) {
            return null;
        }
        return persons.get(id);
    }
}
