package com.hogwarts.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class HogwartsEmployee {

	@EmbeddedId
	HogwartsEmployeeId hogwartsEmployeeId;

	private String section;

	private String floor;

	private String emailAddress;

	@Column(name = "sigFilename")
	private String signatureFileName;

	@Column(name = "hqMemb")
	private String hqMembership;

	@Column(name = "secMemb")
	private String secretaryMembership;

	@Column(name = "treasMemb")
	private String treasurerMembership;

	@Column(name = "partyMemb")
	private String partyMembership;

	@Column(name = "mailingStatus")
	private String mailingStatus;

	private String caseStatus;

	public HogwartsEmployee() {

	}

	public HogwartsEmployee(HogwartsEmployeeId hogwartsEmployeeId, String section, String floor, String emailAddress,
			String signatureFileName, String hqMembership, String secretaryMembership, String treasurerMembership,
			String partyMembership, String mailingStatus, String caseStatus) {
		super();
		this.hogwartsEmployeeId = hogwartsEmployeeId;
		this.section = section;
		this.floor = floor;
		this.emailAddress = emailAddress;
		this.signatureFileName = signatureFileName;
		this.hqMembership = hqMembership;
		this.secretaryMembership = secretaryMembership;
		this.treasurerMembership = treasurerMembership;
		this.partyMembership = partyMembership;
		this.mailingStatus = mailingStatus;
		this.caseStatus = caseStatus;
	}

	public HogwartsEmployeeId getHogwartsEmployeeId() {
		return hogwartsEmployeeId;
	}

	public void setHogwartsEmployeeId(HogwartsEmployeeId hogwartsEmployeeId) {
		this.hogwartsEmployeeId = hogwartsEmployeeId;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getSignatureFileName() {
		return signatureFileName;
	}

	public void setSignatureFileName(String signatureFileName) {
		this.signatureFileName = signatureFileName;
	}

	public String getHqMembership() {
		return hqMembership;
	}

	public void setHqMembership(String hqMembership) {
		this.hqMembership = hqMembership;
	}

	public String getSecretaryMembership() {
		return secretaryMembership;
	}

	public void setSecretaryMembership(String secretaryMembership) {
		this.secretaryMembership = secretaryMembership;
	}

	public String getTreasurerMembership() {
		return treasurerMembership;
	}

	public void setTreasurerMembership(String treasurerMembership) {
		this.treasurerMembership = treasurerMembership;
	}

	public String getPartyMembership() {
		return partyMembership;
	}

	public void setPartyMembership(String partyMembership) {
		this.partyMembership = partyMembership;
	}

	public String getMailingStatus() {
		return mailingStatus;
	}

	public void setMailingStatus(String mailingStatus) {
		this.mailingStatus = mailingStatus;
	}

	public String getCaseStatus() {
		return caseStatus;
	}

	public void setCaseStatus(String caseStatus) {
		this.caseStatus = caseStatus;
	}

	@Override
	public String toString() {
		return "HogwartsEmployee [hogwartsEmployeeId=" + hogwartsEmployeeId + ", section=" + section + ", floor="
				+ floor + ", emailAddress=" + emailAddress + ", signatureFileName=" + signatureFileName
				+ ", hqMembership=" + hqMembership + ", secretaryMembership=" + secretaryMembership
				+ ", treasurerMembership=" + treasurerMembership + ", partyMembership=" + partyMembership
				+ ", mailingStatus=" + mailingStatus + ", caseStatus=" + caseStatus + "]";
	}

}
