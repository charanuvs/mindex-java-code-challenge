package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    /**
     * Endpoint for reading Compensation data for employee id
     * @param id Employee id
     * @return List of Compensations associated with employee id
     */
    @GetMapping("/compensation/{id}")
    @CrossOrigin(origins = "http://localhost:3000") // Added to support call from sample UI app. This will be removed or moved to an app level config.
    public List<Compensation> read(@PathVariable String id) {
        LOG.debug("Received compensation read request for employee id [{}]", id);

        return compensationService.read(id);
    }

    /**
     * Endpoint for creating Compensation data
     * @param compensation Compensation object
     * @return Created Compensation object
     */
    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for employee id [{}]", compensation.getEmployeeId());

        return compensationService.create(compensation);
    }
}
