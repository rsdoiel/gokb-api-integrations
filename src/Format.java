/**
 * Format.java - some helper methods for making it easy to work with ISSNs, and other identifers.
 *
 * @author R. S. Doiel, <rsdoiel@caltech.edu>
 */
package GOKbIntegrations;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.URLEncoder;

// Turn a 8 digit number into 4-4 ISSN formatted string.
public class Format {
    public String ISSN(String s) {
        if (s.trim().equals("") == false) {
            return s.substring(0, 4) + "-" + s.substring(4);
        }
        return "";
    }

    public String quote(String s) {
        String o = "";
        try {
            o = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException err) {
            System.err.println(err.toString());
        }
        return o;
    }
}

