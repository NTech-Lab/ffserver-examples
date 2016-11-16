package com.ntechlab;

import java.util.*;
import org.json.simple.parser.*;
import org.json.simple.*;

public class BBox {
	public int x1, y1, x2, y2;

	public BBox(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/** Convert [x1,y1,x2,y2] string to BBox */
	public BBox(String s) {
		try {
			JSONArray arr = (JSONArray)new JSONParser().parse(s);
			if (arr.size() != 4)
				throw new NumberFormatException("Malformed BBox string: " + s);
			x1 = ((Long)arr.get(0)).intValue();
			y1 = ((Long)arr.get(1)).intValue();
			x2 = ((Long)arr.get(2)).intValue();
			y2 = ((Long)arr.get(3)).intValue();
		} catch (ClassCastException e) {
			throw new NumberFormatException("Malformed BBox string: " + s);
		} catch (ParseException e) {
			throw new NumberFormatException("Malformed BBox string: " + s);
		}
	}

	private void fromJSON(JSONObject obj) {
		x1 = ((Long)obj.get("x1")).intValue();
		y1 = ((Long)obj.get("y1")).intValue();
		x2 = ((Long)obj.get("x2")).intValue();
		y2 = ((Long)obj.get("y2")).intValue();
	}

	public BBox(JSONObject obj) {
		fromJSON(obj);
	}

	public BBox(Object json) {
		fromJSON((JSONObject)json);
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("BBox(").append(x1).append(',').append(y1)
			.append(',').append(x2).append(',').append(y2).append(')');
		return b.toString();
	}

	protected JSONObject asJSONObject() {
		HashMap<String, Object> p = new HashMap<String, Object>();
		p.put("x1", x1);
		p.put("y1", y1);
		p.put("x2", x2);
		p.put("y2", y2);
		return new JSONObject(p);
	}
}
