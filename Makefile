# Variables
GRADLEW = gradlew.bat
BUILD_DIR = build
MAIN_CLASS = org.example.HuskSheetsRunner
DOCKER_IMAGE = husksheets-image

# Default target
.PHONY: all
all: create-build-dir build

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

# Run tests using Gradle
.PHONY: test
test:
	$(GRADLEW) test

# Create build directory if it doesn't exist
.PHONY: create-build-dir
create-build-dir:
	@if not exist $(BUILD_DIR) mkdir $(BUILD_DIR)

# Docker build
.PHONY: docker-build
docker-build: build
	docker build -t $(DOCKER_IMAGE) .

# Docker run with command-line arguments
.PHONY: docker-run
docker-run:
	docker run -e USERNAME=$(USERNAME) -e PASSWORD=$(PASSWORD) $(DOCKER_IMAGE)