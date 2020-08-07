package com.hogwarts.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hogwarts.helper.HogwartsEmployeeCSVHelper;
import com.hogwarts.model.BackgroundDataId;
import com.hogwarts.model.HogwartsEmployee;
import com.hogwarts.model.HogwartsEmployeeId;
import com.hogwarts.repository.BackgroundDataRepository;
import com.hogwarts.repository.HogwartsEmployeeRepository;

@Service
public class HogwartsEmployeeCSVService {

	@Autowired
	HogwartsEmployeeRepository hogwartsEmployeeRepository;

	@Autowired
	BackgroundDataRepository backgroundDataRepository;

	public void save(MultipartFile file) {
		try {
			List<HogwartsEmployee> hogwartsEmployees = HogwartsEmployeeCSVHelper
					.csvToHogwartsEmployees(file.getInputStream());
			hogwartsEmployeeRepository.saveAll(hogwartsEmployees);
		} catch (IOException e) {
			throw new RuntimeException("Failed to store Hogwarts Employee csv data: " + e.getMessage());
		}
	}

	public ByteArrayInputStream loadHogwartsEmployees() {
		List<HogwartsEmployee> hogwartsEmployees = hogwartsEmployeeRepository
				.findAll(Sort.by("section").and(Sort.by("floor")));

		ByteArrayInputStream inputStream = HogwartsEmployeeCSVHelper.HogwartsEmployeeToCSV(hogwartsEmployees);
		return inputStream;
	}

	public ByteArrayInputStream loadReport() {
		List<int[]> totalCount = hogwartsEmployeeRepository.countByCaseStatus();
		List<Object[]> countBySectionList = hogwartsEmployeeRepository.countBySection();
		List<Object[]> countBySection = backgroundDataRepository.countBySection();
		List<Object[]> countBySectionFloorList = hogwartsEmployeeRepository.countBySectionFloor();
		List<Object[]> membAccuracyList = hogwartsEmployeeRepository.membAccuracy();
		List<Object[]> nonMembAccuracyList = hogwartsEmployeeRepository.nonMembAccuracy();
		ByteArrayInputStream inputStream = HogwartsEmployeeCSVHelper.summaryReportCSV(totalCount, countBySectionList,
				countBySection, countBySectionFloorList, membAccuracyList, nonMembAccuracyList);
		return inputStream;
	}

	public ByteArrayInputStream updateEmployee(MultipartFile file) {
		try {
			long initialMasterCount = hogwartsEmployeeRepository.count();
			long newEmployees = 0;
			long seperatedEmployees = 0;
			long employeesWithInvalidSecFloor = 0;
			List<Map<String, String>> refreshEmployees = HogwartsEmployeeCSVHelper.employeeData(file.getInputStream());
			List<HogwartsEmployeeId> refreshEmployeeIds = new ArrayList<>();
			for (Map<String, String> refreshEmployee : refreshEmployees) {
				HogwartsEmployeeId refreshEmployeeId = new HogwartsEmployeeId(refreshEmployee.get("firstName"),
						refreshEmployee.get("lastName"));
				refreshEmployeeIds.add(refreshEmployeeId);
				Optional<HogwartsEmployee> optionalEmployee = hogwartsEmployeeRepository.findById(refreshEmployeeId);
				if (optionalEmployee.isPresent()) {
					HogwartsEmployee masterEmployee = optionalEmployee.get();
					if (!(masterEmployee.getSection().equals(refreshEmployee.get("section"))
							|| masterEmployee.getFloor().equals(refreshEmployee.get("floor")))) {
						masterEmployee.setSection(refreshEmployee.get("section"));
						masterEmployee.setFloor(refreshEmployee.get("floor"));
						hogwartsEmployeeRepository.save(masterEmployee);
					}
				} else {
					newEmployees++;
					HogwartsEmployee masterEmployee = new HogwartsEmployee(refreshEmployeeId,
							refreshEmployee.get("section"), refreshEmployee.get("floor"),
							refreshEmployee.get("emailAddress"), "", "non-member", "non-member", "non-member",
							"non-member", "normal", "NM-OK");
					hogwartsEmployeeRepository.save(masterEmployee);
				}
				BackgroundDataId backgroundDataId = new BackgroundDataId(refreshEmployee.get("section"),
						refreshEmployee.get("floor"));
				if (!backgroundDataRepository.findById(backgroundDataId).isPresent()) {
					employeesWithInvalidSecFloor++;
				}
			}
			// To remove seperated employees
			List<HogwartsEmployeeId> masterEmployeeIds = hogwartsEmployeeRepository.findAllIds();
			for (HogwartsEmployeeId id : masterEmployeeIds) {
				if (!refreshEmployeeIds.contains(id)) {
					hogwartsEmployeeRepository.deleteById(id);
					seperatedEmployees++;
				}
			}
			ByteArrayInputStream inputStream = HogwartsEmployeeCSVHelper.employeeRefreshReportCSV(initialMasterCount,
					newEmployees, seperatedEmployees, hogwartsEmployeeRepository.count(), employeesWithInvalidSecFloor);
			return inputStream;

		} catch (IOException e) {
			throw new RuntimeException("Failed to update Hogwarts Employee csv data: " + e.getMessage());
		}
	}

	public ByteArrayInputStream updateHQStatus(MultipartFile file) {
		try {
			long notOnMasterFile = 0;
			long notOnHQList = 0;
			long changesMade = 0;
			List<HogwartsEmployeeId> masterEmployeeIds = hogwartsEmployeeRepository.findAllIds();
			List<Map<String, String>> hqStatusRecords = HogwartsEmployeeCSVHelper.hqData(file.getInputStream());
			List<HogwartsEmployeeId> hqRecordEmployeeIds = new ArrayList<>();
			for (Map<String, String> hqStatusRecord : hqStatusRecords) {
				HogwartsEmployeeId hqRecordEmployeeId = new HogwartsEmployeeId(hqStatusRecord.get("firstName"),
						hqStatusRecord.get("lastName"));
				hqRecordEmployeeIds.add(hqRecordEmployeeId);
				if (!masterEmployeeIds.contains(hqRecordEmployeeId)) {
					notOnMasterFile++;
				} else {
					HogwartsEmployee masterEmployee = hogwartsEmployeeRepository.findById(hqRecordEmployeeId).get();
					String masterHQStatus = masterEmployee.getHqMembership();
					String hqStatus = hqStatusRecord.get("hqMemb");
					if (!masterHQStatus.equals(hqStatus)) {
						masterEmployee.setHqMembership(hqStatus);
						String caseStatus = resetCaseStatus(masterEmployee);
						masterEmployee.setCaseStatus(caseStatus);
						hogwartsEmployeeRepository.save(masterEmployee);
						changesMade++;
					}
				}
			}

			for (HogwartsEmployeeId masterEmployeeId : masterEmployeeIds) {
				if (!hqRecordEmployeeIds.contains(masterEmployeeId)) {
					notOnHQList++;
					HogwartsEmployee employee = hogwartsEmployeeRepository.findById(masterEmployeeId).get();
					employee.setHqMembership("missing");
					String caseStatus = resetCaseStatus(employee);
					employee.setCaseStatus(caseStatus);
					hogwartsEmployeeRepository.save(employee);

				}
			}
			ByteArrayInputStream inputStream = HogwartsEmployeeCSVHelper.hqRefreshReportCSV(notOnMasterFile,
					notOnHQList, changesMade);
			return inputStream;
		} catch (IOException e) {
			throw new RuntimeException("Failed to update Hogwarts Employee csv data: " + e.getMessage());
		}

	}

	public ByteArrayInputStream updateSecStatus(MultipartFile file) {
		try {
			long emailChanges = 0;
			long secMembChanges = 0;
			List<HogwartsEmployeeId> masterEmployeeIds = hogwartsEmployeeRepository.findAllIds();
			List<Map<String, String>> secStatusRecords = HogwartsEmployeeCSVHelper.secData(file.getInputStream());
			for (Map<String, String> secStatusRecord : secStatusRecords) {
				HogwartsEmployeeId secRecordEmployeeId = new HogwartsEmployeeId(secStatusRecord.get("firstName"),
						secStatusRecord.get("lastName"));
				if (masterEmployeeIds.contains(secRecordEmployeeId)) {
					HogwartsEmployee employee = hogwartsEmployeeRepository.findById(secRecordEmployeeId).get();
					if (!employee.getEmailAddress().equals(secStatusRecord.get("emailAddress"))) {
						emailChanges++;
						employee.setEmailAddress(secStatusRecord.get("emailAddress"));

					}
					if (!employee.getSecretaryMembership().equals(secStatusRecord.get("secMemb"))) {
						secMembChanges++;
						employee.setSecretaryMembership(secStatusRecord.get("secMemb"));
						String caseStatus = resetCaseStatus(employee);
						employee.setCaseStatus(caseStatus);
					}
					hogwartsEmployeeRepository.save(employee);
				}
			}

			ByteArrayInputStream inputStream = HogwartsEmployeeCSVHelper.secRefreshReportCSV(emailChanges,
					secMembChanges);
			return inputStream;
		} catch (

		IOException e) {
			throw new RuntimeException("Failed to update Hogwarts Employee csv data: " + e.getMessage());
		}

	}

	public ByteArrayInputStream updateTreasStatus(MultipartFile file) {
		try {
			long treasMembChanges = 0;
			long sigFileChanges = 0;
			List<HogwartsEmployeeId> masterEmployeeIds = hogwartsEmployeeRepository.findAllIds();
			List<Map<String, String>> treasStatusRecords = HogwartsEmployeeCSVHelper.treasData(file.getInputStream());
			for (Map<String, String> treasStatusRecord : treasStatusRecords) {
				HogwartsEmployeeId treasRecordEmployeeId = new HogwartsEmployeeId(treasStatusRecord.get("firstName"),
						treasStatusRecord.get("lastName"));
				if (masterEmployeeIds.contains(treasRecordEmployeeId)) {
					HogwartsEmployee employee = hogwartsEmployeeRepository.findById(treasRecordEmployeeId).get();
					if (!employee.getSignatureFileName().equals(treasStatusRecord.get("sigFilename"))) {
						sigFileChanges++;
						employee.setSignatureFileName(treasStatusRecord.get("sigFilename"));

					}
					if (employee.getTreasurerMembership().equals("non-member")) {
						treasMembChanges++;
						employee.setTreasurerMembership("member");
					}
					hogwartsEmployeeRepository.save(employee);
				}
			}

			ByteArrayInputStream inputStream = HogwartsEmployeeCSVHelper.treasRefreshReportCSV(sigFileChanges,
					treasMembChanges);
			return inputStream;
		} catch (

		IOException e) {
			throw new RuntimeException("Failed to update Hogwarts Employee csv data: " + e.getMessage());
		}

	}

	public String resetCaseStatus(HogwartsEmployee employee) {
		String hqMemb = employee.getHqMembership();
		String treasMemb = employee.getTreasurerMembership();
		String secMemb = employee.getSecretaryMembership();
		String caseStatus = employee.getCaseStatus();
		if (hqMemb.equals("member") && treasMemb.equals("member")) {
			caseStatus = "member-ok";
		} else if (hqMemb.equals("member") && treasMemb.equals("non-member")) {
			caseStatus = "member, missing our copy of card";
		} else if (hqMemb.equals("non-member") && treasMemb.equals("member")) {
			caseStatus = "NM for HQ but we have card";
		} else if (hqMemb.equals("non-member") && treasMemb.equals("non-member") && secMemb.equals("member")) {
			caseStatus = "NM for HQ but on our list";
		} else if (hqMemb.equals("non-member") && treasMemb.equals("non-member") && secMemb.equals("non-member")) {
			caseStatus = "NM-OK";
		} else if (hqMemb.equals("missing") && (treasMemb.equals("member") || secMemb.equals("member"))) {
			caseStatus = "member, not on IMO list";
		} else if (hqMemb.equals("missing") && treasMemb.equals("non-member") && secMemb.equals("non-member")) {
			caseStatus = "NM, not on IMO list";
		}

		return caseStatus;
	}

}
