package com.ntechlab;

import org.json.simple.*;

/* Represents face match */
public class IdentifyResult {
	public static class FaceMatch extends com.ntechlab.Face {
		public double confidence;

		FaceMatch(JSONObject obj) {
			super(obj.get("face"));
			confidence = ((Number)obj.get("confidence")).doubleValue();
		}
	}

	public BBox					box;			/**< box where face is found on photo */
	public FaceMatch[]			faces;			/**< faces that matched */

	public IdentifyResult(String boxstr, int nfaces) {
		box = new BBox(boxstr);
		faces = new FaceMatch[nfaces];
	}

	protected static FaceMatch matchFromJSON(JSONObject obj) {
		return new FaceMatch(obj);
	}
}



