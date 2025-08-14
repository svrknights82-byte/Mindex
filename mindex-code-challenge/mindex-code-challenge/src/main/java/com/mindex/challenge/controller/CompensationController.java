package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compensation")
public class CompensationController {

    private final CompensationService compensationService;

    public CompensationController(CompensationService compensationService) {
        this.compensationService = compensationService;
    }

    // Create or replace the single active compensation for an employee
    @PostMapping
    public ResponseEntity<Compensation> create(@RequestBody Compensation comp) {
        Compensation saved = compensationService.create(comp);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    // Read the single active compensation for an employee
    @GetMapping("/{employeeId}")
    public ResponseEntity<Compensation> read(@PathVariable String employeeId) {
        Compensation current = compensationService.readCurrent(employeeId);
        return new ResponseEntity<>(current, HttpStatus.OK);
    }
}
