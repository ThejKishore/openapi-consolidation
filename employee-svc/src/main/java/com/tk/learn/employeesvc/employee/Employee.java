package com.tk.learn.employeesvc.employee;


import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class Employee {
    private int employeeId;
    private String employeeName;
    private String employeeSurname;
    private String employeeEmail;
}
