# 🚀 Automated Regression Test Framework

### 🧪 End-to-End Continuous Testing Platform

A complete automated regression testing system that schedules, executes, and reports tests automatically — integrating **Spring Boot**, **RabbitMQ**, **Selenium**, **REST Assured**, and **H2 Database**.

---

## 📋 Table of Contents

* [Project Overview](#project-overview)
* [Architecture](#system-architecture)
* [Key Features](#key-features)
* [Tech Stack](#technology-stack)
* [Project Modules](#project-structure)
* [Setup & Installation](#setup--installation)
* [Configuration](#configuration)
* [Running the Project](#running-the-project)
* [API Endpoints](#api-endpoints)
* [Execution Flow](#complete-execution-flow)
* [Reporting](#reporting-system)
* [Monitoring & Debugging](#monitoring--debugging)
* [Use Cases](#use-cases--applications)
* [Future Enhancements](#future-enhancements)
* [Contributors](#contributors)


---

## Project Overview

### What We Built

A distributed **automated test execution system** capable of:

* Managing, scheduling, and running regression tests
* Performing **UI and API automation**
* Generating **detailed HTML, CSV, and JUnit** reports
* Operating asynchronously via **RabbitMQ messaging**

### Business Value

✅ Reduces manual testing time from hours to minutes
✅ Enables consistent, repeatable test execution
✅ Captures screenshots and logs for failures
✅ Integrates easily into CI/CD pipelines

---

## 🏗️ System Architecture

### Three-Tier Microservices Design

```
┌─────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│  Test Management │    │   Message Queue   │    │   Test Runner     │
│       API        │────│     RabbitMQ      │────│     Worker        │
│  (Spring Boot)   │    │                   │    │  (Spring Boot)    │
└─────────────────┘    └──────────────────┘    └──────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐                                ┌──────────────────┐
│   Database (H2) │                                │   Selenium/REST  │
└─────────────────┘                                └──────────────────┘
```

---

## 🌟 Key Features

* REST API for test scheduling and management
* Message-driven job distribution with RabbitMQ
* Real browser automation (Selenium WebDriver)
* REST API testing (REST Assured)
* Automatic screenshot capture on failures
* HTML, CSV, and JUnit report generation
* Database persistence (H2)
* Parallel worker execution

---

## 🛠️ Technology Stack

| Component             | Technology         | Purpose                         |
| --------------------- | ------------------ | ------------------------------- |
| **Backend Framework** | Spring Boot 3      | Microservice foundation         |
| **Build Tool**        | Maven              | Build and dependency management |
| **Message Broker**    | RabbitMQ           | Reliable messaging              |
| **Database**          | H2                 | Embedded test data storage      |
| **UI Testing**        | Selenium WebDriver | Real browser automation         |
| **API Testing**       | REST Assured       | REST API testing                |
| **Testing Framework** | TestNG             | Test organization               |
| **Logging**           | SLF4J + Logback    | Application logging             |
| **Serialization**     | Jackson            | JSON handling                   |

---

## 🗂️ Project Structure

```
automated-regression-framework/
│
├── pom.xml                           # 🔸 Root parent POM (manages all submodules)
├── .gitignore
├── .gitattributes
├── docker-compose.yml                # 🐳 Spin up RabbitMQ container
├── mvnw / mvnw.cmd                   # Maven wrapper scripts
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
│
├── shared-models/                    # 📦 Shared data models between API & Worker
│   ├── pom.xml
│   └── src/main/java/models/
│       ├── TestExecutionJob.java     # Represents a full test job
│       ├── TestResult.java           # Individual test result
│       ├── TestRunRequest.java       # Request body for /api/runs
│       └── TestRunResponse.java      # Response from /api/runs
│
├── test-management-api/              # 🧠 API Microservice (Command Center)
│   ├── pom.xml
│   ├── Dockerfile
│   ├── logs/
│   ├── data/                         # 📂 H2 database files
│   │   ├── testruns.mv.db
│   │   └── testruns.trace.db
│   └── src/
│       ├── main/java/api/
│       │   ├── config/
│       │   │   └── RabbitMQConfig.java         # RabbitMQ queue setup
│       │   ├── controller/
│       │   │   └── TestRunController.java      # REST API endpoints
│       │   ├── entity/
│       │   │   └── TestRunEntity.java          # JPA Entity for DB table
│       │   ├── listener/
│       │   │   └── TestResultsListener.java    # Consumes worker results
│       │   ├── repository/
│       │   │   └── TestRunRepository.java      # JPA Repository
│       │   ├── service/
│       │   │   └── TestRunService.java         # Core business logic
│       │   └── TestManagementApiApplication.java # Main Spring Boot app
│       └── resources/
│           ├── application.properties          # H2 + RabbitMQ configs
│           └── static/                         # (Optional frontend)
│
├── test-runner-worker/              # ⚙️ Worker Microservice (Execution Engine)
│   ├── pom.xml
│   ├── Dockerfile
│   ├── logs/
│   ├── reports/                     # 📊 HTML, CSV, and JUnit XML reports
│   │   └── test_report_RUN_xxx.html
│   ├── screenshots/                 # 🖼️ Failure screenshots
│   │   └── Google_Failure_xxx.png
│   └── src/main/java/com/regression/framework/worker/
│       ├── TestRunnerWorkerApplication.java    # Main Spring Boot class
│       ├── listener/
│       │   └── WorkerListener.java             # RabbitMQ message listener
│       ├── service/
│       │   ├── TestExecutor.java               # Core test execution engine
│       │   ├── ReportGenerator.java            # HTML/CSV/JUnit report generation
│       │   └── ScreenshotService.java          # Handles screenshot capture
│       └── config/
│           └── RabbitMQConfig.java             # Worker-side queue config
│
│   └── src/main/resources/
│       └── application.yml                     # Worker configuration
│
└── chromedriver.exe                            # Chrome WebDriver for browser automation

```

---

## ⚙️ Setup & Installation

### 1️⃣ Prerequisites

* Java 17+
* Maven 3.8+
* Docker (for RabbitMQ)
* Browser (Chrome/Edge) + WebDriver
* Git

### 2️⃣ Clone Repository

```bash
git clone https://github.com/your-org/automated-regression-framework.git
cd automated-regression-framework
```

### 3️⃣ Start RabbitMQ (via Docker)

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
```

RabbitMQ UI → [http://localhost:15672](http://localhost:15672)
**Credentials:** `guest / guest`

### 4️⃣ Build All Modules

```bash
mvn clean install -DskipTests
```

---

## ⚙️ Configuration

### API (`test-management-api/src/main/resources/application.yml`)

```yaml
server:
  port: 8080
spring:
  rabbitmq:
    host: localhost
    port: 5672
  datasource:
    url: jdbc:h2:file:./data/testruns
    driverClassName: org.h2.Driver
    username: sa
    password: password
```

### Worker (`test-runner-worker/src/main/resources/application.yml`)

```yaml
server:
  port: 8081
spring:
  rabbitmq:
    host: localhost
    port: 5672
```

---

## ▶️ Running the Project

### Start the API Service

```bash
cd test-management-api
mvn spring-boot:run
```

Access: [http://localhost:8080](http://localhost:8080)

### Start the Worker Service

```bash
cd test-runner-worker
mvn spring-boot:run
```

---

## 📡 API Endpoints

| Method   | Endpoint              | Description             |
| -------- | --------------------- | ----------------------- |
| **POST** | `/api/runs`           | Schedule a new test run |
| **GET**  | `/api/runs`           | Get all test runs       |
| **GET**  | `/api/runs/{id}`      | Get test run status     |
| **POST** | `/api/runs/{id}/stop` | Stop an ongoing test    |

### Example Request

```bash

Use this -> # Test 1 - Web tests
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/runs" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{
    "testSuiteName": "SmokeTest",
    "executionType": "WEB",
    "tags": ["smoke"],
    "retryCount": 1,
    "browserType": "chrome"
  }'

#OR

curl -X POST http://localhost:8080/api/runs \
-H "Content-Type: application/json" \
-d '{
  "testSuiteName": "SmokeTest",
  "executionType": "WEB"
}'
```

---

## 🔄 Complete Execution Flow

1. User sends POST `/api/runs`
2. API stores test run in database (`SCHEDULED`)
3. API publishes message to `test-execution-queue`
4. Worker consumes message and executes tests
5. Worker captures screenshots and generates reports
6. Worker sends results back via `test-results-queue`
7. API updates database to `COMPLETED`
8. Reports available in `/reports/` folder

---

## 📊 Reporting System

| Format        | Description                       | Location         |
| ------------- | --------------------------------- | ---------------- |
| **HTML**      | Visual dashboard with screenshots | `/reports/html/` |
| **CSV**       | Raw machine-readable data         | `/reports/csv/`  |
| **JUnit XML** | CI/CD compatible format           | `/reports/xml/`  |

All reports include:

* Execution summary
* Pass/fail stats
* Duration metrics
* Screenshot links for failures

---

## 🧭 Monitoring & Debugging

### 🔍 Logs

* API Logs → `logs/api.log`
* Worker Logs → `logs/worker.log`

### 🐇 RabbitMQ Dashboard

* [http://localhost:15672](http://localhost:15672)
* Monitor queue depth and job flow

### 🗄️ H2 Database Console

* [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* JDBC URL: `jdbc:h2:file:./data/testruns`
* User: `sa` | Password: `password`

---

## 💼 Use Cases & Applications

| Use Case                      | Description                               |
| ----------------------------- | ----------------------------------------- |
| **Regression Testing**        | Automated revalidation after code changes |
| **Continuous Integration**    | Runs automatically in CI/CD               |
| **Cross-Browser Testing**     | Supports multiple browsers                |
| **Service Health Monitoring** | Scheduled API checks                      |

---

## 🏆 Achievements

✅ Complete microservices-based testing framework
✅ Message-driven architecture with RabbitMQ
✅ Multi-format professional reports
✅ Integrated UI + API test automation
✅ Scalable and modular codebase

---

## 🚀 Future Enhancements

| Timeline       | Planned Features                                            |
| -------------- | ----------------------------------------------------------- |
| **Short-Term** | MySQL/PostgreSQL support, enhanced dashboards, email alerts |
| **Mid-Term**   | Kubernetes deployments, performance benchmarking            |
| **Long-Term**  | AI-driven test case generation, self-healing automation     |

---

## 👥 Contributors

| Name                 | Role                          |
| ---------------------|------------------------------ |
| *Mallikarjun Mandava*| Intern                        |


---

## 💬 Support

For help, issues, or feature requests, please open an issue on GitHub or contact the maintainers.

---

### ✅ Quick Start Summary

```bash
# 1. Clone repo
git clone https://github.com/your-org/automated-regression-framework.git

# 2. Start RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# 3. Start API + Worker
cd test-management-api && mvn spring-boot:run
cd ../test-runner-worker && mvn spring-boot:run

# 4. Trigger a run
# Schedule test run
Invoke-RestMethod -Uri "http://localhost:8080/api/runs" -Method Post -ContentType "application/json" -Body '{"testSuiteName": "SmokeTest", "executionType": "WEB"}'

# Test 1 - Web tests
Invoke-RestMethod -Uri "http://localhost:8080/api/runs" -Method Post -ContentType "application/json" -Body '{"testSuiteName": "SmokeTest", "executionType": "WEB", "tags": ["smoke"], "retryCount": 1, "browserType": "chrome"}'

# Test 2 - Web tests  
Invoke-RestMethod -Uri "http://localhost:8080/api/runs" -Method Post -ContentType "application/json" -Body '{"testSuiteName": "RegressionTest", "executionType": "WEB", "tags": ["regression"], "retryCount": 1, "browserType": "chrome"}'

# Test 3 - API tests
Invoke-RestMethod -Uri "http://localhost:8080/api/runs" -Method Post -ContentType "application/json" -Body '{"testSuiteName": "APITest", "executionType": "API", "tags": ["api"], "retryCount": 1, "browserType": "chrome"}'

# OR
curl -X POST http://localhost:8080/api/runs \
-H "Content-Type: application/json" \
-d '{"testSuiteName":"SmokeTest","executionType":"WEB"}'
```

---
