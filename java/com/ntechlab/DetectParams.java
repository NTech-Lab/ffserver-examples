package com.ntechlab;

import java.util.*;

public class DetectParams {
	protected BBox bbox;

	public DetectParams() { }

	public DetectParams setBbox(BBox b) {
		bbox = b;
		return this;
	}

	public DetectParams setBbox(int x1, int y1, int x2, int y2) {
		bbox = new BBox(x1, y1, x2, y2);
		return this;
	}

	protected Map<String, Object> getFields(Map<String, Object> p) {
		if (bbox != null)
			p.put("bbox", bbox.asJSONObject());
		return p;
	}
}
