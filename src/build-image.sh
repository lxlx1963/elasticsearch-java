#!/bin/bash

#1 从git拉取代码
git pull

#2 maven 打包
echo "packaging................................"
mvn clean package -Dmaven.test.skip=true

#3 将maven package的jar,复制target的上一级目录,与Dockerfile同一个目录
cp /data/frp-face-data-dashboard/target/*.jar /data/frp-face-data-dashboard

#4 dokcer build image （$1：image的标签名，如：caidao1963/frp-test：201812111435）
if [ ! $1 ]; then
  echo "Variable $1 is NULL"
  exit 1
fi

docker build -t $1 .
