package com.ntechlab;

import java.util.*;
import org.json.simple.*;
import org.joda.time.*;
import org.joda.time.format.*;

class JSONAdaptor {
	private JSONObject obj;
	private static final DateTimeFormatter dtp = ISODateTimeFormat.dateTimeParser();

	public JSONAdaptor(Object json) {
		obj = (JSONObject)json;
	}

	public final String getString(String key) {
		return (String)obj.get(key);
	}

	public final Integer getInt(String key) {
		Object val = obj.get(key);
		if (val == null)
			return null;
		return ((Long)val).intValue();
	}

	public final Long getLong(String key) {
		return (Long)obj.get(key);
	}

	public final DateTime getTime(String key) {
		return dtp.parseDateTime(getString(key));
	}

	public final Object get(String key) {
		return obj.get(key);
	}

	public final JSONArray getArray(String key) {
		return (JSONArray)obj.get(key);
	}
}
