<?php
/**
 * Quick and dirty parsing of the CUFTS data ignoring UTF-8 problems.
 */
define("GoKB_DATA", "gokb-dataset.json");
define("DELIMITER", "\t");

// Append results to the dataset and keep track of count.
function aggregateResults($dataset_filename, $records, $i) {
    if ($records !== false) {
        $fp = fopen($dataset_filename, "a+");
        if ($fp) {
            foreach ($records as $record) {
                if ($i > 0) {
                    fwrite($fp, ", " . PHP_EOL);
                }
                fwrite($fp, rtrim(json_encode($record, JSON_PRETTY_PRINT)));
                $i += 1;
            }
        }
        fclose($fp);
    }
    return $i;
}

// Create an object of fieldnames and their index from unexploded string.
function extractFields($s) {
    $cols = explode(DELIMITER, strtolower($s));
    $title_i = array_search('title', $cols);
    $issn_i = array_search('issn', $cols);
    $e_issn_i = array_search('e_issn', $cols);
    if ($title_i !== false && $issn_i !== false) {
        $fields = array(
                'title' => $title_i,
                'issn' => $issn_i
        );
        if ($e_issn_i !== false) {
            $fields['e_issn'] = $e_issn_i;
        }
        return $fields;
    }
    return false;
}

// Using the title and issn fields extract a record
function extractRecord($fields, $row) {
    $title_i = intval($fields['title']);
    $issn_i = intval($fields['issn']);
    $e_issn_i = false;
    if (isset($fields['e_issn'])) {
        $e_issn_i = intval($fields['e_issn']);
    }
    $cols = explode(DELIMITER, $row);
    if (count($cols) > 2) {
        $record = array(
            "title" => $cols[$title_i],
            "issn" => $cols[$issn_i]
        );
        if ($e_issn_i !== false && trim($cols[$e_issn_i]) !== "") {
            $record['e_issn'] = $cols[$e_issn_i];
        }
        return $record;
    }
    return false;
}

// Process a file yielding an array of records (title/issn).
function processFilename($fname) {
    $records = [];
    $fields = [];
    $fp = fopen($fname, "r");
    if ($fp) {
        // Get the colum names
        $buf = fgets($fp, 4096);
        $fields = extractFields($buf);
        // Now process all the columns
        if ($fields === false) {
            return false;
        }
        while (!feof($fp)) {
            $buf = fgets($fp, 4096);
            $record = extractRecord($fields, $buf);
            if ($record !== false) {
                array_push($records, $record);
            }
        }
    }
    fclose($fp);
    return $records;
}

//
// Process all the files and generate dataset
//
echo "Creating " . GoKB_DATA . PHP_EOL;
file_put_contents(GoKB_DATA, "[");
$files = glob("CUFTS/*");
$i = 0;
foreach ($files as $fname) {
    $i = aggregateResults(GoKB_DATA, processFilename($fname), $i);
    echo "Wrote $i aggregated records to " . GoKB_DATA . PHP_EOL;
}
file_put_contents(GoKB_DATA, "]", FILE_APPEND);
echo "Complete!" . PHP_EOL;
?>
