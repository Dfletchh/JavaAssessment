package com.reliaquest.api;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.exception.ServiceUnavailableException;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllEmployeesSuccess() {
        List<Employee> mockEmployees = Arrays.asList(new Employee("1", "John", 50000, 30, "Dev", "test@dummy.com"));
        when(employeeService.getAllEmployees()).thenReturn(mockEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEmployees, response.getBody());
    }

    @Test
    public void testGetAllEmployeesServiceUnavailable() {
        when(employeeService.getAllEmployees()).thenThrow(new ServiceUnavailableException("Service down"));

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    public void testGetEmployeeByIdSuccess() {
        Employee mockEmployee = new Employee("1", "John", 50000, 30, "Dev", "test@dummy.com");
        when(employeeService.getEmployeeById("1")).thenReturn(mockEmployee);

        ResponseEntity<Employee> response = employeeController.getEmployeeById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEmployee, response.getBody());
    }

    @Test
    public void testGetEmployeeByIdNotFound() {
        when(employeeService.getEmployeeById("1")).thenReturn(null);

        ResponseEntity<Employee> response = employeeController.getEmployeeById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateEmployeeSuccess() {
        EmployeeInput input = new EmployeeInput();
        input.setName("Jane");
        Employee mockEmployee = new Employee("2", "Jane", 60000, 28, "Manager", "test@dummy.com");
        when(employeeService.createEmployee(input)).thenReturn(mockEmployee);

        ResponseEntity<Employee> response = employeeController.createEmployee(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockEmployee, response.getBody());
    }

    @Test
    public void testDeleteEmployeeByIdSuccess() {
        when(employeeService.deleteEmployee("1")).thenReturn("John");

        ResponseEntity<String> response = employeeController.deleteEmployeeById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John", response.getBody());
    }

    @Test
    public void testDeleteEmployeeByIdNotFound() {
        when(employeeService.deleteEmployee("1")).thenReturn(null);

        ResponseEntity<String> response = employeeController.deleteEmployeeById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Employee with id: 1 not found", response.getBody());
    }
}