#!/bin/bash

cat common-files.txt | while read classname; do
  filename="${classname}.java"
  find src -name "${filename}" | while read f; do
    package=$(echo $f | sed -e 's/.*java\///g' -e "s/\/$filename//g")
    echo "$classname -> $package"
    rm -f $f
  done
done
