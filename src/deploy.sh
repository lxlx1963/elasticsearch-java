#!/bin/bash
#0 删除老的nohup
#echo "deleting old nohup.out................."
#rm -f nohup.out

currentENV=${1}
currentJAR='/frp-face-data-dashboard-0.0.1-SNAPSHOT.jar'
productENV='prod'

if [ -z "$currentENV" ]
then
    echo "please load witch env should be deployed !!" 
    exit 1
fi

#1 从git服务器拉取最新代码
echo "pull newest code........................."
git pull

#2 maven 打包
echo "packaging................................"
mvn clean package -Dmaven.test.skip=true

#3 获取当前正在运行中的 java-process-id
appPid=`ps -ef | grep ${currentJAR} | grep -v grep | awk '{print $2}'`

#4 kill之前的应用进程
echo "kill process ${appPid}.................."
kill -9 ${appPid}
# 休息3秒，等待占用内存被回收
sleep 3

#5 deploy project now . 生产环境重新分配jvm参数设置
echo "start runing ${currentJAR}..............................."
if [ "$currentENV" = "$productENV" ]
then 
    nohup java -server -Xms2G -Xmx2G -Xss512k -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -jar target/*.jar --spring.profiles.active=${currentENV} &
else
	nohup java -server -jar target/*.jar --spring.profiles.active=${currentENV} &
fi
echo "***************************************************************"
echo ""
echo "=================== current env is ${currentENV} ! ====================="
echo ""
echo "***************************************************************"

#6 休息3秒，确认当前加载的配置文件是否正确
sleep 3

#7 打印nohup.out日志
tail -f nohup.out
