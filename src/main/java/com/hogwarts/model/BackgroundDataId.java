package com.hogwarts.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class BackgroundDataId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String section;

	private String floor;

	public BackgroundDataId() {

	}

	public BackgroundDataId(String section, String floor) {
		this.section = section;
		this.floor = floor;
	}

	public String getSection() {
		return section;
	}

	public String getFloor() {
		return floor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((floor == null) ? 0 : floor.hashCode());
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BackgroundDataId other = (BackgroundDataId) obj;
		if (floor == null) {
			if (other.floor != null)
				return false;
		} else if (!floor.equals(other.floor))
			return false;
		if (section == null) {
			if (other.section != null)
				return false;
		} else if (!section.equals(other.section))
			return false;
		return true;
	}

}
