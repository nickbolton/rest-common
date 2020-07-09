package com.tubebreakup.model;

import com.fasterxml.jackson.databind.ser.FilterProvider;

public interface PropertyFilterProvider {
	public FilterProvider defaultFilterProvider();
	public FilterProvider shallowFilterProvider();
}
