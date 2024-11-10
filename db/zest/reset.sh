#!/bin/bash

for f in `find . -name "*.sql"`; do
	echo "process file $f;";
	mysql -uroot -hlocalhost -p123456 --database zest < $f; 
done

