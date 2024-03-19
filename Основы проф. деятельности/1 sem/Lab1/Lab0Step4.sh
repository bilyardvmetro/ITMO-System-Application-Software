#!/usr/bin/bash
cd lab0
mkdir tmp
wc -l growlithe1/* &> tmp/file.txt
(ls -lr | sort -k6) 2> /dev/null
cat growlithe1/* | sort -r
(wc -m growlithe1/* | sort -r) 2>/dev/null
(ls -l | grep "chi" | sort -r) 2> /dev/null
(ls -l | grep "chi" | tail -n 2 | sort -k6) 2> tmp/errorsStep5.txt
cd ..