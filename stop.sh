#!/bin/bash

getPid() {
  ps -ef | grep 'renren-admin-server.jar' | grep 'server.port=8082' | grep -v grep | awk '{ print $2; }'
}

for i in $(seq 1 9); do
    pid=$(getPid)
    if [ x$pid == "x" ]; then
       break;
    fi
    kill -9 $pid > /dev/null 2>&1;
done