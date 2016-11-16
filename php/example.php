<?php

define('DATEFMT', 'Y-m-d\TH:i:s.u');

/* Bounded box from "Common objects" */
class BBox {
    var $x1, $y1, $x2, $y2;

    function __construct($x1, $y1, $x2, $y2) {
        $this->x1 = $x1;
        $this->y1 = $y1;
        $this->x2 = $x2;
        $this->y2 = $y2;
    }

    /* from JSON object or array */
    static function fromJSON($json) {
        if (is_array($json)) {
            return new BBox($json[0], $json[1], $json[2], $json[3]);
        } else if (is_object($json)) {
            return new BBox($json->x1, $json->y1, $json->x2, $json->y2);
        }
    }

    function toJSON() {
        return array("x1" => $this->x1, "y1" => $this->y1,
                     "x2" => $this->x2, "y2" => $this->y2);
    }
};

/* Face from "Common objects" */
class Face {
    var $id, $timestamp, $photo, $photo_hash, $bbox, $meta, $galleries;

    static function fromJSON($json) {
        $face = new Face();
        foreach (array('id', 'timestamp', 'photo', 'photo_hash', 'meta', 'galleries') as $f) {
            $face->{$f} = $json->{$f};
        }
        $face->timestamp = DateTime::createFromFormat(DATEFMT, $json->timestamp);
        $face->bbox = BBox::fromJSON($json);
        return $face;
    }
};

/* FacenAPI wrapper */
class FacenAPI {
    private $base_url, $token;
    private $ch;

    private function do_post($method, $rq) {
        $body = json_encode($rq);
        curl_reset($this->ch);
        curl_setopt($this->ch, CURLOPT_POST, true);
        curl_setopt($this->ch, CURLOPT_URL, $this->base_url . $method);
        curl_setopt($this->ch, CURLOPT_POSTFIELDS, $body);
        curl_setopt($this->ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($this->ch, CURLOPT_HTTPHEADER, array(
            'Content-Type: application/json',
            'Authorization: Token ' . $this->token,
            'Content-Length: ' . strlen($body)));
        $result = curl_exec($this->ch);
        $status = curl_getinfo($this->ch, CURLINFO_HTTP_CODE);
        if ((int)($status / 100) == 2) {
        } else if ($status == 403) {
            throw Exception('AUTH error');
        } else if ($status == 400) {
            throw Exception('BAD request');
        } else {
            throw Exception('BAD facenapi response code: ' . $status);
        }
        return json_decode($result);
    }

    function __construct($base_url, $token) {
        $this->base_url = $base_url;
        $this->token = $token;
        $this->ch = curl_init();
    }

    /* Detect faces found on image */
    function detect($photo_url, $bbox = NULL) {
        $rq = array("photo" => $photo_url);
        if ($bbox) {
            if (is_array($bbox)) {
                if (count($bbox) != 4) {
                    throw new Exception('Invalid bbox size (' . count($bbox) . ')');
                }
                $rq['bbox'] = $bbox;
            } else {
                $rq['bbox'] = $bbox->toJSON();
            }
        }
        $r = $this->do_post('/v0/detect', $rq);
        $faces = array();
        foreach ($r->faces as $box) {
            $faces[] = BBox::fromJSON($box);
        }
        return $faces;
    }

    /* Check similarity of two faces
     * @param optvars is an associative array with the following keys:
     *                "threshold" => float or string
     *                "mf_selector" => string
     * limitations: bbox input parameter is not supported
     */
    function verify($url1, $url2, $optvars = NULL) {
        $rq = array("photo1" => $url1, "photo2" => $url2);
        if (is_array($optvars)) {
            $rq = array_merge($rq,
                array_intersect_key($optvars,
                                    array("threshold" => 0, "mf_selector" => 0)));
        }
        $rs = $this->do_post('/v0/verify', $rq);
        $out = array();
        foreach ($rs->results as $r) {
            $out[] = (object)array("bbox1" => BBox::fromJSON($r->bbox1),
                                   "bbox2" => BBox::fromJSON($r->bbox2),
                                   "confidence" => (float)$r->confidence);
        }
        return $out;
    }

    /* Returns array of identified faces:
     * [ { box: $src_match, matches: [ {confidence: $confidence, face: Face_Object} ] }
     * limitations: bbox input parameter is not supported
     */
    function identify($photo_url, $n = 1, $optvars = NULL) {
        $rq = array("photo" => $photo_url, "n" => $n);
        if (is_array($optvars)) {
            $rq = array_merge($rq,
                array_intersect_key($optvars,
                                    array("threshold" => 0, "mf_selector" => 0)));
        }
        $rs = $this->do_post('/v0/identify', $rq);
        $out = array();
        foreach ($rs->results as $b => $faces) { // iterate over all boxes
            $box = BBox::fromJSON(json_decode($b));
            $matched = array();
            foreach ($faces as $f) {
                $matched[] = (object)array("confidence" => $f->confidence, "face" => Face::fromJSON($f->face));
            }
            $out[] = (object)array("box" => $box, "matches" => $matched);
        }
        return $out;
    }
};

$photo1 = "http://static.findface.pro/sample.jpg";
$photo2 = "http://static.findface.pro/sample2.jpg";

$api = new FacenAPI('http://PORT:HOST', 'TOKEN');

$det = $api->detect($photo1);
var_dump($det);

$ver = $api->verify($photo1, $photo2, array("threshold" => 0.5));
var_dump($ver);

$ident = $api->identify($photo1);
var_dump($ident);
?>
