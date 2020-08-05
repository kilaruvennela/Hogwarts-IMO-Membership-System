package com.hogwarts.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hogwarts.helper.BackgroundDataCSVHelper;
import com.hogwarts.model.BackgroundData;
import com.hogwarts.repository.BackgroundDataRepository;

@Service
public class BackgroundDataCSVService {

	@Autowired
	BackgroundDataRepository backgroundDataRepository;

	public void save(MultipartFile file) {
		try {
			List<BackgroundData> backgroundDataRecords = BackgroundDataCSVHelper
					.csvToBackgroundData(file.getInputStream());
			backgroundDataRepository.saveAll(backgroundDataRecords);
		} catch (IOException e) {
			throw new RuntimeException("Failed to store Background csv data: " + e.getMessage());
		}
	}

	public List<BackgroundData> getAllBackgroundData() {
		return backgroundDataRepository.findAll();
	}
}
