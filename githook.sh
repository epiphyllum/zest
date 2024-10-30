#!/bin/bash

export JAVA_HOME=/home/hary/.jdks/xxx
export PATH=$JAVA_HOME/bin:$PATH

git pull;
mvn package;

./stop.sh;
./start.sh;