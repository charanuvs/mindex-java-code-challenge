package com.mindex.challenge.dao.impl;

import com.mindex.challenge.dao.EmployeeRepositoryCustom;
import com.mindex.challenge.data.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class for defining custom Employee repository methods
 */
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeRepositoryCustomImpl.class);

    private final MongoOperations operations;

    @Autowired
    public EmployeeRepositoryCustomImpl(MongoOperations operations) {
        this.operations = operations;
    }

    /**
     * Gets total number of reports under an Employee
     *
     * @param id Employee Id
     * @return Number of reports
     */
    @Override
    public Integer getNumberOfReports(String id) {
        HashSet<String> directReportsSet = new HashSet();
        return getNumberOfReportsRecursive(id, directReportsSet);
    }

    /**
     * Helper function for counting number of reports under an employee
     * Time complexity: O(n) where n is the number of Employees in the hierarchy. All nodes are visited under input Employee. In worst case, we start at root node.
     *
     * @param employeeId       Employee id under which to find reports
     * @param directReportsSet Hash set keeping track of visited nodes. This is needed in case of trees where same employee reports to multiple parent employees
     * @return Count of all nested employees under Employee id
     */
    private Integer getNumberOfReportsRecursive(String employeeId, HashSet<String> directReportsSet) {
        List<Employee> directReports = queryDirectReports(employeeId).stream().filter(directReport -> {
            // Filtering out duplicate entries in directReports in cases where same employee reports to multiple parent level employees.
            if (directReportsSet.contains(directReport.getEmployeeId())) {
                return false;
            }
            directReportsSet.add(directReport.getEmployeeId());
            return true;

        }).collect(Collectors.toList());

        return directReports.size() + directReports.parallelStream().reduce(0,
                // Recursively count and sum the number of direct reports of each direct report of employeeId
                (count, directReport) -> count + getNumberOfReportsRecursive(directReport.getEmployeeId(), directReportsSet), Integer::sum);
    }

    /**
     * Queries and returns list of direct reports of an employee with id from repository
     * The response of this method is cached using basic caching functionality provided by Spring framework. Cache is evicted when EmployeeController#update is invoked with id.
     * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/cache/annotation/Cacheable.html
     *
     * @param id Employee id
     * @return List of direct reports. Returns empty list if employee id does not exist.
     */
    @Cacheable(value = "directReports", key = "#id")
    private List<Employee> queryDirectReports(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("employeeId").is(id));
        Optional<Employee> employeeOptional = Optional.ofNullable(this.operations.findOne(query, Employee.class));
        if (!employeeOptional.isPresent()) {
            LOG.warn("Employee with Id [{}] does not exist in DB.", id);
        }
        return employeeOptional.map(Employee::getDirectReports).orElse(new ArrayList());
    }
}
