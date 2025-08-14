package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

public interface CompensationService {
    // creates or replaces the single active record
    Compensation create(Compensation comp);
    Compensation read(String employeeId);
}
