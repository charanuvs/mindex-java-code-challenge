package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * Class representing Compensation for an employee
 */
public class Compensation {
    /**
     * Composite key for Compensation repository. This is stored in the database but ignored during serialization.
     */
    @Id
    @JsonIgnore
    private CompensationId compensationId;
    private String employeeId;
    private Double salary;
    private Date effectiveDate;

    /**
     * Gets identifier of employee associated with Compensation
     * @return Employee Id
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets the identifier of employee associated with Compensation
     * @param employeeId Employee Id
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Gets the salary associated with Compensation
     * @return Numeric value of salary
     */
    public Double getSalary() {
        return salary;
    }

    /**
     * Sets the salary associated with Compensation
     * @param salary Numeric value of salary
     */
    public void setSalary(Double salary) {
        this.salary = salary;
    }

    /**
     * Gets effective date of Compensation in UTC
     * @return UTC date
     */
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets effective date of Compensation in UTC
     * @param effectiveDate Effective date
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * Constructor for Compensation class.
     * Using the constructor to initialize the compensationId composite key.
     * @param employeeId Employee id associated with Compensation
     * @param effectiveDate Effective date of Compensation
     * @param salary Numeric value of salary associated with Compensation
     */
    public Compensation(String employeeId, Date effectiveDate, Double salary) {
        this.compensationId = new CompensationId(employeeId, effectiveDate);
        this.employeeId = employeeId;
        this.effectiveDate = effectiveDate;
        this.salary = salary;
    }

    /**
     * Class representing the composite key for Compensation repository
     */
    private class CompensationId implements Serializable {
        private String employeeId;
        private Date effectiveDate;

        public CompensationId(String employeeId, Date effectiveDate) {
            this.employeeId = employeeId;
            this.effectiveDate = effectiveDate;
        }
    }
}
