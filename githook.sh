#!/bin/bash

export JAVA_HOME=/home/hary/opt/corretto-21.0.5
export M2_HOME=/home/hary/opt/apache-maven-3.9.9
export PATH=$JAVA_HOME/bin:$M2_HOME/bin/:$PATH

git pull;
mvn package;

./stop.sh;
./start.sh;
