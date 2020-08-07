package com.hogwarts.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import com.hogwarts.model.HogwartsEmployee;
import com.hogwarts.model.HogwartsEmployeeId;

public class HogwartsEmployeeCSVHelper {

	public static List<HogwartsEmployee> csvToHogwartsEmployees(InputStream inputStream) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<HogwartsEmployee> hogwartsEmployees = new ArrayList<HogwartsEmployee>();

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				// Accessing values by Header names
				String firstName = csvRecord.get("First-name");
				String lastName = csvRecord.get("Last-name");
				String section = csvRecord.get("Section");
				String floor = csvRecord.get("Floor");
				String emailAddress = csvRecord.get("Email-addr");
				String sigFileName = csvRecord.get("Sig-filename");
				String hqMemb = csvRecord.get("HQ-memb");
				String secMemb = csvRecord.get("Sec-memb");
				String treasMemb = csvRecord.get("Treas-memb");
				String partyMemb = csvRecord.get("Party-memb");
				String mailingStatus = csvRecord.get("Mailing-status");
				String caseStatus = csvRecord.get("Case");
				if (partyMemb.length() == 0) {
					if (hqMemb.equals("member") || treasMemb.equals("member") || secMemb.equals("member")) {
						partyMemb = "member";
					} else {
						partyMemb = "non-member";
					}
				}
				if (caseStatus.length() == 0) {
					if (hqMemb.equals("member") && treasMemb.equals("member")) {
						caseStatus = "member-ok";
					} else if (hqMemb.equals("member") && treasMemb.equals("non-member")) {
						caseStatus = "member, missing our copy of card";
					} else if (hqMemb.equals("non-member") && treasMemb.equals("member")) {
						caseStatus = "NM for HQ but we have card";
					} else if (hqMemb.equals("non-member") && treasMemb.equals("non-member")
							&& secMemb.equals("member")) {
						caseStatus = "NM for HQ but on our list";
					} else if (hqMemb.equals("non-member") && treasMemb.equals("non-member")
							&& secMemb.equals("non-member")) {
						caseStatus = "NM-OK";
					} else if (hqMemb.equals("missing") && (treasMemb.equals("member") || secMemb.equals("member"))) {
						caseStatus = "member, not on IMO list";
					} else if (hqMemb.equals("missing") && treasMemb.equals("non-member")
							&& secMemb.equals("non-member")) {
						caseStatus = "NM, not on IMO list";
					}
				}

				if (firstName.length() != 0 && lastName.length() != 0) {
					HogwartsEmployeeId hogwartsEmployeeId = new HogwartsEmployeeId(firstName, lastName);
					HogwartsEmployee hogwartsEmployee = new HogwartsEmployee(hogwartsEmployeeId, section, floor,
							emailAddress, sigFileName, hqMemb, secMemb, treasMemb, partyMemb, mailingStatus,
							caseStatus);
					hogwartsEmployees.add(hogwartsEmployee);
				}
			}
			return hogwartsEmployees;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse Hogwarts Employee Data CSV file: " + e.getMessage());
		}
	}

	public static ByteArrayInputStream HogwartsEmployeeToCSV(List<HogwartsEmployee> hogwartsEmployees) {
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {

			List<String> header = Arrays.asList("First-name", "Last-name", "Section", "Floor", "Email-addr",
					"Sig-filename", "HQ-memb", "Treas-memb", "Sec-memb", "Party-memb", "Mailing-status", "Case");
			csvPrinter.printRecord(header);
			for (HogwartsEmployee hogwartsEmployee : hogwartsEmployees) {
				List<String> data = Arrays.asList(
						String.valueOf(hogwartsEmployee.getHogwartsEmployeeId().getFirstName()),
						String.valueOf(hogwartsEmployee.getHogwartsEmployeeId().getLastName()),
						String.valueOf(hogwartsEmployee.getSection()), String.valueOf(hogwartsEmployee.getFloor()),
						String.valueOf(hogwartsEmployee.getEmailAddress()),
						String.valueOf(hogwartsEmployee.getSignatureFileName()),
						String.valueOf(hogwartsEmployee.getHqMembership()),
						String.valueOf(hogwartsEmployee.getTreasurerMembership()),
						String.valueOf(hogwartsEmployee.getSecretaryMembership()),
						String.valueOf(hogwartsEmployee.getPartyMembership()),
						String.valueOf(hogwartsEmployee.getMailingStatus()),
						String.valueOf(hogwartsEmployee.getCaseStatus()));

				csvPrinter.printRecord(data);
			}

			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Fail to import Hogwarts Employee data to CSV file: " + e.getMessage());
		}
	}

	public static ByteArrayInputStream summaryReportCSV(List<int[]> totalCount, List<Object[]> countBySectionList,
			List<Object[]> countBySection, List<Object[]> countBySectionFloorList, List<Object[]> membAccuracyList,
			List<Object[]> nonMembAccuracyList) {
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {

			List<String> header = Arrays.asList("HOGWARTS", "MEMBERS", "NON-MEM", "TOTAL", "%MEMB");
			csvPrinter.printRecord(header);
			for (int[] count : totalCount) {
				double percentMemb = (double) Math.round(count[0] * 100) / count[2];
				List<String> totalData = Arrays.asList("Total", String.valueOf(count[0]), String.valueOf(count[1]),
						String.valueOf(count[2]), String.valueOf(percentMemb));
				csvPrinter.printRecord(totalData);
			}

			csvPrinter.printRecord();
			csvPrinter.printRecord();

			List<String> header1 = Arrays.asList("SECTION", "MEMBERS", "NON-MEM", "TOTAL", "%MEMB", "", "Floors");
			csvPrinter.printRecord(header1);
			for (Object[] record : countBySectionList) {
				for (Object[] recordCount : countBySection) {
					if (String.valueOf(record[0]).equals(String.valueOf(recordCount[0]))) {
						long membSectionCount = (long) record[1];
						long nonMembSectionCount = (long) record[2];
						long totalSectionCount = membSectionCount + nonMembSectionCount;
						double percentSecMemb = (double) Math.round(membSectionCount * 100) / totalSectionCount;
						List<String> sectionData = Arrays.asList(String.valueOf(record[0]),
								String.valueOf(membSectionCount), String.valueOf(nonMembSectionCount),
								String.valueOf(totalSectionCount), String.valueOf(percentSecMemb), "",
								String.valueOf(recordCount[1]));
						csvPrinter.printRecord(sectionData);
					}
				}

			}

			csvPrinter.printRecord();
			csvPrinter.printRecord();

			List<String> header2 = Arrays.asList("SECTION", "FLOOR", "MEMBERS", "NON-MEM", "TOTAL", "%MEMB");
			csvPrinter.printRecord(header2);
			for (Object[] record : countBySectionFloorList) {
				long membSectionFloorCount = (long) record[2];
				long nonMembSectionFloorCount = (long) record[3];
				long totalSectionFloorCount = membSectionFloorCount + nonMembSectionFloorCount;
				double percentSecFloorMemb = (double) Math.round(membSectionFloorCount * 100) / totalSectionFloorCount;
				List<String> sectionFloorData = Arrays.asList(String.valueOf(record[0]), String.valueOf(record[1]),
						String.valueOf(membSectionFloorCount), String.valueOf(nonMembSectionFloorCount),
						String.valueOf(totalSectionFloorCount), String.valueOf(percentSecFloorMemb));
				csvPrinter.printRecord(sectionFloorData);
			}

			csvPrinter.printRecord();
			csvPrinter.printRecord();

			List<String> header3 = Arrays.asList("ACCURACY");
			csvPrinter.printRecord(header3);
			long totalMemb = 0;
			for (Object[] record : membAccuracyList) {
				totalMemb += (long) record[1];
				List<String> accuracyData = Arrays.asList(String.valueOf(record[0]), String.valueOf(record[1]));
				csvPrinter.printRecord(accuracyData);
			}
			List<String> data = Arrays.asList("TOTAL MEMBERS", String.valueOf(totalMemb));
			csvPrinter.printRecord(data);
			csvPrinter.printRecord();

			long totalNonMemb = 0;
			for (Object[] record : nonMembAccuracyList) {
				totalNonMemb += (long) record[1];
				List<String> accuracyData = Arrays.asList(String.valueOf(record[0]), String.valueOf(record[1]));
				csvPrinter.printRecord(accuracyData);
			}
			List<String> data1 = Arrays.asList("TOTAL NON-MEMBERS", String.valueOf(totalNonMemb));
			csvPrinter.printRecord(data1);
			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Failed to import report data to CSV file: " + e.getMessage());
		}
	}

	public static List<Map<String, String>> employeeData(InputStream inputStream) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<Map<String, String>> employees = new ArrayList<Map<String, String>>();

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				String firstName = csvRecord.get("First-name");
				String lastName = csvRecord.get("Last-name");
				String section = csvRecord.get("Section");
				String floor = csvRecord.get("Floor");
				String emailAddress = csvRecord.get("Email-addr");

				if (firstName.length() != 0 && lastName.length() != 0) {
					Map<String, String> employee = new HashMap<String, String>();
					employee.put("firstName", firstName);
					employee.put("lastName", lastName);
					employee.put("section", section);
					employee.put("floor", floor);
					employee.put("emailAddress", emailAddress);
					employees.add(employee);
				}
			}
			return employees;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse Hogwarts Employee Data CSV file: " + e.getMessage());
		}
	}

	public static ByteArrayInputStream employeeRefreshReportCSV(long initialMasterCount, long newEmployees,
			long seperatedEmployees, long updatedMasterCount, long employeesWithInvalidSecFloor) {
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {

			List<String> row1 = Arrays.asList("Original Number of Employees on master file ",
					String.valueOf(initialMasterCount));
			csvPrinter.printRecord(row1);

			List<String> row2 = Arrays.asList("Number of new employees added ", String.valueOf(newEmployees));
			csvPrinter.printRecord(row2);

			List<String> row3 = Arrays.asList("Number of seperated employees dropped ",
					String.valueOf(seperatedEmployees));
			csvPrinter.printRecord(row3);

			List<String> row4 = Arrays.asList("Number of Employees on master file after updates",
					String.valueOf(updatedMasterCount));
			csvPrinter.printRecord(row4);

			List<String> row5 = Arrays.asList("Number of employees with invalid section/floor ",
					String.valueOf(employeesWithInvalidSecFloor));
			csvPrinter.printRecord(row5);

			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Failed to import employee refresh data to CSV file: " + e.getMessage());
		}
	}

	public static List<Map<String, String>> hqData(InputStream inputStream) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<Map<String, String>> hqStatusRecords = new ArrayList<Map<String, String>>();

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				String firstName = csvRecord.get("First-name");
				String lastName = csvRecord.get("Last-name");
				String hqMemb = csvRecord.get("HQ-memb");
				if (firstName.length() != 0 && lastName.length() != 0) {
					Map<String, String> hqStatusRecord = new HashMap<String, String>();
					hqStatusRecord.put("firstName", firstName);
					hqStatusRecord.put("lastName", lastName);
					hqStatusRecord.put("hqMemb", hqMemb);
					hqStatusRecords.add(hqStatusRecord);
				}
			}
			return hqStatusRecords;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse Hogwarts HQ Data CSV file: " + e.getMessage());
		}
	}

	public static ByteArrayInputStream hqRefreshReportCSV(long notOnMasterFile, long notOnHQList, long changesMade) {
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {

			List<String> row1 = Arrays.asList("Number of employees not on Master File ",
					String.valueOf(notOnMasterFile));
			csvPrinter.printRecord(row1);

			List<String> row2 = Arrays.asList("Number of employees not on IMO HQ list ", String.valueOf(notOnHQList));
			csvPrinter.printRecord(row2);

			List<String> row3 = Arrays.asList("Number of employees whose HQ-Memb status changed  ",
					String.valueOf(changesMade));
			csvPrinter.printRecord(row3);

			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Failed to import HQ List refresh data to CSV file: " + e.getMessage());
		}
	}

	public static List<Map<String, String>> secData(InputStream inputStream) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<Map<String, String>> secStatusRecords = new ArrayList<Map<String, String>>();

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				String firstName = csvRecord.get("First-name");
				String lastName = csvRecord.get("Last-name");
				String emailAddress = csvRecord.get("Email-addr");
				String secMemb = csvRecord.get("Sec-memb");
				if (firstName.length() != 0 && lastName.length() != 0) {
					Map<String, String> secStatusRecord = new HashMap<String, String>();
					secStatusRecord.put("firstName", firstName);
					secStatusRecord.put("lastName", lastName);
					secStatusRecord.put("emailAddress", emailAddress);
					secStatusRecord.put("secMemb", secMemb);
					secStatusRecords.add(secStatusRecord);
				}
			}
			return secStatusRecords;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse Hogwarts Secretary Data CSV file: " + e.getMessage());
		}
	}

	public static ByteArrayInputStream secRefreshReportCSV(long emailChanges, long secMembChanges) {
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {

			List<String> row1 = Arrays.asList("Number of employees whose Emial Address changed ",
					String.valueOf(emailChanges));
			csvPrinter.printRecord(row1);

			List<String> row2 = Arrays.asList("Number of employees whose Secretary Membership changed ",
					String.valueOf(secMembChanges));
			csvPrinter.printRecord(row2);

			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Failed to import Secretary List refresh data to CSV file: " + e.getMessage());
		}
	}

	public static List<Map<String, String>> treasData(InputStream inputStream) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

			List<Map<String, String>> treasStatusRecords = new ArrayList<Map<String, String>>();

			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				String firstName = csvRecord.get("First-name");
				String lastName = csvRecord.get("Last-name");
				String sigFilename = csvRecord.get("Sig-filename");
				if (firstName.length() != 0 && lastName.length() != 0) {
					Map<String, String> treasStatusRecord = new HashMap<String, String>();
					treasStatusRecord.put("firstName", firstName);
					treasStatusRecord.put("lastName", lastName);
					treasStatusRecord.put("sigFilename", sigFilename);
					treasStatusRecords.add(treasStatusRecord);
				}
			}
			return treasStatusRecords;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse Hogwarts Treasurer Data CSV file: " + e.getMessage());
		}
	}

	public static ByteArrayInputStream treasRefreshReportCSV(long sigFileChanges, long treasMembChanges) {
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {

			List<String> row1 = Arrays.asList("Number of employees whose Signature File Name changed ",
					String.valueOf(sigFileChanges));
			csvPrinter.printRecord(row1);

			List<String> row2 = Arrays.asList("Number of employees whose Treasurer Membership changed ",
					String.valueOf(treasMembChanges));
			csvPrinter.printRecord(row2);

			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Failed to import Treasurer List refresh data to CSV file: " + e.getMessage());
		}
	}

}
