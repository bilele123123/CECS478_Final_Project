# Makefile for Security-Focused WebSocket Service
# CECS 478 Final Project - "Simple Web Service to Hardened Service"
# Authors: Thai Le & Brian Ho

PROJECT_NAME = Final_Project_Proposal
SERVER_DIR = server
JAR = $(SERVER_DIR)/target/final-0.0.1-SNAPSHOT.jar
DC = docker compose

.DEFAULT_GOAL := help

.PHONY: help up-windows demo-windows up-linux demo-linux build-windows build-linux clean

help:
	@echo "Usage:"
	@echo "  make up-windows && make demo-windows"
	@echo "  make up-linux   && make demo-linux"
	@echo ""
	@echo "Targets:"
	@echo "  up-windows/demo-windows  - Build & launch + demo on Windows"
	@echo "  up-linux/demo-linux      - Build & launch + demo on Unix/Linux"
	@echo "  clean                    - Stop containers & remove artifacts"

# -----------------------
# --- WINDOWS TARGETS ---
# -----------------------
up-windows: build-windows
	@echo ""
	@echo "Starting Docker services (Windows)..."
	$(DC) up -d --build chat-server pcap-capture
	@echo "Waiting for chat-server TLS initialization (10s)..."
	@powershell -Command "Start-Sleep -Seconds 10"
	@echo "Services are running."

build-windows:
	@echo "Building Spring Boot server (Windows)..."
	$(SERVER_DIR)\mvnw.cmd -f $(SERVER_DIR)\pom.xml clean package -DskipTests
	@echo "Build complete."

demo-windows:
	@echo "Running demo-runner container (Windows)..."
	$(DC) run --rm --service-ports demo-runner
	@echo "Waiting for logs to flush..."
	@powershell -Command "Start-Sleep -Seconds 3"
	@echo "Stopping pcap-capture..."
	$(DC) stop pcap-capture || true
	@echo "Exporting metrics..."
	@curl -sk https://localhost:8443/metrics/all -o evidence/metrics/metrics.json || true
	@echo "Demo complete."

# -----------------------
# --- LINUX/UNIX TARGETS ---
# -----------------------
up-linux: build-linux
	@echo ""
	@echo "Starting Docker services (Linux)..."
	$(DC) up -d --build chat-server pcap-capture
	@echo "Waiting for chat-server TLS initialization (10s)..."
	@sleep 10
	@echo "Services are running."

build-linux:
	@echo "Building Spring Boot server (Linux)..."
	./$(SERVER_DIR)/mvnw -f $(SERVER_DIR)/pom.xml clean package -DskipTests
	@echo "Build complete."

demo-linux:
	@echo "Running demo-runner container (Linux)..."
	$(DC) run --rm --service-ports demo-runner
	@echo "Waiting for logs to flush..."
	@sleep 3
	@echo "Stopping pcap-capture..."
	$(DC) stop pcap-capture || true
	@echo "Exporting metrics..."
	@curl -sk https://localhost:8443/metrics/all -o evidence/metrics/metrics.json || true
	@echo "Demo complete."

# -----------------------
# --- CLEANUP (CROSS-PLATFORM)
# -----------------------
clean:
	@echo "Stopping and removing containers..."
	$(DC) down -v || true
	@echo "Removing build artifacts..."
	rm -rf $(SERVER_DIR)/target
	rm -f $(SERVER_DIR)/demo-keystore.p12
	@echo "Cleanup complete."
