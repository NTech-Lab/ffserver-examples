package com.ntechlab;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class FacenAPI {
	private String baseUrl;		/**< API base URL */
	private String token;		/**< authorization token */

	public FacenAPI(String url, String auth) {
		baseUrl = url;
		token = auth;
	}

	private JSONObject postJSON(String method, Map<String, Object> rq) throws IOException, ParseException {
		HttpURLConnection conn = (HttpURLConnection)new URL(baseUrl + method).openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Token " + token);
		conn.setDoOutput(true);
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		JSONObject.writeJSONString(rq, w);
		w.close();
		conn.connect();

		int code = conn.getResponseCode();
		if ((int)(code / 100) != 2)
			throw new RuntimeException("Bad response code: " + code);

		BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		return (JSONObject)new JSONParser().parse(r);
	}

	/* /v0/detect */
	public BBox[] detect(String photoUrl) throws IOException, ParseException {
		return detect(photoUrl, null);
	}

	/* /v0/detect */
	public BBox[] detect(String photoUrl, DetectParams params) throws IOException, ParseException {
		Map<String, Object> rq = new HashMap<String, Object>();
		rq.put("photo", photoUrl);
		if (params != null)
			params.getFields(rq);
		JSONArray faces = (JSONArray)postJSON("/v0/detect", rq).get("faces");
		if (faces != null) {
			int i = 0;
			BBox[] box = new BBox[faces.size()];
			for (Object b : faces) {
				box[i] = new BBox(b);
				i++;
			}
			return box;
		} else {
			return new BBox[0];
		}
	}

	/* /v0/verify */
	public VerifyResult[] verify(String photoUrl1, String photoUrl2) throws IOException, ParseException {
		return verify(photoUrl1, photoUrl2, null);
	}

	/* /v0/verify */
	public VerifyResult[] verify(String photoUrl1, String photoUrl2, VerifyParams params) throws IOException, ParseException {
		/* build parameters object */
		Map<String, Object> rq = new HashMap<String, Object>();
		rq.put("photo1", photoUrl1);
		rq.put("photo2", photoUrl2);
		if (params != null)
			params.getFields(rq);

		/* extract results */
		JSONArray results = (JSONArray)postJSON("/v0/verify", rq).get("results");

		if (results != null) {
			int i = 0;
			VerifyResult[] vr = new VerifyResult[results.size()];
			for (Object r : results) {
				vr[i] = new VerifyResult((JSONObject)r);
				i++;
			}
			return vr;
		} else {
			return new VerifyResult[0];
		}
	}

	/* /v0/identify */
	public IdentifyResult[] identify(String photoUrl) throws IOException, ParseException {
		return identify(photoUrl);
	}

	/* /v0/identify */
	public IdentifyResult[] identify(String photoUrl, IdentifyParams params) throws IOException, ParseException {
		/* serialize request */
		Map<String, Object> rq = new HashMap<String, Object>();
		rq.put("photo", photoUrl);
		if (params != null)
			params.getFields(rq);

		/* perform JSON call and work on results */
		JSONObject boxes = (JSONObject)postJSON("/v0/identify", rq).get("results");

		/* build FaceMatche[] array */
		IdentifyResult[] matches = new IdentifyResult[boxes.size()];
		/* Iterate over all boxes found in response */
		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, Object>> it = boxes.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Map.Entry<String,Object> e = it.next();
			JSONArray faces = (JSONArray)e.getValue();
			IdentifyResult m = new IdentifyResult(e.getKey(), faces.size());
			/* iterate over all FaceWithConfidence that matched inside specific box */
			int j = 0;
			for (Object f : faces) {
				m.faces[j] = IdentifyResult.matchFromJSON((JSONObject)f);
				j++;
			}
			matches[i] = m;
			i++;
		}
		return matches;
	}
}
