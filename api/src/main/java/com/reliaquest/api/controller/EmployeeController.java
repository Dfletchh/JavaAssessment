package com.reliaquest.api.controller;

import java.util.List;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable("searchString") String searchString) {
        List<Employee> matchingEmployees = employeeService.searchEmployeesByName(searchString);
        return new ResponseEntity<>(matchingEmployees, HttpStatus.OK);
    }

    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") String id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return new ResponseEntity<>(employee, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalary();
        return new ResponseEntity<>(highestSalary, HttpStatus.OK);
    }

    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();
        return new ResponseEntity<>(topEarners, HttpStatus.OK);
    }

    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeInput employeeInput) {
        Employee createdEmployee = employeeService.createEmployee(employeeInput);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        boolean deleted = employeeService.deleteEmployee(id);
        if (deleted) {
            return new ResponseEntity<>("Successfully deleted employee with id: " + id, HttpStatus.OK);
        }
        return new ResponseEntity<>("Employee with id: " + id + " not found", HttpStatus.NOT_FOUND);
    }
}