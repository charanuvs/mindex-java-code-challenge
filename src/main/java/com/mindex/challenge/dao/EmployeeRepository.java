package com.mindex.challenge.dao;

import com.mindex.challenge.data.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String>, EmployeeRepositoryCustom {
    Employee findByEmployeeId(String employeeId);
    Integer getNumberOfReports(String employeeId);
}
