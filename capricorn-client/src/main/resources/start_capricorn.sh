#!/bin/bash
source /etc/profile
NOW=`date +"%Y-%m-%d %H:%M:%S"`
CAPRICORN_PID=`jps|grep capricorn|awk -F ' ' '{print $1}'`
if [ -n "$CAPRICORN_PID" ] ; then
    echo "$NOW capricorn (${CAPRICORN_PID}) running now, keep alive" >> /root/capricorn.log;
    exit 2;
fi
nohup java -jar /root/capricorn-client-1.0-SNAPSHOT.jar > /dev/null 2>&1 &
