/**
 * QuerySherpaRomeo.java - given a list of JSON objects with issn or e_issn
 * get back the usage/license information from Sherpa/Romeo API.
 */
package GOKbIntegrations;

import java.io.*;

public class QuerySherpaRomeo {
    public static void main(String[] Args) {
        // Check to see if environment vars are set for querying Sherpa/Romeo API
        // Read in the JSON file containing a list of objects to query and
        // expand.
        // For each object query the API without going over the rate limits.
        // Write out a new JONS file with the results
        /*
        IO out = new IO();

        out.open("w");
        out.write("DEBUG QuerySherpaRomeo not implemented.");
        out.close();
        */
    }
}
