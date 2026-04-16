
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app


RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender-dev \
    && rm -rf /var/lib/apt/lists/*


RUN pip3 install numpy opencv-python-headless --break-system-packages

RUN mkdir -p /app/src/main/resources/python
RUN mkdir -p /app/src/main/resources/static/images



COPY --from=build /app/src/main/resources/python /app/src/main/resources/python
COPY --from=build /app/src/main/resources/static/images /app/src/main/resources/static/images


COPY --from=build /app/target/*.jar app.jar


RUN chmod +x /app/src/main/resources/python/virtual_tryon.py

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
