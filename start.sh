#!/bin/bash

nohup java -server -Xms4096M -Xmx4096M -jar renren-admin/renren-admin-server/target/renren-admin-server.jar --spring.profiles.active=prod --server.port=8082 >> zest.log &