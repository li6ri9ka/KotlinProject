FROM gradle:8.8-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle :app:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/app/build/install/app/ /app/
EXPOSE 8080
CMD ["/app/bin/app"]
