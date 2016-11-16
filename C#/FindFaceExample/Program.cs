using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.IO;
using System.Web;

/* Install from http://www.newtonsoft.com/json */
using Newtonsoft.Json;
using Newtonsoft.Json.Converters;

namespace FindFace
{

    class Bbox
    {
        public uint x1;
        public uint y1;
        public uint x2;
        public uint y2;
        public override string ToString()
        {
            return String.Format("Bbox(x1: {0}; y1: {1}; x2: {2}; y2: {3})", x1, y1, x2, y2);
        }
    }

    [JsonConverter(typeof(StringEnumConverter))]
    enum mf_selector
    {
        reject,
        biggest,
        all
    }

    [JsonConverter(typeof(StringEnumConverter))]
    enum threshold
    {
        strict,
        medium,
        low
    }

    namespace Detect
    {
        class Face : Bbox { }
        class Results : List<Face> { }
        class Response
        {
            public Results faces;
        }
        class Request
        {
            public String photo;
            public Bbox bbox;
        }
    }

    namespace Identify
    {
        class Face
        {
            public IList<String> galleries;
            public uint id;
            public String meta;
            public String photo;
            public String photo_hash;
            public DateTime timestamp;
            public uint x1;
            public uint y1;
            public uint x2;
            public uint y2;
        }
        class BoxResult
        {
            public uint confidence;
            public Face face;
        }
        class Results : Dictionary<String, IList<BoxResult>> { }
        class Response
        {
            public Results results;
        }
        class Request
        {
            public String photo;
            public uint? x1;
            public uint? y1;
            public uint? x2;
            public uint? y2;
            public threshold threshold;
            public uint n;
            public mf_selector mf_selector;
        }
    }

    namespace Verify
    {
        class Result
        {
            public bool verified;
            public float confidence;
            public Bbox bbox1;
            public Bbox bbox2;
        }
        class Results : List<Result> { }
        class Response
        {
            public bool verified;
            public Results results;
        }
        class Request
        {
            public String photo1;
            public String photo2;
            public Bbox bbox1;
            public Bbox bbox2;
            public threshold threshold;
            public mf_selector mf_selector;
        }
    }



    class FindFaceAPI
    {
        private String api_server;
        private String api_token;

        public FindFaceAPI(String server, String token)
        {
            this.api_server = server;
            this.api_token = token;
        }

        public Detect.Results detect(String photo, FindFace.Bbox bbox = null)
        {
            Detect.Request request = new Detect.Request();
            request.photo = photo;
            request.bbox = bbox;
            Detect.Response response = do_request<Detect.Response>("/v0/detect", request);
            return response.faces;
        }

        public Identify.Results identify(String photo, uint? x1 = null, uint? y1 = null, uint? x2 = null, uint? y2 = null, threshold threshold = threshold.low, uint n = 1, mf_selector mf_selector = mf_selector.biggest)
        {
            Identify.Request request = new Identify.Request();
            request.photo = photo;
            request.x1 = x1;
            request.y1 = y1;
            request.x2 = x2;
            request.y2 = y2;
            request.threshold = 0;
            request.n = n;
            request.mf_selector = 0;
            Identify.Response response = do_request<Identify.Response>("/v0/identify", request);
            return response.results;
        }

        public Verify.Response verify(String photo1, String photo2, Bbox bbox1 = null, Bbox bbox2 = null, threshold threshold = threshold.low, mf_selector mf_selector = mf_selector.biggest)
        {
            Verify.Request request = new Verify.Request();
            request.photo1 = photo1;
            request.photo2 = photo2;
            request.bbox1 = bbox1;
            request.bbox2 = bbox2;
            request.threshold = threshold;
            request.mf_selector = mf_selector;
            Verify.Response response = do_request<Verify.Response>("/v0/verify", request);
            return response;
        }

        private T do_request<T>(String method, Object requestObject)
        {
            WebRequest request = WebRequest.Create(api_server + method);
            request.Method = "POST";
            request.Headers.Add(HttpRequestHeader.Authorization, "Token " + api_token);
            request.ContentType = "application/json";
            String jsonRequest = JsonConvert.SerializeObject(requestObject, Formatting.Indented, new JsonSerializerSettings { NullValueHandling = NullValueHandling.Ignore });
            // Console.WriteLine("+++ Sending request to {0}:", request.RequestUri);
            // Console.WriteLine(jsonRequest);
            // Console.WriteLine("--- End of Request");
            byte[] postBytes = Encoding.ASCII.GetBytes(jsonRequest);
            Stream dataStream = request.GetRequestStream();
            dataStream.Write(postBytes, 0, postBytes.Length);
            dataStream.Close();
            dataStream = request.GetResponse().GetResponseStream();
            StreamReader reader = new StreamReader(dataStream);
            String response = reader.ReadToEnd();
            reader.Close();
            dataStream.Close();
            return JsonConvert.DeserializeObject<T>(response);
        }
    }
}

namespace FindFaceExample
{
    class Program
    {
        static void printIdentifyResults(FindFace.Identify.Results r)
        {
            Console.WriteLine("+++ Identify Results:");
            foreach (string box in r.Keys)
            {
                Console.WriteLine("  Box {0}", box);
                foreach (FindFace.Identify.BoxResult br in r[box])
                {
                    Console.WriteLine("    Confidence: {0}", br.confidence);
                    Console.WriteLine("    Face.meta: {0}", br.face.meta);
                    Console.WriteLine("    Face.timestamp: {0}", br.face.timestamp.ToString("dd MMM yyyy, HH:MM:ss"));
                    Console.WriteLine("    Face.photo_hash: {0}", br.face.photo_hash);
                    Console.WriteLine("    ...");
                }
            }
            Console.WriteLine("--- End of Identify Results");
        }

        static void printDetectResults(FindFace.Detect.Results r)
        {
            Console.WriteLine("+++ Detect results:");
            foreach (FindFace.Detect.Face face in r)
            {
                Console.WriteLine("  {0}", face);
            }
            Console.WriteLine("--- End of Detect results");
        }

        static void printVerifyResponse(FindFace.Verify.Response r)
        {
            Console.WriteLine("+++ Verify response:");
            Console.WriteLine("  Verified: {0}", r.verified);
            foreach (FindFace.Verify.Result res in r.results)
            {
                Console.WriteLine("    Confidence: {0}", res.confidence);
                Console.WriteLine("    BBox1: {0}", res.bbox1);
                Console.WriteLine("    BBox2: {0}", res.bbox2);
                Console.WriteLine("    Verified: {0}", res.verified);
            }
            Console.WriteLine("--- End of Verify response");
        }

        static void Main(string[] args)
        {
            String photo1 = "http://static.findface.pro/sample.jpg";
            String photo2 = "http://static.findface.pro/sample2.jpg";

            /* XXX Modify Host and Key here XXX */
            FindFace.FindFaceAPI api = new FindFace.FindFaceAPI("http://172.16.9.2:8000", "GAFDHGIAGIHKGAAD");

            try
            {
                FindFace.Identify.Results identifyResults = api.identify(photo1);
                printIdentifyResults(identifyResults);
                Console.WriteLine("");

                FindFace.Detect.Results detectResults = api.detect(photo1);
                printDetectResults(detectResults);
                Console.WriteLine("");

                FindFace.Verify.Response verifyResponse = api.verify(photo1, photo2);
                printVerifyResponse(verifyResponse);
                Console.WriteLine("");
            } catch(Exception e)
            {
                Console.WriteLine("ERROR: {0}", e);
            }
            Console.WriteLine("Press any key to exit...");
            Console.ReadKey();
        }
    }
}
