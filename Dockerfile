FROM openjdk:17-jdk

# JAR 파일을 컨테이너에 복사
COPY build/libs/study-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

# 애플리케이션 실행
CMD ["java", "-jar", "study-0.0.1-SNAPSHOT.jar"]