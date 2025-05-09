package com.reliaquest.api.controller;

import java.util.List;
import java.util.Objects;

import com.reliaquest.api.exception.ServiceUnavailableException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Received request to get all employees");
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            log.info("Returning {} employees", employees.size());
            return new ResponseEntity<>(employees, HttpStatus.OK);
        } catch (ServiceUnavailableException e) {
            log.error("Service unavailable while fetching all employees", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable("searchString") String searchString) {
        log.info("Received request to search employees by name: {}", searchString);
        try {
            List<Employee> matchingEmployees = employeeService.searchEmployeesByName(searchString);
            log.info("Found {} employees matching search string '{}'", matchingEmployees.size(), searchString);
            return new ResponseEntity<>(matchingEmployees, HttpStatus.OK);
        } catch (ServiceUnavailableException e) {
            log.error("Service unavailable while searching employees by name: {}", searchString, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") String id) {
        log.info("Received request to get employee by ID: {}", id);
        try {
            Employee employee = employeeService.getEmployeeById(id);
            if (employee != null) {
                log.info("Found employee with ID: {}", id);
                return new ResponseEntity<>(employee, HttpStatus.OK);
            }
            log.warn("Employee with ID: {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ServiceUnavailableException e) {
            log.error("Service unavailable while fetching employee with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Received request to get highest salary of employees");
        try {
            Integer highestSalary = employeeService.getHighestSalary();
            log.info("Highest salary found: {}", highestSalary);
            return new ResponseEntity<>(highestSalary, HttpStatus.OK);
        } catch (ServiceUnavailableException e) {
            log.error("Service unavailable while fetching highest salary", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Received request to get top 10 highest earning employee names");
        try {
            List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();
            log.info("Returning {} top earners", topEarners.size());
            return new ResponseEntity<>(topEarners, HttpStatus.OK);
        } catch (ServiceUnavailableException e) {
            log.error("Service unavailable while fetching top 10 earners", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeInput employeeInput) {
        log.info("Received request to create employee with input: {}", employeeInput);
        try {
            Employee createdEmployee = employeeService.createEmployee(employeeInput);
            log.info("Created employee: {}", createdEmployee);
            return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
        } catch (ServiceUnavailableException e) {
            log.error("Service unavailable while creating employee", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }

    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        log.info("Received request to delete employee with ID: {}", id);
        try {
            String employeeName = employeeService.deleteEmployee(id);
            if (Objects.nonNull(employeeName)) {
                log.info("Deleted employee with ID: {}, name: {}", id, employeeName);
                return new ResponseEntity<>(employeeName, HttpStatus.OK);
            }
            log.warn("Employee with ID: {} not found for deletion", id);
            return new ResponseEntity<>("Employee with id: " + id + " not found", HttpStatus.NOT_FOUND);
        } catch (ServiceUnavailableException e) {
            log.error("Service unavailable while deleting employee with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}