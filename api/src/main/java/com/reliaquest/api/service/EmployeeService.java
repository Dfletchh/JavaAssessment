package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private final String serverUrl = "http://localhost:8112/api/v1/";

    @Autowired
    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get all employees from the external API
     */
    public List<Employee> getAllEmployees() {
        // Make API call to external service to get all employees
        Employee[] employees = restTemplate.getForObject(serverUrl, Employee[].class);
        return List.of(employees != null ? employees : new Employee[0]);
    }

    /**
     * Search employees by name
     */
    public List<Employee> searchEmployeesByName(String searchString) {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Get employee by ID
     */
    public Employee getEmployeeById(String id) {
        try {
            return restTemplate.getForObject(serverUrl + id, Employee.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the highest salary among all employees
     */
    public Integer getHighestSalary() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);
    }

    /**
     * Get the names of the top 10 highest earning employees
     */
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    /**
     * Create a new employee
     */
    public Employee createEmployee(EmployeeInput employeeInput) {
        Employee newEmployee = new Employee();
        newEmployee.setId(UUID.randomUUID().toString());
        newEmployee.setName(employeeInput.getName());
        newEmployee.setSalary(employeeInput.getSalary());
        newEmployee.setAge(employeeInput.getAge());
        newEmployee.setProfileImage(employeeInput.getProfileImage());
        
        // Call external API to create the employee
        return restTemplate.postForObject(serverUrl, newEmployee, Employee.class);
    }

    /**
     * Delete an employee by ID
     */
    public boolean deleteEmployee(String id) {
        try {
            restTemplate.delete(serverUrl + id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}