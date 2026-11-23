# payment-file-worker MoSeguros (S5-09). Imagens fixadas e listadas em scripts/images.txt.
# Contexto de build: a raiz do repositório (reactor Maven, S5-06), ex.:
#   docker build -f payment-file-worker/Dockerfile .
# (no compose: context ".." e dockerfile "payment-file-worker/Dockerfile"; exclusões no .dockerignore da raiz).

# --- build ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Remove CR caso o repositório tenha sido clonado no Windows sem respeitar o .gitattributes.
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# O pom.xml da raiz lista os três módulos: o backend só entra pelo pom (não é construído com -pl).
COPY payment-contracts/ payment-contracts/
COPY backend/pom.xml backend/pom.xml
COPY payment-file-worker/ payment-file-worker/
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    ./mvnw -B -pl payment-file-worker -am -DskipTests package \
    && cp payment-file-worker/target/payment-file-worker-*.jar app.jar

# --- runtime ---
FROM eclipse-temurin:17-jre

# Pasta de trabalho (ficheiros descarregados, apagados no fim de cada leitura).
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid app --no-create-home --shell /usr/sbin/nologin app \
    && mkdir -p /var/lib/payment-file-worker \
    && chown app:app /var/lib/payment-file-worker

WORKDIR /app
COPY --from=build --chown=app:app /workspace/app.jar app.jar

USER 10001:10001

ENV JAVA_OPTS=""
ENV WORKER_WORK_DIR=/var/lib/payment-file-worker
EXPOSE 8081

HEALTHCHECK --interval=10s --timeout=5s --retries=12 --start-period=20s \
    CMD curl -fsS http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
