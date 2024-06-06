# Variables
JAVAC = javac
JAVA = java
SRC_DIR = app/src
BIN_DIR = app/bin
MAIN_CLASS = org.example.HuskSheetsRunner

# Source files
SOURCES := $(shell find $(SRC_DIR) -name "*.java")
# Object files
CLASSES := $(SOURCES:$(SRC_DIR)/%.java=$(BIN_DIR)/%.class)

# Default target
.PHONY: all
all: $(BIN_DIR) $(CLASSES)

# Rule to compile Java files
$(BIN_DIR)/%.class: $(SRC_DIR)/%.java
	@mkdir -p $(dir $@)
	$(JAVAC) -d $(BIN_DIR) $<

# Clean up build files
.PHONY: clean
clean:
	@if exist $(BIN_DIR) (rmdir /s /q $(BIN_DIR))

# Run the main class
.PHONY: run
run: all
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS)

# Create bin directory if it doesn't exist
$(BIN_DIR):
	@mkdir -p $(BIN_DIR)
