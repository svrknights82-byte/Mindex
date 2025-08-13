// src/main/java/com/mindex/challenge/service/impl/ReportingStructureServiceImpl.java
package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);
    private final EmployeeService employeeService;

    public ReportingStructureServiceImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ReportingStructure read(String id) {
        Employee employee = employeeService.read(id);
        // need this to prevent loops and counting someone twice
        Set<String> visitedEmployees = new HashSet<>();
        int numberOfReports = calcAllReports(employee, visitedEmployees);

        LOG.debug("Employee [{} {}] has {} reports",
                employee.getFirstName(),
                employee.getLastName(),
                numberOfReports);

        return new ReportingStructure(employee, numberOfReports);
    }

    // This should now handle cycles
    private int calcAllReports(Employee supervisor, Set<String> visitedEmployees) {
        if (supervisor.getDirectReports() == null) {
            return 0;
        }
        visitedEmployees.add(supervisor.getEmployeeId());
        int numOfReports = 0;

        // find all indirect reports
        for (Employee subEmployee : supervisor.getDirectReports()) {
            String subEmployeeID = subEmployee.getEmployeeId();
            if (visitedEmployees.add(subEmployeeID)) {
                numOfReports++;
                // take the id and make sure we have all the fields filled for the employee
                Employee filledSubEmployee = employeeService.read(subEmployeeID);

                if (filledSubEmployee != null) { // verify we have an actual id
                    numOfReports += calcAllReports(filledSubEmployee, visitedEmployees);
                }
            }
        }
        return numOfReports;
    }
}
