package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import com.reliaquest.api.model.EmployeesResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class EmployeeIntegrationTest {

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void testGetAllEmployeesIntegration() {
        List<Employee> mockEmployees = Arrays.asList(new Employee("1", "John", 50000, 30, "Dev", "test@dummy.com"));
        EmployeesResponse response = new EmployeesResponse();
        response.setData(mockEmployees);
        when(restTemplate.getForObject(any(String.class), eq(EmployeesResponse.class)))
                .thenReturn(response);

        ResponseEntity<List<Employee>> result = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals("John", result.getBody().get(0).getName());
    }

    @Test
    public void testGetEmployeeByIdIntegration() {
        Employee mockEmployee = new Employee("1", "John", 50000, 30, "Dev", "test@dummy.com");
        EmployeeResponse response = new EmployeeResponse();
        response.setData(mockEmployee);
        when(restTemplate.getForObject(eq("http://localhost:8112/api/v1/employee/1"), eq(EmployeeResponse.class)))
                .thenReturn(response);

        ResponseEntity<Employee> result = employeeController.getEmployeeById("1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("John", result.getBody().getName());
    }

    @Test
    public void testCreateEmployeeIntegration() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Jane");
        Employee mockEmployee = new Employee("2", "Jane", 60000, 28, "Manager", "test@dummy.com");
        EmployeeResponse response = new EmployeeResponse();
        response.setData(mockEmployee);
        when(restTemplate.postForObject(
                        eq("http://localhost:8112/api/v1/employee"), eq(input), eq(EmployeeResponse.class)))
                .thenReturn(response);

        ResponseEntity<Employee> result = employeeController.createEmployee(input);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Jane", result.getBody().getName());
    }

    @Test
    public void testDeleteEmployeeByIdIntegration() {
        Employee mockEmployee = new Employee("1", "John", 50000, 30, "Dev", "test@dummy.com");
        EmployeeResponse getResponse = new EmployeeResponse();
        getResponse.setData(mockEmployee);
        when(restTemplate.getForObject(eq("http://localhost:8112/api/v1/employee/1"), eq(EmployeeResponse.class)))
                .thenReturn(getResponse);
        when(restTemplate.exchange(
                        eq("http://localhost:8112/api/v1/employee"), eq(HttpMethod.DELETE), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("John"));

        ResponseEntity<String> result = employeeController.deleteEmployeeById("1");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("John", result.getBody());
    }

    @Test
    public void testGetEmployeesByNameSearchIntegration() {
        List<Employee> mockEmployees = Arrays.asList(
                new Employee("1", "John Doe", 50000, 30, "Dev", "test@dummy.com"),
                new Employee("2", "Jane Smith", 60000, 28, "Manager", "test@dummy.com"),
                new Employee("3", "Alice Johnson", 55000, 32, "Dev", "test@dummy.com"));
        EmployeesResponse response = new EmployeesResponse();
        response.setData(mockEmployees);
        when(restTemplate.getForObject(any(String.class), eq(EmployeesResponse.class)))
                .thenReturn(response);

        ResponseEntity<List<Employee>> result = employeeController.getEmployeesByNameSearch("john");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        assertTrue(result.getBody().stream().anyMatch(e -> e.getName().equals("John Doe")));
        assertTrue(result.getBody().stream().anyMatch(e -> e.getName().equals("Alice Johnson")));
    }

    @Test
    public void testGetAllEmployeesServiceUnavailableIntegration() {
        when(restTemplate.getForObject(any(String.class), eq(EmployeesResponse.class)))
                .thenThrow(new RuntimeException("Connection failed"))
                .thenThrow(new RuntimeException("Connection failed"))
                .thenThrow(new RuntimeException("Connection failed"));

        ResponseEntity<List<Employee>> result = employeeController.getAllEmployees();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
    }
}
