package com.ntechlab;

import java.util.*;

public class IdentifyParams {
	/* names of the fields are kept as close to JSON API as possible (no camelCase here) */
	private BBox bbox;
	private Double threshold;
	private Integer n;
	private String mf_selector;

	public IdentifyParams() { }

	public IdentifyParams setBBox(BBox box) {
		bbox = box;
		return this;
	}

	public IdentifyParams setBBox(int x1, int y1, int x2, int y2) {
		return setBBox(new BBox(x1, y1, x2, y2));
	}

	public IdentifyParams setThreshold(double value) {
		threshold = new Double(value);
		return this;
	}

	public IdentifyParams setN(int value) {
		n = new Integer(value);
		return this;
	}

	public IdentifyParams setMFSelector(String value) {
		mf_selector = value;
		return this;
	}

	/* Build map from parameters */
	protected Map<String, Object> getFields(Map<String, Object> p) {
		if (n != null)
			p.put("n", n);
		if (bbox != null) {
			p.put("x1", bbox.x1);
			p.put("y1", bbox.y1);
			p.put("x2", bbox.x2);
			p.put("y2", bbox.y2);
		}
		if (threshold != null)
			p.put("threshold", threshold);
		if (mf_selector != null)
			p.put("mf_selector", mf_selector);
		return p;
	}
}
