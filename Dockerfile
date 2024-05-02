# FROM docker.io/alibabadragonwell/dragonwell:17-anolis
FROM registry.jihulab.com/xuxiaowei-jihu/xuxiaowei-cloud/spring-cloud-xuxiaowei/alibabadragonwell/dragonwell:17-anolis

WORKDIR /home

ARG GROUP_ID=${GROUP_ID}
ARG ARTIFACT_ID=${ARTIFACT_ID}
ARG VERSION=${VERSION}
ARG CI_PIPELINE_URL=${CI_PIPELINE_URL}
ARG CI_JOB_ID=${CI_JOB_ID}
ARG JAVA_OPTS=${JAVA_OPTS}
ARG APP_ARGS=${APP_ARGS}

RUN printf "GROUP_ID: %s\n" "$GROUP_ID" \
    && printf "ARTIFACT_ID: %s\n" "$ARTIFACT_ID" \
    && printf "VERSION: %s\n" "$VERSION" \
    && printf "CI_PIPELINE_URL: %s\n" "$CI_PIPELINE_URL" \
    && printf "CI_JOB_ID: %s\n" "$CI_JOB_ID" \
    && printf "JAVA_OPTS: %s\n" "$JAVA_OPTS" \
    && printf "APP_ARGS: %s\n" "$APP_ARGS"

COPY gateway/target/$ARTIFACT_ID-$VERSION.jar app.jar

EXPOSE 45450
EXPOSE 45455

ENV TZ=Asia/Shanghai \
    LANG=C.UTF-8 \
    GROUP_ID=${GROUP_ID} \
    ARTIFACT_ID=${ARTIFACT_ID} \
    VERSION=${VERSION} \
    CI_PIPELINE_URL=${CI_PIPELINE_URL} \
    CI_JOB_ID=${CI_JOB_ID} \
    JAVA_OPTS=${JAVA_OPTS} \
    APP_ARGS=${APP_ARGS}

CMD java $JAVA_OPTS -jar app.jar $APP_ARGS
