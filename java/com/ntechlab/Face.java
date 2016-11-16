package com.ntechlab;

import java.util.*;
import org.joda.time.*;
import org.joda.time.format.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.ntechlab.*;

public class Face {
	public long id;
	public Long person_id;
	public String cam_id;
	public DateTime timestamp;
	public String photo;
	public String photo_hash;
	public BBox box;
	public String meta;
	public String[] galleries;

	/* Build Face object from JSON object */
	public Face(Object jsonObject) {
		JSONAdaptor a = new JSONAdaptor(jsonObject);
		id = a.getLong("id");
		person_id = a.getLong("person_id");
		cam_id = a.getString("cam_id");
		timestamp = a.getTime("timestamp");
		photo = a.getString("photo");
		photo_hash = a.getString("photo_hash");
		box = new BBox(jsonObject);
		meta = a.getString("meta");
		JSONArray g = a.getArray("galleries");
		if (g != null) {
			galleries = new String[g.size()];
			for (int i = 0; i < galleries.length; i++) {
				galleries[i] = (String)g.get(i);
			}
		}
	}

	/* Convert face to human-readable representation */
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Face(")
			.append(", id = ").append(id);
		if (person_id != null)
			b.append(", person_id = ").append(person_id);
		if (cam_id != null)
			b.append(", cam_id = ").append(cam_id);
		b.append(", timestamp = ").append(timestamp)
			.append(", photo = ").append(photo)
			.append(", photo_hash = ").append(photo_hash)
			.append(", ").append(box);
		if (meta != null && meta.length() != 0)
			b.append(", meta = ").append(meta);
		if (galleries.length != 0) {
			b.append(", galleries = [\"").append(galleries[0]);
			for (int i = 1; i < galleries.length; i++) {
				b.append("\" , \"").append(galleries[i]);
			}
			b.append("\"]");
		}
		return b.toString();
	}
}
