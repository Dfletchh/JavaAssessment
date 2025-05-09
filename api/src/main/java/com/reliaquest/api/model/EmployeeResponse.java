package com.reliaquest.api.model;

import lombok.Data;

@Data
public class EmployeeResponse {
    private Employee data;
    private String status;
}
