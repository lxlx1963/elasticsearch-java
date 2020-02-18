#依赖于caidao1963/alpine-jdk180151-utf8:latest image
FROM caidao1963/alpine-jdk180151-utf8:latest

#定义编码
ENV LANG zh_CN.UTF-8

#创建目录
RUN mkdir -p /data

RUN mkdir -p /data/tmpupload

#工作目录
WORKDIR /data

#添加具体jar文件
ARG JAR_FILE=face-data-dashboard-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

#Port
EXPOSE 8166

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Dspring.profiles.active=dev","app.jar"]