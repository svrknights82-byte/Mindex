package com.mindex.challenge.service;

import com.mindex.challenge.data.ReportingStructure;

public interface ReportingStructureService {
    // Should only need to read
    // No need to create anything new because it won't persist
    ReportingStructure read(String id);
}
