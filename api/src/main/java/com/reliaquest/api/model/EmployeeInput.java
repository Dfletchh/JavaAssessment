package com.reliaquest.api.model;

public class EmployeeInput {
    private String name;
    private int salary;
    private int age;
    private String profileImage;

    // Default constructor
    public EmployeeInput() {
    }

    // Constructor with all fields
    public EmployeeInput(String name, int salary, int age, String profileImage) {
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.profileImage = profileImage;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}