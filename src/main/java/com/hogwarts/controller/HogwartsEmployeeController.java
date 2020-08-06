package com.hogwarts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.hogwarts.message.ResponseMessage;
import com.hogwarts.service.HogwartsEmployeeCSVService;

@CrossOrigin("http://localhost:8081")
@Controller
@RequestMapping("/api/hogwartsemployee")
public class HogwartsEmployeeController {

	@Autowired
	HogwartsEmployeeCSVService hogwartsEmployeeCSVService;

	@PostMapping("/upload")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
		String message = "";

		try {
			hogwartsEmployeeCSVService.save(file);

			message = "Uploaded the file successfully: " + file.getOriginalFilename();
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
	}

	@GetMapping("/download")
	public ResponseEntity<Resource> getMasterFile() {
		String filename = "MasterFileFromDB.csv";
		InputStreamResource file = new InputStreamResource(hogwartsEmployeeCSVService.loadHogwartsEmployees());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);
	}

	@GetMapping("report/download")
	public ResponseEntity<Resource> getFile() {
		String filename = "SummaryReport.csv";
		InputStreamResource file = new InputStreamResource(hogwartsEmployeeCSVService.loadReport());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);
	}

	@PostMapping("/employee-refresh")
	public ResponseEntity<Resource> uploadEmployeeFile(@RequestParam("file") MultipartFile file) {
		String filename = "EmployeeRefreshReport.csv";
		InputStreamResource reportFile = new InputStreamResource(hogwartsEmployeeCSVService.updateEmployee(file));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(reportFile);
	}

	@PostMapping("/imohq-refresh")
	public ResponseEntity<Resource> uploadHQRefreshFile(@RequestParam("file") MultipartFile file) {
		String filename = "HQRefreshReport.csv";
		InputStreamResource reportFile = new InputStreamResource(hogwartsEmployeeCSVService.updateHQStatus(file));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(reportFile);
	}

	@PostMapping("/sec-refresh")
	public ResponseEntity<Resource> uploadSecRefreshFile(@RequestParam("file") MultipartFile file) {
		String filename = "SecRefreshReport.csv";
		InputStreamResource reportFile = new InputStreamResource(hogwartsEmployeeCSVService.updateSecStatus(file));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(reportFile);
	}

	@PostMapping("/treas-refresh")
	public ResponseEntity<Resource> uploadTreasRefreshFile(@RequestParam("file") MultipartFile file) {
		String filename = "TreasRefreshReport.csv";
		InputStreamResource reportFile = new InputStreamResource(hogwartsEmployeeCSVService.updateTreasStatus(file));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(reportFile);
	}

}
