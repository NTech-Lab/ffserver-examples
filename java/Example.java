import com.ntechlab.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.*;
import org.joda.time.*;

/* This example will look through faces database for specified person and then
 * print events related to this person */

public class Example {

	private final static String apiBaseUrl = "http://HOST:PORT";
	private final static String apiToken = "YOUR_TOKEN";
	private final static String samplePhoto = "URL_OF_PHOTO_OF_THE_PERSON_YOU_ARE_LOOKING_FOR";
	private final static String samplePhoto2 = "URL_OF_PHOTO_FOR_VERIFY";

	public static void main(String[] args) throws Exception {
		FacenAPI api = new FacenAPI(apiBaseUrl, apiToken);

		/* detect demo */
		System.out.println("*** Detected faces on " + samplePhoto + ":");
		for (BBox b : api.detect(samplePhoto)) {
			System.out.println("  - " + b);
		}

		/* verify demo */
		System.out.println("*** Verify " + samplePhoto + " against " + samplePhoto2);
		for (VerifyResult r : api.verify(samplePhoto, samplePhoto2, new VerifyParams().setThreshold(0.6))) {
			System.out.println("  - match = " + r.verified + " (confidence = " + r.confidence
					+ ") for faces " + r.bbox1 + " <=> " + r.bbox2);
		}

		/* identify demo */
		System.out.println("*** Identify " + samplePhoto);
		for (IdentifyResult r : api.identify(samplePhoto, new IdentifyParams().setN(3).setThreshold(0.4))) {
			System.out.println("  + Face @ " + r.box + " matched " + r.faces.length + " faces");
			for (IdentifyResult.FaceMatch f : r.faces) {
				System.out.println("    " + f.photo + " (confidence = " + f.confidence + ", meta = " + f.meta + ")");
			}
		}
	}
}
