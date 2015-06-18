/**
 * IO.java - IO wrapper scripts for building Unix style pipe friendly utilities.
 */
package GOKbIntegrations;

import java.io.*;
//import java.util.*;
//import java.lang.*;
//import java.net.URLEncoder;
//import MakeDataSet.IO;


// IO Management
public class IO {
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

