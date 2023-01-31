package com.mindex.challenge.controller;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @GetMapping("/employee/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    // Added to support call from sample UI app. This will be removed or moved to an app level config.
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee read request for id [{}]", id);

        return employeeService.read(id);
    }

    /**
     * Endpoint for updating Employee with id.
     * Invoking this endpoint evicts directReports cache with key #id set on EmployeeRepositoryCustomImpl#queryDirectReports
     *
     * @param id       Employee id
     * @param employee Employee object
     * @return Updated Employee object
     */
    @PutMapping("/employee/{id}")
    @CacheEvict(value = "directReports", key = "#id")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee create request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }

    /**
     * Endpoint to get ReportingStructure of employee id
     *
     * @param id Employee id
     * @return Number of reports
     */
    @GetMapping("/employee/{id}/reportingStructure")
    @CrossOrigin(origins = "http://localhost:3000")
    // Added to support call from sample UI app. This will be removed or moved to an app level config.
    public ReportingStructure reportingStructure(@PathVariable String id) {
        LOG.debug("Received reporting structure request for id [{}]", id);

        Employee employee = employeeService.read(id);
        Integer numberOfReports = employeeService.numberOfReports(id);

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(numberOfReports);
        return reportingStructure;
    }
}
