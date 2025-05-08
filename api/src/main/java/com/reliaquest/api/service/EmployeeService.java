package com.reliaquest.api.service;

import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.EmployeesResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private final String serverUrl = "http://localhost:8112/api/v1/employee";

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAllEmployees() {
        EmployeesResponse response = restTemplate.getForObject(serverUrl, EmployeesResponse.class);
        List<Employee> employees = response.getData();
        return employees;
    }

    public List<Employee> searchEmployeesByName(String searchString) {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        try {
            EmployeeResponse response = restTemplate.getForObject(serverUrl + "/" + id, EmployeeResponse.class);
            Employee employee = response.getData();
            return employee;
        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }
    }

    public Integer getHighestSalary() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(EmployeeInput employeeInput) {
        EmployeeResponse response = restTemplate.postForObject(serverUrl, employeeInput, EmployeeResponse.class);
        Employee employee = response.getData();
        return employee;
    }

    public boolean deleteEmployee(String id) {
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            return false;
        }

        DeleteEmployeeInput input = new DeleteEmployeeInput();
        input.setName(employee.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DeleteEmployeeInput> request = new HttpEntity<>(input, headers);

        restTemplate.exchange(serverUrl, HttpMethod.DELETE, request, String.class);
        return true;
    }
}