#!/bin/bash
echo -ne 'set JAVA_OPTS=-XX:MaxPermSize=128m -Xmx384m\r\n' > runprojx.bat
echo -n groovy -cp `ls lib/* | awk '{ ORS=";"; print }'`. projx >> runprojx.bat
