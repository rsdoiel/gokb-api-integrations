;;
;; Parse the files in the CUFT folder for objects to generate Title/ISSN/License list from.
;;
(defun get-file-listing ()
   (directory (make-pathname :directory '(:relative "CUFTS") :name :wild :type :wild)))

(defun tokenize-tabbed-line (line)
    (loop 
           for start = 0 then (+ space 1)
                for space = (position #\Tab line :start start)
                     for token = (subseq line start space)
                          collect token until (not space)))

;; Get the headings of the tab delimited file as a list of strings.
(defun get-file-headings (data-filename)
    (with-open-file (input-stream data-filename :direction :input)
      (tokenize-tabbed-line (read-line input-stream nil nil))))


