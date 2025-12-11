# STAGE 1: Build the App
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# STAGE 2: Run the App
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 1. Install Python 3 and System Libraries (Required for OpenCV)
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender-dev \
    && rm -rf /var/lib/apt/lists/*

# 2. Install Python Libraries (Headless = No GUI, prevents crashes)
RUN pip3 install numpy opencv-python-headless --break-system-packages

# 3. CRITICAL FIX: Recreate the 'src' folder structure
# We explicitly create the folders Java expects
RUN mkdir -p /app/src/main/resources/python
RUN mkdir -p /app/src/main/resources/static/images

# 4. Copy the specific files from the build stage to these folders
# Note: We copy from the source context in Stage 1
COPY --from=build /app/src/main/resources/python /app/src/main/resources/python
COPY --from=build /app/src/main/resources/static/images /app/src/main/resources/static/images

# 5. Copy the Application JAR
COPY --from=build /app/target/*.jar app.jar

# 6. Make sure Python script is executable
RUN chmod +x /app/src/main/resources/python/virtual_tryon.py

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
