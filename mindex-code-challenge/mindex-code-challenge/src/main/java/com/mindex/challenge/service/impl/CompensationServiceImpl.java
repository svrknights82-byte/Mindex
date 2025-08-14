package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    private final CompensationRepository compensationRepository;
    private final EmployeeService employeeService;

    public CompensationServiceImpl(CompensationRepository compensationRepository,
                                   EmployeeService employeeService) {
        this.compensationRepository = compensationRepository;
        this.employeeService = employeeService;
    }

    @Override
    public Compensation create(Compensation comp) {
        if (comp == null) {
            throw new RuntimeException("You need to build a compensation body first");
        }
        if (comp.getEmployeeId() == null || comp.getEmployeeId().isBlank()) {
            throw new RuntimeException("You need to give a valid employeeId");
        }
        if (comp.getEffectiveDate() == null) {
            throw new RuntimeException("You must add an effective date");
        }
        if (comp.getSalary() < 0) {
            throw new RuntimeException("The salary you add must be >= 0");
        }

        // Validate the employee exists
        employeeService.read(comp.getEmployeeId());

        // Remove any current existing record and replace it with the most up to date one
        Compensation existingComp = compensationRepository.findByEmployeeId(comp.getEmployeeId());
        if (existingComp != null) {
            compensationRepository.delete(existingComp);
        }
        return compensationRepository.save(comp);
    }

    @Override
    public Compensation readCurrent(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new RuntimeException("you must give an employeeId");
        }

        LOG.debug("Reading compensation for employeeId [{}]", employeeId);

        Compensation comp = compensationRepository.findByEmployeeId(employeeId);
        if (comp == null) {
            throw new RuntimeException("No compensation found for employeeId: " + employeeId);
        }
        return comp;
    }
}
