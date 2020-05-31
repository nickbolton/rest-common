#!/bin/bash

cat common-files.txt | while read classname; do
  filename="${classname}.java"
  find ~/src/talentdrop/services/shared/src -name "${filename}" | while read f; do
    package=$(echo $f | sed -e 's/.*java\///g' -e "s/\/$filename//g")
    echo "$classname -> $package"
    mkdir -p src/main/java/$package
    cp $f src/main/java/$package
  done
done
