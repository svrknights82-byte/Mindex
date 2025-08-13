package com.mindex.challenge.data;

public class ReportingStructure {
    // We will need to determine what employee has what number of reports
    private Employee employee;
    private int numOfReports;

    // We will need to set the object for the number of reports
    public ReportingStructure(Employee employee, int numOfReports) {
        this.employee = employee;
        this.numOfReports = numOfReports;
    }

    // Believe I only need getters not setters
    public Employee getEmployee() {
        return employee;
    }
    public int getNumOfReports () {
        return numOfReports;
    }
}
