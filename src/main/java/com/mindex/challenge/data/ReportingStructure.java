package com.mindex.challenge.data;

/**
 * Class used to represent employee reporting structure
 */
public class ReportingStructure {
    private Employee employee;
    private Integer numberOfReports;

    public ReportingStructure() {
    }

    /**
     * Gets employee associated with ReportingStructure
     *
     * @return Employee object
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Sets employee associated with ReportingStructure
     *
     * @param employee Employee object
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * Gets number of reports for employee in ReportingStructure
     *
     * @return
     */
    public Integer getNumberOfReports() {
        return numberOfReports;
    }

    /**
     * Sets number of reports for Employee in ReportingStructure
     * @param numberOfReports
     */
    public void setNumberOfReports(Integer numberOfReports) {
        this.numberOfReports = numberOfReports;
    }
}
