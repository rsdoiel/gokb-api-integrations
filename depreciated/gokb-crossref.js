//
// gokb-crossref.js - Utility methods for checking GoKB content against CrossRef.
// @author R. S. Doiel, <rsdoiel@gmail.com>
//
/*jslint node: true, indent: 4 */
/*global */
"use strict";
var fs = require("fs");

var one_second = 1000;

// parseLine - given a line return a record
// @param line - a string of tab delimited text
// @param fieldnames - a list of fieldnames
// @return an object representing a parsed record.
function parseLine(line, fieldnames) {
    var record = {};

    line.split("\t").forEach(function (value, j) {
        if (fieldnames[j] !== undefined) {
            record[fieldnames[j]] = value;
        } else {
            record[("col_" + j)] = value;
        }
    });
    return record;
}

// parseData - given a NodeJS buffer object parse the text into results.
// @param buf - the buffer contents returned by fs.readFile().
// @return an object containing the parsed results.
function parseData(buf) {
    var results = {
            fieldnames: [],
            data: [],
            records: 0,
            rows: 0,
            warnings: [],
            errors: []
        },
        key_cnt = 0,
        record = {},
        i = 0,
        lines = buf.toString().split("\n");

    if (lines.length < 1) {
        results.errors.push("empty data, nothing to parsable.");
        return results;
    }
    results.fieldnames = lines[0].split("\t") || [];
    if (results.fieldnames.length < 1) {
        results.errors.push("Could not parse fieldnames.");
    }

    for (i = 1; i < lines.length; i += 1) {
        results.rows += 1;
        record = parseLine(lines[i], results.fieldnames);
        key_cnt = Object.keys(record).length;
        if (key_cnt > 0) {
            results.records += 1;
            results.data.push(record);
            if (key_cnt !== results.fieldnames.length) {
                results.warnings.push("Record number " + results.records + " has fieldname mismatch.");
            }
        }
    }
    if (results.rows !== results.records) {
        results.warnings.push("Row and record count do not match. Parse not completed.");
    }
    return results;
}

// scanDirs - read a directory contents with a max timeout.
// @param basepath - the is the path to the directory to scan with fs.readdir();
// @param max_timeout - the number of seconds to wait before timing out.
// @param callback - the callback function to hand the results to.  Callback is
// passed the array of entries found with fs.readdir() or is undefined on timeout.
function scanDirs(basepath, max_timeout, callback) {
    var intval = 0;

    // Collect the directory entries and pass to callback.
    fs.readdir(basepath, function (err, dirs) {
        if (err) { throw err; }
        callback(dirs);
        clearInterval(intval);
    });

    // Don't wait forever, timeout if it takes too long.
    intval = setTimeout(function () {
        max_timeout -= 1;
        if (max_timeout < 0) {
            console.warn("fs.readdir() has timed taken too long.");
            callback();
            clearInterval(intval);
        }
    }, one_second);
}

// scanFile - extract the contents of a file and hand the results
// to the callback function.
function scanFile(fname, callback) {
    var intval = 0,
        max_timeout = 60; // timeout in seconds

    console.log("DEBUG scanning file", fname);
    fs.readFile(fname, function (err, buf) {
        if (err) { throw err; }
        console.log("DEBUG callback on ", fname);
        callback(parseData(buf));
    });

    // Don't wait forever, timeout if it takes too long.
    intval = setInterval(function () {
        max_timeout -= 1;
        if (max_timeout < 0) {
            console.warn("fs.readFile(" + fname + ") has timed taken too long.");
            callback(parseData(""));
            clearInterval(intval);
        }
    }, one_second);
}


exports.parseData = parseData;
exports.scanFile = scanFile;
exports.scanDirs = scanDirs;
