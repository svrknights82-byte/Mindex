package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;

import java.util.HashSet;
import java.util.Set;

public class ReportingStructureServiceImpl implements ReportingStructureService {

    private EmployeeService employeeService;

    public ReportingStructureServiceImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public ReportingStructure read(String id) {
        Employee employee = employeeService.read(id);
        int numberOfReports = calcAllReports(employee);
        return new ReportingStructure(employee, numberOfReports);
    }

    // This function assumes that reports are non-cyclic. If B is a direct report to A, and C is a direct report to B, then A is not a direct report to C.
    // This solution also assumes that each worker only has one direct report.
    private int calcAllReports(Employee supervisor) {
        // if no employees
        if (supervisor.getDirectReports() == null) {
            return 0;
        }
        int count = supervisor.getDirectReports().size();
        for (Employee directReport : supervisor.getDirectReports()) {
            Employee fullEmployee = employeeService.read(directReport.getEmployeeId());
            count += calcAllReports(fullEmployee);
        }
        return count;
    }
}
