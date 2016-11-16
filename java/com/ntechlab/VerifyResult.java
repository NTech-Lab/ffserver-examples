package com.ntechlab;

import org.json.simple.*;

public class VerifyResult {
	public boolean verified;
	public double confidence;
	public BBox bbox1;
	public BBox bbox2;

	protected VerifyResult(boolean Verified, double Confidence, BBox Bbox1, BBox Bbox2) {
		verified = Verified;
		confidence = Confidence;
		bbox1 = Bbox1;
		bbox2 = Bbox2;
	}

	/* Initialize from JSON object */
	protected VerifyResult(JSONObject obj) {
		verified = (Boolean)obj.get("verified");
		confidence = ((Number)obj.get("confidence")).doubleValue();
		bbox1 = new BBox(obj.get("bbox1"));
		bbox2 = new BBox(obj.get("bbox2"));
	}
}
