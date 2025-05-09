package com.reliaquest.api.service;

import com.reliaquest.api.exception.ServiceUnavailableException;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.EmployeesResponse;
import com.reliaquest.api.web.NetworkHandler;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);
    private final RestTemplate restTemplate;
    private final String serverUrl = "http://localhost:8112/api/v1/employee";

    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Employee> getAllEmployees() throws ServiceUnavailableException {
        log.info("Fetching all employees from server at {}", serverUrl);
        Callable<EmployeesResponse> callable = () -> restTemplate.getForObject(serverUrl, EmployeesResponse.class);

        // NetworkHandler provides simple retry logic with exponential backoff
        EmployeesResponse response = NetworkHandler.call(callable, 3, 1000);
        List<Employee> employees = response.getData();
        log.info("Received {} employees from server", employees.size());
        return employees;
    }

    public List<Employee> searchEmployeesByName(String searchString) throws ServiceUnavailableException {
        log.info("Searching employees by name with search string: {}", searchString);
        List<Employee> allEmployees = getAllEmployees();
        List<Employee> matchingEmployees = allEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
        log.info("Found {} employees matching '{}'", matchingEmployees.size(), searchString);
        return matchingEmployees;
    }

    public Employee getEmployeeById(String id) throws ServiceUnavailableException {
        log.info("Fetching employee by ID: {} from server", id);
        try {
            Callable<EmployeeResponse> callable =
                    () -> restTemplate.getForObject(serverUrl + "/" + id, EmployeeResponse.class);
            EmployeeResponse response = NetworkHandler.call(callable, 3, 1000);
            Employee employee = response.getData();
            if (Objects.nonNull(employee)) {
                log.info("Retrieved employee with ID: {}", id);
            } else {
                log.warn("No employee found with ID: {}", id);
            }
            return employee;
        } catch (ServiceUnavailableException e) {
            log.error("Service unavailable while fetching employee with ID: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching employee with ID: {}", id, e);
            return null;
        }
    }

    public Integer getHighestSalary() throws ServiceUnavailableException {
        log.info("Calculating highest salary among employees");
        List<Employee> allEmployees = getAllEmployees();

        // In terms of scalability this responsibility could be pushed onto a DB query
        Integer highestSalary =
                allEmployees.stream().mapToInt(Employee::getSalary).max().orElse(0);
        log.info("Highest salary calculated: {}", highestSalary);
        return highestSalary;
    }

    public List<String> getTopTenHighestEarningEmployeeNames() throws ServiceUnavailableException {
        log.info("Retrieving top 10 highest earning employee names");
        List<Employee> allEmployees = getAllEmployees();

        // My assumption here is that for this task the server has limited Employees
        // Therefore sorting ~30 employees is negligible
        // In terms of scalability this responsibility could be pushed onto a DB query
        List<String> topEarners = allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
        log.info("Retrieved {} top earners", topEarners.size());
        return topEarners;
    }

    public Employee createEmployee(EmployeeInput employeeInput) throws ServiceUnavailableException {
        log.info("Creating employee with input: {}", employeeInput);
        Callable<EmployeeResponse> callable =
                () -> restTemplate.postForObject(serverUrl, employeeInput, EmployeeResponse.class);
        EmployeeResponse response = NetworkHandler.call(callable, 3, 1000);
        Employee employee = response.getData();
        log.info("Employee created: {}", employee);
        return employee;
    }

    public String deleteEmployee(String id) throws ServiceUnavailableException {
        log.info("Attempting to delete employee with ID: {}", id);
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            log.warn("Cannot delete, employee with ID: {} not found", id);
            return null;
        }

        DeleteEmployeeInput input = new DeleteEmployeeInput();
        input.setName(employee.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DeleteEmployeeInput> request = new HttpEntity<>(input, headers);

        Callable<ResponseEntity<String>> callable =
                () -> restTemplate.exchange(serverUrl, HttpMethod.DELETE, request, String.class);
        NetworkHandler.call(callable, 3, 1000);
        log.info("Deleted employee with ID: {}, name: {}", id, employee.getName());
        return employee.getName();
    }
}
