# Backend MoSeguros. Imagens fixadas e listadas em scripts/images.txt.

# --- build ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Remove CR caso o repositório tenha sido clonado no Windows sem respeitar o .gitattributes.
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests package \
    && cp target/*.jar app.jar

# --- runtime ---
FROM eclipse-temurin:17-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid app --no-create-home --shell /usr/sbin/nologin app

WORKDIR /app
COPY --from=build --chown=app:app /workspace/app.jar app.jar

USER 10001:10001

ENV JAVA_OPTS=""
EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --retries=12 --start-period=20s \
    CMD curl -fsS http://localhost:8080/api/v1/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
