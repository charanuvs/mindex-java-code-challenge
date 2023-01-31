package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationIdUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Date testDate = new Date("01/12/2020");
        Compensation testCompensation = new Compensation("testId", testDate, 1000D);

        // Create checks
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        assertNotNull(createdCompensation.getEmployeeId());
        assertCompensationEquivalence(testCompensation, createdCompensation);

        // Insert new compensation for same employee with different effectiveDate
        Date newDate = new Date("01/12/2022");
        testCompensation.setEffectiveDate(newDate);
        testCompensation.setSalary(2000D);
        createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();

        // Read checks
        Compensation[] readCompensationList = restTemplate.getForEntity(compensationIdUrl, Compensation[].class, createdCompensation.getEmployeeId()).getBody();

        assertEquals(2, readCompensationList.length);
        assertCompensationEquivalence(testCompensation, createdCompensation);
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
    }
}
