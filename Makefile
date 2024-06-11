# Variables
GRADLEW = gradlew.bat
BUILD_DIR = build
MAIN_CLASS = org.example.HuskSheetsRunner

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
