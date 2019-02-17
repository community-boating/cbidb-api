#!/bin/bash
rm target/ -r
rm project/target/ -r
rm project/project/target/ -r
sbt clean
sbt clean-files
sbt compile
