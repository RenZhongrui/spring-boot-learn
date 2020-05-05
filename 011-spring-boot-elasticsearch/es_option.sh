#!/bin/bash

ES_HOME=/home/elk/elasticsearch-7.3.2
APP_NAME=elasticsearch
echo "开始启停elasticsearch"
# 使用方式
usage() {
	echo "case:sh run.sh [start | stop | restart | status]"
	echo "请类似这样执行 ./*.sh start or ./*sh restart"
	exit 1
}

# 判断当前服务是否已经启动的函数
is_exist() {
	echo "执行 is_exist方法"
	pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}'`
	if [ -z "${pid}" ]; then
		echo "pid is null."
		return 1
	else
		echo "pid <> null"
		return 0
	fi
}
# 启动
start() {
	is_exist
	if [ $? -eq 0 ]; then
		echo "${APP_NAME} running. pid=${pid}"
	else
	        su elk -c "sh ${ES_HOME}/bin/elasticsearch -d -p ${ES_HOME}/pid"
		echo "${APP_NAME} started"
	fi
}
# 停止
stop() {
	echo "执行stop方法"
	is_exist
	if [ $? -eq 0 ]; then
		kill `cat ${ES_HOME}/pid`
		echo "${pid} stop"
	else
		echo "${APP_NAME} not running"
	fi
}
#重启
restart() {
	stop
	start
}
case "$1" in 
	"start")
		start
		;;
	"stop")
		stop
		;;
	"restart")
		restart
		;;
	*)
		usage
		;;
esac
	
























