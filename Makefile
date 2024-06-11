# Variables
GRADLEW = gradlew.bat
BUILD_DIR = build
MAIN_CLASS = org.example.HuskSheetsRunner
DOCKER_IMAGE = husksheets-image

# Default target
.PHONY: all
all: build

# Use Gradle to build the project
.PHONY: build
build:
	$(GRADLEW) build

# Clean up build files using Gradle
.PHONY: clean
clean:
	$(GRADLEW) clean

# Run the main class using Gradle
.PHONY: run
run:
	$(GRADLEW) bootRun

# Create build directory if it doesn't exist
$(BUILD_DIR):
	@if not exist $(BUILD_DIR) mkdir $(BUILD_DIR)

# Docker build
.PHONY: docker-build
docker-build:
	docker build -t $(DOCKER_IMAGE) .

# Docker run with command-line arguments
.PHONY: docker-run
docker-run:
	docker run -e USERNAME=$(USERNAME) -e PASSWORD=$(PASSWORD) $(DOCKER_IMAGE)

# Run the main class with arguments
.PHONY: run-with-args
run-with-args:
	$(GRADLEW) bootRun --args='--local --name="$(USERNAME)" --password="$(PASSWORD)" --verbose --url="https://husksheets.fly.dev" --publisher="team2" --sheet="Sheet1"'
