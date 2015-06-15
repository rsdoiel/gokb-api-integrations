#!/usr/bin/env node
//
// gokb-cross_test.js - Tests of gokb-cross.js module.
//
// @author R. S. Doiel, <rsdoiel@gmail.com>
//
/*jslint node: true, indent: 4,  */
/*global process, console */
"use strict";

var assert = require("assert"),
    path = require("path"),
    fs = require("fs"),
    gc = require("./gokb-crossref");

// Applications tests
function testData(fname, data) {
    assert.ok(data, "Should have data for " + fname);
}

function testScanFile(fname) {
    console.log("testScanFile(" + fname + ")...");
    gc.scanFile(fname, function (data) {
        testData(fname, data);
    });
    console.log("OK");
}

function testScanDirs(datapath) {
    console.log("testScanDirs...");
    var basepath = path.normalize(datapath);

    assert.ok(basepath, "Should have a basepath");
    fs.stat(basepath, function (err, stats) {
        assert.ok(!err, err);
        assert.ok(stats, "Should have stats for " + basepath);
        fs.readdir(basepath, function (err, dirs) {
            assert.ok(!err, err);
            assert.ok(dirs, "Should have a list of " + basepath + " content.");
            gc.scanDirs(basepath, 10, function (items) {
                assert.ok(items.length > 1, "Should find more than one directory item");
                //console.log("Scanning " + items);
                items.forEach(function (item) {
                    testScanFile(path.join(basepath, item));
                });
            });
            console.log("OK");
        });
    });
}

// Run tests methods
(function () {
    assert.ok(gc, "Should have gokb-cross object");
    assert.equal(typeof gc.scanDirs, "function", "Should have a scanDirs function");
    assert.equal(typeof gc.scanFile, "function", "Should have a scanFile function");
    assert.equal(typeof gc.parseData, "function", "Should have a parseData function");
    testScanDirs("CUFTS");
    console.log("Success!");
}());
