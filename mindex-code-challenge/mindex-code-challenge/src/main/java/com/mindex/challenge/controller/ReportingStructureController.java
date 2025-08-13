package com.mindex.challenge.controller;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportingstructure")
public class ReportingStructureController {
    private final ReportingStructureService reportingStructureService;

    public ReportingStructureController(ReportingStructureService reportingStructureService) {
        this.reportingStructureService = reportingStructureService;
    }

    @GetMapping("/{id}")
    public ReportingStructure read(@PathVariable String id) {
        return reportingStructureService.read(id);
    }
}
