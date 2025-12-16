#!/bin/bash
# Quick Start Script for Review API Backend

echo "================================"
echo " REVIEW API BACKEND QUICK START"
echo "================================"
echo ""

echo "[1/4] Checking Java version..."
java -version
if [ $? -ne 0 ]; then
    echo "ERROR: Java not found! Please install Java 17+"
    exit 1
fi
echo ""

echo "[2/4] Building Maven project..."
cd backend
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Maven build failed!"
    exit 1
fi
echo ""

echo "[3/4] Starting Spring Boot Application..."
echo "Server will run at http://localhost:8080"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

mvn spring-boot:run
