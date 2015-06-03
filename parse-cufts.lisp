;;
;; Parse the files in the CUFT folder for objects to generate Title/ISSN/e-ISSN/License list from.
;;
(defun get-file-listing ()
   (directory (make-pathname :directory '(:relative "CUFTS") :name :wild :type :wild)))

(defun tokenize-tabbed-line (line)
    (loop 
      for start = 0 then (+ space 1)
        for space = (position #\Tab line :start start)
            for token = (subseq line start space)
                collect token until (not space))) 

(defun tokenize-file (data-filename)
  (let ((records '()))
    (with-open-file (input-stream data-filename :direction :input)
      (do ((line (read-line input-stream) (read-line input-stream nil 'eof)))
        ((eq line 'eof) (reverse records))
        (push (tokenize-tabbed-line line) records)))))

(defun profile-files (file-list)
  (let ((fname (car file-list)))
    (if (> (list-length file-list) 1)
      (profile-files (rest file-list)))
       (format t "~&atom ~A~%" fname)
      (tokenize-file (car file-list))))

;;
;; Experimental functions
;;

;; Get the headings of the tab delimited file as a list of strings.
(defun get-headings-line (data-filename)
    (with-open-file (input-stream data-filename :direction :input)
      (tokenize-tabbed-line (read-line input-stream nil nil))))

(defun get-all-lines (data-filename)
    (with-open-file (input-stream data-filename :direction :input)
      (do ((line (read-line input-stream) (read-line input-stream nil 'eof)))
        ((eq line 'eof) "Reached end of file.")
        (format t "~&~A~%" line))))


