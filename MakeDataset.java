/**
 * MakeDataset.java - prototype to convert the CUFTS data into a simple JSON data file.
 */
import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.URLEncoder;


// Turn a 8 digit number into 4-4 ISSN formatted string.
class Format {
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

// Extract - process the IO and extract data.
class Extract {
    private int[] headings;

    public HashMap<String,Integer> columns(String[] header) {
        HashMap<String,Integer> results = new HashMap<String,Integer>();

        for (int i = 0; i < header.length; i++) {
            if (header[i].equals("title")) {
                results.put("title", i);
            }
            if (header[i].equals("issn")) {
                results.put("issn", i);
            }
            if (header[i].equals("e_issn")) {
                results.put("e_issn", i);
            }
        }
        return results;
    }

    public HashMap<String,String> record(HashMap<String,Integer> cols, String line) {
        HashMap<String,String> results = new HashMap<String,String>();
        Format f = new Format();

        if (line != null && line.length() > 0) {
            String[] parts = line.split("\t");
            int key_i;

            if (cols.get("title") != null) {
                key_i = cols.get("title");
                if (key_i < parts.length && parts[key_i].equals("") == false) {
                    results.put("title", f.quote(parts[key_i]));
                }
            }
            if (cols.get("issn") != null) {
                key_i = cols.get("issn");
                if (key_i < parts.length && parts[key_i].equals("") == false) {
                    results.put("issn", f.ISSN(parts[key_i]));
                }
            }
            if (cols.get("e_issn") != null) {
                key_i = cols.get("e_issn");
                if (key_i < parts.length && parts[key_i].equals("") == false) {
                    results.put("e_issn", f.ISSN(parts[key_i]));
                }
            }
        }
        return results;
    }

    public String recordToJSON(HashMap<String,String> record) {
        ArrayList<String> s = new ArrayList<String>();
        String results = "";
        Boolean delimit = false;

        s.add("{");
        if (record.get("title") != null) {
            s.add("\"title\":");
            s.add("\"" + record.get("title") + "\"");
            delimit = true;
        }
        if (record.get("issn") != null) {
            if (delimit == true) {
                s.add(",");
            }
            s.add("\"issn\":");
            s.add("\"" + record.get("issn") + "\"");
            delimit = true;
        }
        if (record.get("e_issn") != null) {
            if (delimit == true) {
                s.add(",");
            }
            s.add("\"e_issn\":");
            s.add("\"" + record.get("e_issn") + "\"");
            delimit = true;
        }
        s.add("}");

        for (int i = 0; i < s.size(); i++) {
            results += s.get(i);
        }
        return results;
    }

    public Boolean parse(IO in, IO out, String delimiter) {
        String line = in.readLine();
        // Extract header and figure out the columns we're interested in.
        String[] header;
        Boolean delimit = false;
        HashMap<String,Integer> cols;

        if (line != null) {
            header = line.split("\t");
        } else {
            System.err.println("Cannot parse header line for " + in.Filename());
            return false;
        }
        cols = this.columns(header);

        line = in.readLine();
        while (line != null) {
            // For each data row assemble the interesting columns and output JSON blob.
            HashMap<String,String> record = this.record(cols, line);
            if (record.get("title") != null &&
                    record.get("title").equals("") == false) {
                if (delimit == true) {
                    out.write(delimiter);
                } else {
                    delimit = true;
                }
                // NOTE: We must have a title or why bother with record?
                out.write(this.recordToJSON(record));
            }
            line = in.readLine();
        }
        return true;
    }
}

// IO Management
class IO {
    public String filename = "";
    private BufferedReader inBuf = null;
    private BufferedWriter outBuf = null;
    private Boolean useStdio = false;


    public String Filename() {
        return this.filename;
    }

    public Boolean open(String readOrWrite) {
        this.useStdio = true;
        if (readOrWrite.equals("r")) {
            this.inBuf = new BufferedReader(new InputStreamReader(System.in));
        }
        if (readOrWrite.equals("w")) {
            this.outBuf = new BufferedWriter(new OutputStreamWriter(System.out));
        }
        return true;
    }

    public Boolean open(String filename, String readOrWrite) {
        if (readOrWrite.equals("r")) {
            try {
                this.inBuf = new BufferedReader(new FileReader(filename));
            } catch (IOException err) {
                System.err.println(err.toString());
                return false;
            }
            this.filename = filename;
            return true;
        }
        if (readOrWrite.equals("w")) {
            try {
                this.outBuf = new BufferedWriter(new FileWriter(filename));
            } catch (IOException err) {
                System.err.println(err.toString());
                return false;
            }
            this.filename = filename;
            return true;
        }
        System.err.println("need to know if " + filename + " is open for reading or writing");
        return false;
    }

    public String readLine() {
        String line = "";
        try {
            line = this.inBuf.readLine();
        } catch (IOException err) {
            System.err.println(err.toString());
        }
        return line;
    }

    public void write(String s) {
        if (this.useStdio == true) {
            System.out.print(s);
        } else {
            try {
                this.outBuf.write(s, 0, s.length());
            } catch (IOException err) {
                System.err.println(err);
            }
        }
    }

    public Boolean close() {
        if (this.useStdio == true) {
            return true;
        }
        if (this.inBuf != null) {
            try {
                this.inBuf.close();
            } catch (IOException err) {
                System.err.println(err.toString());
                return false;
            }
        }
        if (this.outBuf != null) {
            try {
                this.outBuf.flush();
                this.outBuf.close();
            } catch (IOException err) {
                System.err.println(err.toString());
                return false;
            }
        }
        return true;
    }
}

class Usage {
    public void usage(String msg, int exitCode) {
        String s =  "USAGE: java MakeDataset INPUT_FILE [INPUT_FILE...]\n\n"
            + "Example: java MakeDataset CUFTS/* > cufts-dataset.json\n\n"
            + msg;
        if (exitCode == 0) {
            System.out.println(s);
        } else {
            System.err.println(s);
        }
        System.exit(exitCode);
    }
}

// Command line tool interface
public class MakeDataset {

    public static void main(String[] Args) {
        Usage u = new Usage();
        IO in = new IO();
        IO out = new IO();
        Extract extract = new Extract();

        if (Args.length == 0) {
            u.usage("", 0);
        }
        out.open("w");
        out.write("[");
        for (int i = 0; i < Args.length; i++) {
            if (in.open(Args[i], "r")) {
                if (i == 0) {
                    out.write("\n\t");
                } else {
                    out.write(",\n\t");
                }
                extract.parse(in, out, ",\n\t");
                in.close();
            }
        }
        out.write("\n]\n");
        out.close();
    }
}
