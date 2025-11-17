# Makefile for Security-Focused WebSocket Service
# CECS478 Final Project - "Simple Web Service to Hardened Service"
# Authors: Thai Le & Brian Ho

PROJECT_NAME = Final_Project_Proposal
DOCKER_COMPOSE = docker-compose

# Default target
.DEFAULT_GOAL := help

## bootstrap: Set up the environment, build images, and start the containers
bootstrap:
	@echo "Bootstrapping $(PROJECT_NAME)..."
	$(DOCKER_COMPOSE) build
	$(DOCKER_COMPOSE) up -d
	@echo "Containers are up and running."

## up: Start existing containers (if already built)
up:
	$(DOCKER_COMPOSE) up -d

## down: Stop all running containers
down:
	$(DOCKER_COMPOSE) down

## logs: View logs from all services
logs:
	$(DOCKER_COMPOSE) logs -f

## ps: List running containers
ps:
	$(DOCKER_COMPOSE) ps

## rebuild: Force rebuild images without cache
rebuild:
	$(DOCKER_COMPOSE) build --no-cache
	$(DOCKER_COMPOSE) up -d

## clean: Stop containers and remove all images/volumes
clean:
	$(DOCKER_COMPOSE) down --rmi all --volumes --remove-orphans
	@echo "Cleaned up containers, images, and volumes."

## pcap: Capture Docker network traffic for analysis (controlled use only)
pcap:
	@echo "Capturing traffic for internal analysis..."
	sudo docker exec -it $$(docker ps -qf "name=$(PROJECT_NAME)_server") \
		tcpdump -i eth0 -w /tmp/ws_traffic.pcap

## help: Show available commands
help:
	@echo "Available make commands:"
	@grep -E '^##' Makefile | sed -e 's/## //'
