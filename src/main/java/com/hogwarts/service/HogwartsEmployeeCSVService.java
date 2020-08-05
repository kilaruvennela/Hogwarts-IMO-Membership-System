package com.hogwarts.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hogwarts.helper.HogwartsEmployeeCSVHelper;
import com.hogwarts.model.HogwartsEmployee;
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
		List<Object[]> countBySectionFloorList = hogwartsEmployeeRepository.countBySectionFloor();
		List<Object[]> membAccuracyList = hogwartsEmployeeRepository.membAccuracy();
		List<Object[]> nonMembAccuracyList = hogwartsEmployeeRepository.nonMembAccuracy();
		ByteArrayInputStream inputStream = HogwartsEmployeeCSVHelper.summaryReportCSV(totalCount, countBySectionList,
				countBySectionFloorList, membAccuracyList, nonMembAccuracyList);
		return inputStream;
	}

}
