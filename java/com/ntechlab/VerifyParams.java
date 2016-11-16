package com.ntechlab;

import java.util.*;

public class VerifyParams {
	protected BBox bbox1;
	protected BBox bbox2;
	protected Object threshold;	/* Double or String */
	protected String mf_selector;

	public VerifyParams() { }

	public VerifyParams setBbox1(BBox b) {
		bbox1 = b;
		return this;
	}

	public VerifyParams setBbox1(int x1, int y1, int x2, int y2) {
		bbox1 = new BBox(x1, y1, x2, y2);
		return this;
	}

	public VerifyParams setBbox2(BBox b) {
		bbox2 = b;
		return this;
	}

	public VerifyParams setBbox2(int x1, int y1, int x2, int y2) {
		bbox2 = new BBox(x1, y1, x2, y2);
		return this;
	}

	/* threshold as double */
	public VerifyParams setThreshold(double value) {
		threshold = new Double(value);
		return this;
	}

	/* threshold as string */
	public VerifyParams setThreshold(String value) {
		threshold = new String(value);
		return this;
	}

	/* mf_selector */
	public VerifyParams setMfSelector(String value) {
		mf_selector = value;
		return this;
	}

	protected Map<String, Object> getFields(Map<String, Object> p) {
		if (bbox1 != null)
			p.put("bbox1", bbox1.asJSONObject());
		if (bbox2 != null)
			p.put("bbox2", bbox1.asJSONObject());
		if (threshold != null)
			p.put("threshold", threshold);
		if (mf_selector != null)
			p.put("mf_selector", mf_selector);
		return p;
	}
}
