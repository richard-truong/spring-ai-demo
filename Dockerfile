# Build stage
FROM gradle:9.5.1-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle :app:bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime
RUN addgroup -S app && adduser -S app -G app
USER app
WORKDIR /app
COPY --from=build /workspace/app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
