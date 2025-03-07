package com.tk.learn.personsvc.person;


import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class Person {

    private int personId;
    private String firstName;
    private String lastName;
    private int age;

}
