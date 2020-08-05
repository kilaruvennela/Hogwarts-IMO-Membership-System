package com.hogwarts.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class BackgroundData {

	@EmbeddedId
	private BackgroundDataId backgroundDataId;

	public BackgroundData() {

	}

	public BackgroundData(BackgroundDataId backgroundDataId) {
		super();
		this.backgroundDataId = backgroundDataId;
	}

	public BackgroundDataId getBackgroundDataId() {
		return backgroundDataId;
	}

	public void setBackgroundDataId(BackgroundDataId backgroundDataId) {
		this.backgroundDataId = backgroundDataId;
	}

	@Override
	public String toString() {
		return "BackgroundData [backgroundDataId=" + backgroundDataId + "]";
	}

}
