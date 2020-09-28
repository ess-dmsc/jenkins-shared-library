#!/bin/sh

for filepath in $(ls test/ecdcpipeline/*Test.groovy); do
  groovy --classpath src:test $filepath
done
