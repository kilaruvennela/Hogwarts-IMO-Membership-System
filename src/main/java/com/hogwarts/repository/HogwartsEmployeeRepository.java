package com.hogwarts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hogwarts.model.HogwartsEmployee;
import com.hogwarts.model.HogwartsEmployeeId;

public interface HogwartsEmployeeRepository extends JpaRepository<HogwartsEmployee, HogwartsEmployeeId> {

	// public List<HogwartsEmployee> findAllByOrderBySectionAsc();

	@Query("SELECT COUNT(CASE WHEN he.caseStatus LIKE 'member%' THEN 1 END), COUNT(CASE WHEN he.caseStatus LIKE 'NM%' THEN 1 END), COUNT(he) FROM HogwartsEmployee he")
	public List<int[]> countByCaseStatus();

	@Query("SELECT he.section, COUNT(CASE WHEN he.caseStatus LIKE 'member%' THEN 1 END), COUNT(CASE WHEN he.caseStatus LIKE 'NM%' THEN 1 END) FROM HogwartsEmployee he GROUP BY he.section ORDER BY he.section")
	public List<Object[]> countBySection();

	@Query("SELECT he.section, he.floor, COUNT(CASE WHEN he.caseStatus LIKE 'member%' THEN 1 END), COUNT(CASE WHEN he.caseStatus LIKE 'NM%' THEN 1 END) FROM HogwartsEmployee he GROUP BY he.section, he.floor ORDER BY he.section, he.floor")
	public List<Object[]> countBySectionFloor();

	@Query("SELECT DISTINCT he.caseStatus, COUNT(he) FROM HogwartsEmployee he WHERE he.caseStatus LIKE 'member%' GROUP BY he.caseStatus")
	public List<Object[]> membAccuracy();

	@Query("SELECT DISTINCT he.caseStatus, COUNT(he) FROM HogwartsEmployee he WHERE he.caseStatus LIKE 'NM%' GROUP BY he.caseStatus")
	public List<Object[]> nonMembAccuracy();

	@Query("SELECT he.hogwartsEmployeeId FROM HogwartsEmployee he")
	public List<HogwartsEmployeeId> findAllIds();
}
