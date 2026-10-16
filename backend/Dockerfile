# ---- Compilación ----
# Se usa el Maven de la imagen: .mvn/ no está en git, así que ./mvnw no funciona en un clon limpio.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
# Las pruebas usan un MySQL local; se corren fuera de Docker con ./mvnw test
RUN --mount=type=cache,target=/root/.m2 mvn -B -q package -Dmaven.test.skip=true

# ---- Ejecución ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Las asistencias usan LocalDate.now(): sin esto el contenedor corre en UTC y cambia de día a las 5 p.m.
ENV TZ=America/Hermosillo

RUN groupadd --system app && useradd --system --gid app app \
    && mkdir -p /app/keys && chown app:app /app/keys
COPY --from=build /app/target/*.jar app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
