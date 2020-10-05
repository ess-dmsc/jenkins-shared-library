#!/bin/sh

for filepath in $(ls test/ecdcpipeline/*Test.groovy); do
  echo "Running tests in ${filepath}"
  groovy --classpath src:test $filepath
done
