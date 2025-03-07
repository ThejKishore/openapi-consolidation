package com.tk.learn.employeesvc.employee;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = {"/v1/api/employees","/v1/web/employees"})
public class EmployeeRsc {

    @GetMapping
    public List<Employee> getEmployee() {
        List<Employee> employees = new ArrayList<>();
        employees.add(Employee.builder().employeeId(1).employeeName("test1").employeeSurname("surname").employeeEmail("test1.surename@xyz.com").build());
        employees.add(Employee.builder().employeeId(2).employeeName("test2").employeeSurname("surname").employeeEmail("test2.surename@xyz.com").build());
        employees.add(Employee.builder().employeeId(3).employeeName("test3").employeeSurname("surname").employeeEmail("test3.surename@xyz.com").build());
        employees.add(Employee.builder().employeeId(4).employeeName("test4").employeeSurname("surname").employeeEmail("test4.surename@xyz.com").build());
        return employees;
    }
}
