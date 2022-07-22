FROM openjdk:11.0.15-slim
VOLUME /tmp
ENV MQ_HOST=
ENV MQ_USER_NAME=
ENV MQ_PASSWORD=
COPY target/eks-mq-demo.jar eks-mq-demo.jar
ENTRYPOINT ["java","-Dspring.rabbitmq.host=${MQ_HOST}","-Dspring.rabbitmq.username=${MQ_USER_NAME}","-Dspring.rabbitmq.password=${MQ_PASSWORD}","-jar","eks-mq-demo.jar"]