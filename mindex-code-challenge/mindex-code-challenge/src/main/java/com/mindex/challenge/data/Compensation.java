package com.mindex.challenge.data;

import java.time.LocalDate;

/**
 * This is the backbone of the compensation design.  This will relate the employee id to
 * a salary and effective date.
 */
public class Compensation {
    private String employeeId; // This is how we will link this to an employee
    private double salary;
    private LocalDate effectiveDate;

    public Compensation() {
    }

    public Compensation(String employeeId, double salary, LocalDate effectiveDate) {
        this.employeeId = employeeId;
        this.salary = salary;
        this.effectiveDate = effectiveDate;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
