FROM bellsoft/liberica-runtime-container:jre-21-glibc

RUN apk update && apk upgrade && apk add --no-cache tzdata

RUN addgroup appuser && \
    adduser -G appuser -D appuser

WORKDIR /opt/kvalitetsit
COPY /maven/application.cdx.json ./

WORKDIR /home/appuser
COPY entrypoint.sh .

COPY config/logback.xml logback-spring.xml
COPY /maven/web-exec.jar web.jar

EXPOSE 8080
EXPOSE 8081

USER appuser
ENTRYPOINT ["/home/appuser/entrypoint.sh"]
