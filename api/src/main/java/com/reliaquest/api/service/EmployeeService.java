package com.reliaquest.api.service;

import com.reliaquest.api.exception.ServiceUnavailableException;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.EmployeesResponse;
import com.reliaquest.api.web.NetworkHandler;

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
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final RestTemplate restTemplate;
    private final String serverUrl = "http://localhost:8112/api/v1/employee";

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAllEmployees() throws ServiceUnavailableException {
        Callable<EmployeesResponse> callable = () -> restTemplate.getForObject(serverUrl, EmployeesResponse.class);
        EmployeesResponse response = NetworkHandler.call(callable, 3, 1000);
        List<Employee> employees = response.getData();
        return employees;
    }

    public List<Employee> searchEmployeesByName(String searchString) throws ServiceUnavailableException {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) throws ServiceUnavailableException {
        try {
            Callable<EmployeeResponse> callable = () -> restTemplate.getForObject(serverUrl + "/" + id, EmployeeResponse.class);
            EmployeeResponse response = NetworkHandler.call(callable, 3, 1000);
            Employee employee = response.getData();
            return employee;
        } catch (ServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getHighestSalary() throws ServiceUnavailableException {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .mapToInt(Employee::getSalary)
                .max()
                .orElse(0);
    }

    public List<String> getTopTenHighestEarningEmployeeNames() throws ServiceUnavailableException {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(EmployeeInput employeeInput) throws ServiceUnavailableException {
        Callable<EmployeeResponse> callable = () -> restTemplate.postForObject(serverUrl, employeeInput, EmployeeResponse.class);
        EmployeeResponse response = NetworkHandler.call(callable, 3, 1000);
        Employee employee = response.getData();
        return employee;
    }

    public boolean deleteEmployee(String id) throws ServiceUnavailableException {
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
        Callable<ResponseEntity<String>> callable = () -> restTemplate.exchange(serverUrl, HttpMethod.DELETE, request, String.class);
        ResponseEntity<String> response = NetworkHandler.call(callable, 3, 1000);
        return true;
    }
}