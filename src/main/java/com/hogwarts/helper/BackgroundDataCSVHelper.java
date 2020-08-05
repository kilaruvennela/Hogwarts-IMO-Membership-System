package com.hogwarts.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.hogwarts.model.BackgroundData;
import com.hogwarts.model.BackgroundDataId;

public class BackgroundDataCSVHelper {

	public static List<BackgroundData> csvToBackgroundData(InputStream inputStream) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<BackgroundData> backgroundDataRecords = new ArrayList<BackgroundData>();

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				// Accessing values by Header names
				String section = csvRecord.get("SECTION");
				String floor = csvRecord.get("FLOOR");

				if (section.length() != 0 && floor.length() != 0) {
					BackgroundDataId backgroundDataId = new BackgroundDataId(section, floor);
					BackgroundData backgroundDataRecord = new BackgroundData(backgroundDataId);
					backgroundDataRecords.add(backgroundDataRecord);
				}
			}
			return backgroundDataRecords;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse Background Data CSV file: " + e.getMessage());
		}
	}

}
