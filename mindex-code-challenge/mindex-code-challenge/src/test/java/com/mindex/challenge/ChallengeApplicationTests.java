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

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.service.CompensationService;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChallengeApplicationTests {


	@Autowired
	private ReportingStructureService reportingStructureService;
	@Autowired
	private CompensationService compensationService;

	// Mock Objects
	@MockBean
	private EmployeeService employeeService;
	@MockBean
	private CompensationRepository compensationRepository;

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
	private static Employee idWithReports(String id, List<String> directReportIds) {
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
	public void noReportsReturnsZero() {
		// graph: {A}
		Employee a = idWithReports("A", null);
		when(employeeService.read("A")).thenReturn(a);

		ReportingStructure rs = reportingStructureService.read("A");
		assertEquals(0, rs.getNumOfReports());
	}

	@Test
	public void onlyDirectReports() {
		// Graph A -> {B, C}
		Employee a = idWithReports("A", Arrays.asList("B", "C"));
		Employee b = idWithReports("B", null);
		Employee c = idWithReports("C", null);

		when(employeeService.read("A")).thenReturn(a);
		when(employeeService.read("B")).thenReturn(b);
		when(employeeService.read("C")).thenReturn(c);

		ReportingStructure rs = reportingStructureService.read("A");
		assertEquals(2, rs.getNumOfReports());
	}

	@Test
	public void directAndIndirect() {
		// Graph A -> {B -> {C, D}}
		Employee a = idWithReports("A", Collections.singletonList("B"));
		Employee b = idWithReports("B", Arrays.asList("C", "D"));
		Employee c = idWithReports("C", null);
		Employee d = idWithReports("D", null);

		when(employeeService.read("A")).thenReturn(a);
		when(employeeService.read("B")).thenReturn(b);
		when(employeeService.read("C")).thenReturn(c);
		when(employeeService.read("D")).thenReturn(d);

		ReportingStructure rs = reportingStructureService.read("A");
		assertEquals(3, rs.getNumOfReports());
	}

	@Test
	public void reportingStructureCycle() {
		// graph A -> {B}
		// 		 B -> {A}
		Employee a = idWithReports("A", Collections.singletonList("B"));
		Employee b = idWithReports("B", Collections.singletonList("A"));

		when(employeeService.read("A")).thenReturn(a);
		when(employeeService.read("B")).thenReturn(b);

		ReportingStructure rs = reportingStructureService.read("A");
		assertEquals(1, rs.getNumOfReports());
	}
	@Test
	public void createNewCompRecord() {
		// build new compensation
		String employeeId = "E1";
		Compensation comp = new Compensation();
		comp.setEmployeeId(employeeId);
		comp.setSalary(120000.0);
		comp.setEffectiveDate(LocalDate.of(2025, 8, 1));

		// Check to make sure employee exists
		when(employeeService.read(employeeId)).thenReturn(new Employee());

		// There should be no comp on this employee right now
		when(compensationRepository.findByEmployeeId(employeeId)).thenReturn(null);
		when(compensationRepository.save(comp)).thenReturn(comp);
		Compensation saved = compensationService.create(comp);

		// Need to verify all the information is stored accurately
		assertEquals(employeeId, saved.getEmployeeId());
		assertEquals(120000.0, saved.getSalary());
		assertEquals(LocalDate.of(2025, 8, 1), saved.getEffectiveDate());
	}

	@Test
	public void replaceExistingComp() {
		// build new compensation
		String employeeId = "E2";
		Compensation existing = new Compensation();
		existing.setEmployeeId(employeeId);
		existing.setSalary(90000.0);
		existing.setEffectiveDate(LocalDate.of(2024, 1, 1));
		
		// replace the existing compensation
		Compensation comp = new Compensation();
		comp.setEmployeeId(employeeId);
		comp.setSalary(120000.0);
		comp.setEffectiveDate(LocalDate.of(2025, 8, 1));

		when(employeeService.read(employeeId)).thenReturn(new Employee());
		when(compensationRepository.findByEmployeeId(employeeId)).thenReturn(existing);
		when(compensationRepository.save(comp)).thenReturn(comp);
		
		Compensation saved = compensationService.create(comp);

		assertEquals(employeeId, saved.getEmployeeId());
		assertEquals(120000.0, saved.getSalary());
		assertEquals(LocalDate.of(2025, 8, 1), saved.getEffectiveDate());
	}

	// Test errors
	@Test(expected = RuntimeException.class)
	public void missingEmployeeId() {
		Compensation comp = new Compensation();
		comp.setSalary(100000.0);
		comp.setEffectiveDate(LocalDate.now());
		compensationService.create(comp);
	}

	@Test(expected = RuntimeException.class)
	public void missingEffectiveDate() {
		Compensation comp = new Compensation();
		comp.setEmployeeId("E3");
		comp.setSalary(100000.0);
		compensationService.create(comp);
	}

	@Test(expected = RuntimeException.class)
	public void negativeSalary() {
		Compensation comp = new Compensation();
		comp.setEmployeeId("E4");
		comp.setSalary(-1.0);
		comp.setEffectiveDate(LocalDate.now());
		compensationService.create(comp);
	}
}
