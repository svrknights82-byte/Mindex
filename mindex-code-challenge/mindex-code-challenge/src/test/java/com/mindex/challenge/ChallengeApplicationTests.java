package com.mindex.challenge;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChallengeApplicationTests {


	@Autowired
	private ReportingStructureService reportingStructureService;

	@MockBean
	private EmployeeService employeeService;

	// Mock Objects

	/*
	To simulate a real run, the employee object only will have the id when called using
	getDirectReports.  This first object will be a similar object, only with its id.
	 */
	private static Employee id_only(String id) {
		Employee mockEmployee = new Employee();
		mockEmployee.setEmployeeId(id);
		return mockEmployee;
	}

	/*
	This will create the full mock employee
	 */
	private static Employee id_with_reports(String id, List<String> directReportIds) {
		Employee mockEmployee = new Employee();
		mockEmployee.setEmployeeId(id);
		if (directReportIds == null) {
			mockEmployee.setDirectReports(null);
		} else {
			mockEmployee.setDirectReports(directReportIds.stream()
					.map(ChallengeApplicationTests::id_only)
					.collect(Collectors.toList()));
		}
		return mockEmployee;
	}


	@Test
	public void noReports_returnsZero() {
		// graph: {A}
		Employee a = id_with_reports("A", null);
		when(employeeService.read("A")).thenReturn(a);

		ReportingStructure rs = reportingStructureService.read("A");
		assertEquals(0, rs.getNumOfReports());
	}

	@Test
	public void onlyDirectReports() {
		// Graph A -> {B, C}
		Employee a = id_with_reports("A", Arrays.asList("B", "C"));
		Employee b = id_with_reports("B", null);
		Employee c = id_with_reports("C", null);

		when(employeeService.read("A")).thenReturn(a);
		when(employeeService.read("B")).thenReturn(b);
		when(employeeService.read("C")).thenReturn(c);

		ReportingStructure rs = reportingStructureService.read("A");
		assertEquals(2, rs.getNumOfReports());
	}

	@Test
	public void directAndIndirect() {
		// Graph A -> {B -> {C, D}}
		Employee a = id_with_reports("A", Collections.singletonList("B"));
		Employee b = id_with_reports("B", Arrays.asList("C", "D"));
		Employee c = id_with_reports("C", null);
		Employee d = id_with_reports("D", null);

		when(employeeService.read("A")).thenReturn(a);
		when(employeeService.read("B")).thenReturn(b);
		when(employeeService.read("C")).thenReturn(c);
		when(employeeService.read("D")).thenReturn(d);

		ReportingStructure rs = reportingStructureService.read("A");
		assertEquals(3, rs.getNumOfReports());
	}

	@Test
	public void reportingStructure_cycle() {
		// graph A -> {B}
		// 		 B -> {A}
		Employee a = id_with_reports("A", Collections.singletonList("B"));
		Employee b = id_with_reports("B", Collections.singletonList("A"));

		when(employeeService.read("A")).thenReturn(a);
		when(employeeService.read("B")).thenReturn(b);

		ReportingStructure rs = reportingStructureService.read("A");
		assertEquals(1, rs.getNumOfReports());
	}
	@Test
	public void contextLoads() {
	}

}
