# STAGE 1: Build the App
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# STAGE 2: Run the App
FROM eclipse-temurin:21-jdk
WORKDIR /app

# --- FIX 1: INSTALL PYTHON & LIBRARIES ---
# We update Linux, install Python3, Pip, and required system libraries for OpenCV
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# --- FIX 2: INSTALL PYTHON PACKAGES ---
# We use --break-system-packages because modern Linux requires it for global pip installs
RUN pip3 install numpy opencv-python-headless --break-system-packages

# --- FIX 3: COPY RESOURCES SO JAVA CAN FIND THEM ---
# Your Java code looks in "src/main/resources/python/..."
# In a Docker container, that folder doesn't exist automatically. We must copy it manually.
COPY --from=build /app/src/main/resources /app/src/main/resources

# Copy the JAR file
COPY --from=build /app/target/*.jar app.jar

# Expose Port
EXPOSE 8080

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]
