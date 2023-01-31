package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/employee/{id}/reportingStructure";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee = updateEmployee(readEmployee);

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testNumberOfReports() {
        Employee root = createEmployeeGraph();
        Employee root_A = root.getDirectReports().get(0);

        ReportingStructure expectedReportingStructure = new ReportingStructure();
        expectedReportingStructure.setNumberOfReports(4);
        expectedReportingStructure.setEmployee(root_A);

        // fetch reporting structure of root_A
        ReportingStructure actualReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, root_A.getEmployeeId()).getBody();

        assertEquals(4, actualReportingStructure.getNumberOfReports().intValue());
        assertEmployeeEquivalence(root_A, actualReportingStructure.getEmployee());
    }

    @Test
    public void testNumberOfReports_with_multiple_parents() {
        // Testing edge case when multiple parent level employees are supported.
        Employee root = createEmployeeGraphWithMultipleParents();
        Employee root_A = root.getDirectReports().get(0);

        // fetch reporting structure of root_A
        ReportingStructure actualReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, root_A.getEmployeeId()).getBody();

        assertEquals(6, actualReportingStructure.getNumberOfReports().intValue());
        assertEmployeeEquivalence(root_A, actualReportingStructure.getEmployee());

        // fetch reporting structure of root
        actualReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, root.getEmployeeId()).getBody();

        assertEquals(7, actualReportingStructure.getNumberOfReports().intValue());
        assertEmployeeEquivalence(root, actualReportingStructure.getEmployee());
    }

    private Employee updateEmployee(Employee input) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<Employee>(input, headers),
                Employee.class,
                input.getEmployeeId()).getBody();
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }

    private Employee createEmployeeGraph() {
        // Create employee tree in database
        //      root
        //      /   \
        //     A     B
        //    /\     |
        //  C   D    E
        Employee root = restTemplate.postForEntity(employeeUrl, new Employee(), Employee.class).getBody();
        Employee root_A = restTemplate.postForEntity(employeeUrl, new Employee(), Employee.class).getBody();
        Employee root_B = restTemplate.postForEntity(employeeUrl, new Employee(), Employee.class).getBody();
        Employee root_A_C = restTemplate.postForEntity(employeeUrl, new Employee(), Employee.class).getBody();
        Employee root_A_D = restTemplate.postForEntity(employeeUrl, new Employee(), Employee.class).getBody();
        Employee root_A_D_F = restTemplate.postForEntity(employeeUrl, new Employee(), Employee.class).getBody();
        Employee root_A_D_G = restTemplate.postForEntity(employeeUrl, new Employee(), Employee.class).getBody();
        Employee root_B_E = restTemplate.postForEntity(employeeUrl, new Employee(), Employee.class).getBody();

        root.setDirectReports(Arrays.asList(root_A, root_B));
        root_A.setDirectReports(Arrays.asList(root_A_C, root_A_D));
        root_B.setDirectReports(Arrays.asList(root_B_E));
        root_A_D.setDirectReports(Arrays.asList(root_A_D_F, root_A_D_G));

        // Update direct reports in database
        updateEmployee(root);
        updateEmployee(root_A);
        updateEmployee(root_B);
        updateEmployee(root_A_D);

        return root;
    }

    private Employee createEmployeeGraphWithMultipleParents() {
        Employee root = createEmployeeGraph();
        Employee root_A = root.getDirectReports().get(0);

        // Adding root_B as direct report of root_A
        //      root
        //      /   \
        //     A ->  B
        //    /\     |
        //  C   D    E
        //     / \
        //    F   G
        ArrayList<Employee> updatedDirectReports = new ArrayList(root_A.getDirectReports());
        updatedDirectReports.add(root.getDirectReports().get(1));
        root_A.setDirectReports(updatedDirectReports);

        updateEmployee(root_A);
        return root;
    }
}
