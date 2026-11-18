# Backend MoSeguros. Imagens fixadas e listadas em scripts/images.txt.
# Contexto de build: a raiz do repositório (reactor Maven, S5-06), ex.:
#   docker build -f backend/Dockerfile .
# (no compose: context ".." e dockerfile "backend/Dockerfile"; exclusões no .dockerignore da raiz).

# --- build ---
FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Remove CR caso o repositório tenha sido clonado no Windows sem respeitar o .gitattributes.
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY payment-contracts/ payment-contracts/
# O pom.xml da raiz lista também o payment-file-worker (S5-09): só o pom, não é construído com -pl.
COPY payment-file-worker/pom.xml payment-file-worker/pom.xml
COPY backend/ backend/
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -pl backend -am -DskipTests package \
    && cp backend/target/backend-*.jar app.jar

# --- runtime ---
FROM eclipse-temurin:17-jre

# Pasta dos ficheiros (S2-01): o volume "files" do compose monta-a e herda o dono app na 1.ª montagem.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 10001 app \
    && useradd --system --uid 10001 --gid app --no-create-home --shell /usr/sbin/nologin app \
    && mkdir -p /var/lib/moseguros/files \
    && chown app:app /var/lib/moseguros/files

WORKDIR /app
COPY --from=build --chown=app:app /workspace/app.jar app.jar

USER 10001:10001

ENV JAVA_OPTS=""
EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --retries=12 --start-period=20s \
    CMD curl -fsS http://localhost:8080/api/v1/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
