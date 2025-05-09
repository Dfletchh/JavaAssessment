package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.EmployeesResponse;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

public class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllEmployees() {
        List<Employee> mockEmployees = Arrays.asList(new Employee("1", "John", 50000, 30, "Dev", "test@dummy.com"));
        EmployeesResponse response = new EmployeesResponse();
        response.setData(mockEmployees);
        when(restTemplate.getForObject(anyString(), eq(EmployeesResponse.class)))
                .thenReturn(response);

        List<Employee> employees = employeeService.getAllEmployees();

        assertEquals(1, employees.size());
        assertEquals("John", employees.get(0).getName());
    }

    @Test
    public void testSearchEmployeesByName() {
        List<Employee> mockEmployees = Arrays.asList(
                new Employee("1", "John Doe", 50000, 30, "Dev", "test@dummy.com"),
                new Employee("2", "Jane Smith", 60000, 28, "Manager", "test@dummy.com"));
        EmployeesResponse response = new EmployeesResponse();
        response.setData(mockEmployees);
        when(restTemplate.getForObject(anyString(), eq(EmployeesResponse.class)))
                .thenReturn(response);

        List<Employee> result = employeeService.searchEmployeesByName("john");

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    public void testGetHighestSalary() {
        List<Employee> mockEmployees = Arrays.asList(
                new Employee("1", "John", 50000, 30, "Dev", "test@dummy.com"),
                new Employee("2", "Jane", 60000, 28, "Manager", "test@dummy.com"));
        EmployeesResponse response = new EmployeesResponse();
        response.setData(mockEmployees);
        when(restTemplate.getForObject(anyString(), eq(EmployeesResponse.class)))
                .thenReturn(response);

        Integer highestSalary = employeeService.getHighestSalary();

        assertEquals(60000, highestSalary.intValue());
    }

    @Test
    public void testCreateEmployee() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Jane");
        Employee mockEmployee = new Employee("2", "Jane", 60000, 28, "Manager", "test@dummy.com");
        EmployeeResponse response = new EmployeeResponse();
        response.setData(mockEmployee);
        when(restTemplate.postForObject(anyString(), eq(input), eq(EmployeeResponse.class)))
                .thenReturn(response);

        Employee created = employeeService.createEmployee(input);

        assertEquals("Jane", created.getName());
    }
}
