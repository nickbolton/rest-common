package com.tubebreakup.model;

import java.util.Comparator;

public class CreatedAtComparator implements Comparator<BaseModel> {
	
	private Boolean descending;

	public Boolean getDescending() {
		return descending;
	}

	public void setDescending(Boolean descending) {
		this.descending = descending;
	}

	public CreatedAtComparator(Boolean descending) {
		super();
		this.descending = descending;
	}

	@Override
	public int compare(BaseModel o1, BaseModel o2) {
		if (descending) {			
			return o2.getCreatedAt().compareTo(o1.getCreatedAt());
		}
		return o1.getCreatedAt().compareTo(o2.getCreatedAt());
	}
}
